package com.mineinjava.quail.util.geometry;

import java.util.Objects;

import com.mineinjava.quail.util.MathUtil;

public class Pose2d {
    public final double x;
    public final double y;
    public final double heading;

    public Pose2d() {
        this(0.0, 0.0, 0.0);
    }

    public Pose2d(double x, double y, double heading) {
        this.x = x;
        this.y = y;
        this.heading = heading;
    }

    public Pose2d(Vec2d pos, double heading) {
        this(pos.x, pos.y, heading);
    }

    public Vec2d vec() {
        return new Vec2d(x, y);
    }

    public Vec2d headingVec() {
        return new Vec2d(Math.cos(heading), Math.sin(heading));
    }

    public Pose2d plus(Pose2d other) {
        return new Pose2d(x + other.x, y + other.y, heading + other.heading);
    }

    public Pose2d minus(Pose2d other) {
        return new Pose2d(x - other.x, y - other.y, heading - other.heading);
    }

    public Pose2d times(double scalar) {
        return new Pose2d(scalar * x, scalar * y, scalar * heading);
    }

    public Pose2d div(double scalar) {
        return new Pose2d(x / scalar, y / scalar, heading / scalar);
    }

    public Pose2d unaryMinus() {
        return new Pose2d(-x, -y, -heading);
    }

    public boolean epsilonEquals(Pose2d other) {
        return MathUtil.epsilonEquals(x, other.x) &&
               MathUtil.epsilonEquals(y, other.y) &&
               MathUtil.epsilonEquals(heading, other.heading);
    }

    public boolean epsilonEqualsHeading(Pose2d other) {
        return MathUtil.epsilonEquals(x, other.x) &&
               MathUtil.epsilonEquals(y, other.y) &&
               MathUtil.epsilonEquals(Angle.normDelta(heading - other.heading), 0.0);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Pose2d pose2d = (Pose2d) o;
        return Double.compare(pose2d.x, x) == 0 &&
               Double.compare(pose2d.y, y) == 0 &&
               Double.compare(pose2d.heading, heading) == 0;
    }

    @Override
    public int hashCode() {
        return Objects.hash(x, y, heading);
    }

    @Override
    public String toString() {
        return String.format("(%.3f, %.3f, %.3f°)", x, y, Math.toDegrees(heading));
    }

    /**
     * Returns a pose from the given list
     * @param list - double[] in the format [x, y, theta]
     * @return
     */
    public Pose2d fromList(double[] list) {
        return new Pose2d(list[0], list[1], list[2]);
    }
}