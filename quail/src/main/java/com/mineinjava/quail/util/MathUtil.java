package com.mineinjava.quail.util;

import com.mineinjava.quail.util.geometry.Pose2d;

public class MathUtil {

  public static boolean epsilonEquals(double value1, double value2) {
    double epsilon = Constants.EPSILON;
    return Math.abs(value1 - value2) < epsilon;
  }

  /**
   * Detects if a line segment defined by two positions will intersect a circle with given center
   * and radius Note that the headings for all three Pose2d arguments are not used <a
   * href="https://en.wikipedia.org/wiki/Distance_from_a_point_to_a_line#Line_defined_by_two_points">Reference</a>
   */
  public static boolean lineSegHitCircle(
      Pose2d lineSegStart, Pose2d lineSegEnd, Pose2d circleCenter, double circleRadius) {
    // Calculate the distance from a line defined by 2 points to a point formula
    // d = |(x2-x1)(y1-y0) - (x1-x0)(y2-y1)| / sqrt((x2-x1)^2 + (y2-y1)^2)
    double d =
        Math.abs(
                (lineSegEnd.x - lineSegStart.x) * (lineSegStart.y - circleCenter.y)
                    - (lineSegStart.x - circleCenter.x) * (lineSegEnd.y - lineSegStart.y))
            / Math.sqrt(
                Math.pow(lineSegEnd.x - lineSegStart.x, 2)
                    + Math.pow(lineSegEnd.y - lineSegStart.y, 2));

    // Return true if the distance is less than or equal to the radius
    return d <= circleRadius;
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
    return Math.max(min, Math.min(max, value));
  }
}
