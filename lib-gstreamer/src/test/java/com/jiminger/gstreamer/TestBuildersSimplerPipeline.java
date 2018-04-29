package com.jiminger.gstreamer;

import static com.jiminger.gstreamer.util.GstUtils.instrument;
import static net.dempsy.utils.test.ConditionPoll.poll;
import static org.junit.Assert.assertTrue;

import org.freedesktop.gstreamer.Gst;
import org.freedesktop.gstreamer.Pipeline;
import org.freedesktop.gstreamer.elements.DecodeBin;
import org.freedesktop.gstreamer.event.EOSEvent;
import org.junit.Test;

import com.jiminger.gstreamer.guard.GstScope;
import com.jiminger.gstreamer.util.GstUtils;

public class TestBuildersSimplerPipeline extends BaseTest {

    @Test
    public void testSimplePipeline() throws Exception {
        try (final GstScope m = new GstScope(TestBuildersSimplerPipeline.class);) {

            final Pipeline pipe = new BinBuilder()
                    .make("filesrc").with("location", STREAM.getPath())
                    .delayed(new DecodeBin("source"))
                    .make("fakesink").with("sync", "true")
                    .buildPipeline(m);

            instrument(pipe);
            pipe.play();
            Thread.sleep(2000);

            GstUtils.printDetails(pipe);

            pipe.sendEvent(new EOSEvent());

            assertTrue(poll(o -> !pipe.isPlaying()));

            final Thread t = new Thread(() -> Gst.main());
            t.setDaemon(true);
            t.start();

            poll(o -> !t.isAlive());
        }
    }

}
