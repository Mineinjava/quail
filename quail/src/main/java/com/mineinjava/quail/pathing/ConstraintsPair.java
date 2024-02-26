package com.mineinjava.quail.pathing;

/**
 * Represents a pair of constraints (max velocity and max acceleration) for use in the path follower.
 * This can be either translational or angular velocity. If angular velocity, units are in radians.
 */
public class ConstraintsPair {

  private final double velocity;
  private final double acceleration;

  /**
   * Constructs a new ConstraintsPair with the specified max velocity and max acceleration.
   *
   * @param maxVelocity the maximum velocity in units/s
   * @param maxAcceleration the maximum acceleration in units/s^2
   */
  public ConstraintsPair(double maxVelocity, double maxAcceleration) {
    this.velocity = maxVelocity;
    this.acceleration = maxAcceleration;
  }

  /**
   * Returns the maximum velocity of the constraints.
   *
   * @return the maximum velocity in units/s
   */
  public double getMaxVelocity() {
    return velocity;
  }

  /**
   * Returns the maximum acceleration of the constraints.
   *
   * @return the maximum acceleration in units/s^2
   */
  public double getMaxAcceleration() {
    return acceleration;
  }
}

