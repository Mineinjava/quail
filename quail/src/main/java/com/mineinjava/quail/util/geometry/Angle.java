/*
 * Class to represent an ccw+ angle in radians and provide various utilities for working with angles in radians or degrees.
 * 
 * The purpose is to provide a more intuitive and safe way to work with angles in radians and degrees.
 */

package com.mineinjava.quail.util.geometry;

/** Various utilities for working with angles. ALL angles MUST be in ccw+ radians */
public class Angle {
  private static final double TAU = 2 * Math.PI;

  private final double radians;

  /**
   * Constructs a new {@link Angle} with the given measure in radians.
   * @param radians
   */
  private Angle(double radians) {
    this.radians = radians;
  }

  /**
   * Factory method to create a new {@link Angle} with the given measure in radians.
   * @param radians measure in radians (ccw+)
   * @return a new {@link Angle} with the given measure in radians
   */
  public static Angle fromRadians(double radians) {
    return new Angle(radians);
  }

  /**
   * Factory method to create a new {@link Angle} with the given measure in degrees.
   * @param degrees measure in degrees (ccw+)
   * @return a new {@link Angle} with the given measure in degrees
   */
  public static Angle fromDegrees(double degrees) {
    return new Angle(Math.toRadians(degrees));
  }

  /**
   * Returns the measure of this angle in degrees.
   * @return the measure of this angle in degrees
   */
  public double getDegrees() {
    return Math.toDegrees(radians);
  }

  /**
   * Returns the measure of this angle in radians.
   * @return the measure of this angle in radians
   */
  public double getRadians() {
    return radians;
  }

  /**
   * Add {@code angle} to this angle and return the result.
   * @param angle angle to add
   * @return the result of adding {@code angle} to this angle
   */
  public Angle add(Angle angle) {
    return new Angle(radians + angle.radians);
  }

  /**
   * Subtract {@code angle} from this angle and return the result.
   *
   * @param angle angle to subtract
   * @return the result of subtracting {@code angle} from this angle
   */
  public Angle subtract(Angle angle) {
    return new Angle(radians - angle.radians);
  }

  /**
   * Returns this {@code Angle} clamped to {@code [0, 2pi]}.
   *
   * @param angle angle measure in radians
   */
  public Angle norm() {
    double modifiedAngle = ((radians % TAU) + TAU) % TAU;
    return new Angle(modifiedAngle);
  }

  /**
   * Returns {@code angle} clamped to {@code [0, 2pi]}.
   *
   * @param angle angle measure in radians
   */
  @Deprecated
  public static double norm(double angle) {
    double modifiedAngle = angle % TAU;

    modifiedAngle = (modifiedAngle + TAU) % TAU;

    return modifiedAngle;
  }

  /**
   * Returns this {@code Angle} clamped to {@code [-pi, pi]}.
   *
   * @param angleDelta angle delta in radians
   */
  public Angle normDelta() {
    double modifiedAngleDelta = norm().radians;

    if (modifiedAngleDelta > Math.PI) {
      modifiedAngleDelta -= TAU;
    }

    return new Angle(modifiedAngleDelta);
  }

  /**
   * Returns {@code angleDelta} clamped to {@code [-pi, pi]}.
   *
   * @param angleDelta angle delta in radians
   */
  @Deprecated
  public static double normDelta(double angleDelta) {
    double modifiedAngleDelta = norm(angleDelta);

    if (modifiedAngleDelta > Math.PI) {
      modifiedAngleDelta -= TAU;
    }

    return modifiedAngleDelta;
  }

  @Override
  public String toString() {
    return "Angle{radians=" + radians + "}";
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }

    if (!(o instanceof Angle)) {
      return false;
    }

    Angle angle = (Angle) o;

    return Double.compare(angle.radians, radians) == 0;
  }
}
