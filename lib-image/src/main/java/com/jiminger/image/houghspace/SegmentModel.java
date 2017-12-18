package com.jiminger.image.houghspace;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import com.jiminger.image.geometry.LineSegment;
import com.jiminger.image.geometry.Point;
import com.jiminger.image.geometry.SimplePoint;

public class SegmentModel implements Model {

    final private LineSegment[] segments;
    // final private double num;
    // final private double minX;
    // final private double minY;
    // final private double maxX;
    // final private double maxY;
    final private double w;
    final private double h;

    public SegmentModel(final Collection<LineSegment> segments) {
        if (segments == null || segments.size() == 0)
            throw new IllegalArgumentException();

        final LineSegment[] array = segments.stream().toArray(LineSegment[]::new);
        this.segments = array;
        // this.num = this.segments.length;

        final List<Point> points = segments.stream()
                .map(l -> Arrays.asList(l.p1, l.p2))
                .flatMap(ps -> ps.stream())
                .collect(Collectors.toList());

        double minX = Double.POSITIVE_INFINITY;
        double maxX = Double.NEGATIVE_INFINITY;
        double minY = Double.POSITIVE_INFINITY;
        double maxY = Double.NEGATIVE_INFINITY;

        for (final Point p : points) {
            final double x = p.x();
            if (x < minX)
                minX = x;
            if (x > maxX)
                maxX = x;
            final double y = p.y();
            if (y < minY)
                minY = y;
            if (y > maxY)
                maxY = y;
        }

        // this.minX = minX;
        // this.minY = minY;
        // this.maxX = maxX;
        // this.maxY = maxY;

        this.w = maxX - minX;
        this.h = maxY - minY;
    }

    @Override
    public double distance(final double ox, final double oy, final double scale) {
        double minDist = Double.POSITIVE_INFINITY;
        for (final LineSegment seg : segments) {
            final double dist = seg.distance(new SimplePoint(oy, ox));
            if (dist < minDist)
                minDist = dist;
        }
        return minDist;
    }

    @Override
    public byte gradientDirection(final double ox, final double oy) {
        return closest(ox, oy, 1.0).gradientDirection;
    }

    @Override
    public double featureWidth() {
        return w;
    }

    @Override
    public double featureHeight() {
        return h;
    }

    @Override
    public boolean flipYAxis() {
        return false;
    }

    private LineSegment closest(final double ox, final double oy, final double scale) {
        double minDist = Double.POSITIVE_INFINITY;
        LineSegment nearest = null;
        for (final LineSegment seg : segments) {
            final double dist = seg.distance(new SimplePoint(oy, ox));
            if (dist < minDist) {
                minDist = dist;
                nearest = seg;
            }
        }
        return nearest;
    }
}
