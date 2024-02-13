package com.mineinjava.quail.pathing;

public class ConstraintsPair {

  private double velocity;
  private double acceleration;

  /**
   * Represents a pair of constraints (max velocity and max acceleration) for use in the path
   * follower Can be either translational or angular velocity. If angular velocity, units are in
   * radians.
   *
   * @param maxVelocity your units/s
   * @param maxAcceleration your units/s^2
   */
  public ConstraintsPair(double maxVelocity, double maxAcceleration) {
    this.velocity = maxVelocity;
    this.acceleration = maxAcceleration;
  }

  /**
   * Returns the constraints' max velocity
   *
   * @return
   */
  public double getMaxVelocity() {
    return velocity;
  }

  /**
   * Returns the constraints' max acceleration
   *
   * @return
   */
  public double getMaxAcceleration() {
    return acceleration;
  }
}
