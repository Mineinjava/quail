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

package com.mineinjava.quail.util;

import com.mineinjava.quail.util.geometry.Pose2d;
import com.mineinjava.quail.util.geometry.Vec2d;

import java.util.ArrayList;
import java.util.List;

public class MathUtil {
  public static boolean epsilonEquals(double value1, double value2) {
    double epsilon = 1e-6; // Adjust the epsilon value as needed
    return Math.abs(value1 - value2) < epsilon;
  }

  /**
   * Detects if a line segment defined by two positions will intersect a circle with given center
   * and radius Note that the headings for all three Pose2d arguments are not used
   * https://stackoverflow.com/a/1084899/13224997
   */
  public static boolean LineSegHitCircle(
      Pose2d lineSegStart, Pose2d lineSegEnd, Pose2d circleCenter, double circleRadius) {

    Vec2d d = lineSegEnd.vec().subtract(lineSegStart.vec());
    Vec2d f = lineSegStart.vec().subtract(circleCenter.vec());

    double a = d.dot(d);
    if (a == 0.0) {
      return false;
    }
    double b = 2 * f.dot(d);
    double c = f.dot(f) - (circleRadius * circleRadius);

    double discriminant = (b * b) - (4 * a * c);
    if (discriminant < 0) {
      return false;
    }
    discriminant = Math.sqrt(discriminant);
    double t1 = (-b - discriminant) / (2 * a);
    double t2 = (-b + discriminant) / (2 * a);

    return ((0d <= t1 && t1 <= 1d) && (0d <= t2 && t2 <= 1d));
  }

  /**
   * basic lerp function
   *
   * @return value between a and b at t
   */
  public static double lerp(double a, double b, double t) {
    return a + (b - a) * t;
  }

    public static double floormod(double a, double b) {
      return a - (b * Math.floor(a / b));
    }

    /**
     * Calculates the smallest angle between two angles Useful for determining how far and in which
     * direction to rotate anything angle returned is in radians, counterclockwise from angle 1 to
     * angle 2
     *
     * @param angle1
     * @param angle2
     * @return
     */
    public static double deltaAngle(double angle1, double angle2) {
      // calculate the smallest angle to rotate between the current angle and the target angle
      double deltaAngle = angle2 - angle1;
      deltaAngle = floormod(deltaAngle + Math.PI, 2 * Math.PI) - Math.PI;
      return deltaAngle % (2 * Math.PI);
    }

    /**
     * clamps a value between a min and a max
     *
     * @param value
     * @param min
     * @param max
     * @return
     */
    public static double clamp(double value, double min, double max) {
      // clamp a value between a minimum and maximum
      if (value < min) {
        return min;
      } else if (value > max) {
        return max;
      } else {
        return value;
      }
    }

    /** returns a list of all the pairs that can be formed from a list */
    public static <T> List<List<T>> getPairs(T[] inputArray) {
      List<List<T>> pairs = new ArrayList<>();
      for (int i = 0; i < inputArray.length; i++) {
        for (int j = i + 1; j < inputArray.length; j++) {
          List<T> pair = new ArrayList<>();
          pair.add(inputArray[i]);
          pair.add(inputArray[j]);
          pairs.add(pair);
        }
      }
      return pairs;
    }
}
