// Copyright (C) Marcus Kauffman 2023-Present

// This work would not have been possible without the work of many
// contributors, most notably Colin Montigel. See ACKNOWLEDGEMENT.md for
// more details.

// This file is part of Quail.

// Quail is free software: you can redistribute it and/or modify it
// underthe terms of the GNU General Public License as published by the
// Free Software Foundation, version 3.

// Quail is distributed in the hope that it will be useful, but WITHOUT
// ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
// FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License
// for more details.

// You should have received a copy of the GNU General Public License
// along with Quail. If not, see <https://www.gnu.org/licenses/>

package com.mineinjava.quail.util.geometry;

import com.mineinjava.quail.util.MathUtil;
import java.util.Objects;

/** 
 * Represents a two-dimensional position and heading.
 * ALL angles MUST be in ccw+ radians 
 */
public class Pose2d {
  public final double x;
  public final double y;
  public final double heading;

  /** 
   * Returns zero pose.
   *
   * <p> (x, y, and heading all are zero)
   */
  public Pose2d() {
    this(0.0, 0.0, 0.0);
  }

  /**
   * @param x x position
   * @param y y position
   * @param heading heading
   */
  public Pose2d(double x, double y, double heading) {
    this.x = x;
    this.y = y;
    this.heading = heading;
  }

  /**
   * Useful for conversion from vector.
   *
   * @param pos vector representing the x and y position
   * @param heading heading
   */
  public Pose2d(Vec2d pos, double heading) {
    this(pos.x, pos.y, heading);
  }

  /**
   * Converts x and y position to a vector Does not include heading.
   *
   * @return vector representing the x and y position
   */
  public Vec2d vec() {
    return new Vec2d(x, y);
  }

  /**
   * Converts the heading to a vector.
   *
   * @return unit vector (length=1) with angle matching {@code heading}
   */
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
    return MathUtil.epsilonEquals(x, other.x)
        && MathUtil.epsilonEquals(y, other.y)
        && MathUtil.epsilonEquals(heading, other.heading);
  }

  public boolean epsilonEqualsHeading(Pose2d other) {
    return MathUtil.epsilonEquals(x, other.x)
        && MathUtil.epsilonEquals(y, other.y)
        && MathUtil.epsilonEquals(Angle.normDelta(heading - other.heading), 0.0);
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    Pose2d pose2d = (Pose2d) o;
    return Double.compare(pose2d.x, x) == 0
        && Double.compare(pose2d.y, y) == 0
        && Double.compare(pose2d.heading, heading) == 0;
  }

  public double distanceTo(Pose2d other) {
    return Math.sqrt(Math.pow(other.x - x, 2) + Math.pow(other.y - y, 2));
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
   * Returns a pose from the given list.
   *
   * @param list double[] in the format [x, y, heading]
   * @return
   */
  public Pose2d fromList(double[] list) {
    return new Pose2d(list[0], list[1], list[2]);
  }

  /**
   * Detects if the circle with (@code radius) is "hit" by a straight line between (@code robotPose)
   * and (@code oldRobotPose).
   *
   * @param radius circle radius for hit detection
   * @param robotPose position one for hit detection
   * @param oldRobotPose position two for hit detection
   * @return Boolean whether the pose it "hit" or not
   */
  public boolean isHit(double radius, Pose2d robotPose, Pose2d oldRobotPose) {
    if (this.distanceTo(robotPose) < radius) {
      return true;
    } else {
      return MathUtil.LineSegHitCircle(robotPose, oldRobotPose, this, radius);
    }
  }

  /**
   * Returns a vector from this Pose2d to another Pose2d
   */
  public Vec2d vectorTo(Pose2d other) {
    return other.vec().subtract(vec());
  }
}
