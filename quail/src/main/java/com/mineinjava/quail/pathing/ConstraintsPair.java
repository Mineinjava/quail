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

package com.mineinjava.quail.pathing;

/**
 * Represents a pair of constraints (max velocity and max acceleration) for use in the path
 * follower.
 *
 * <p>Can be either translational or angular velocity. If angular velocity, units are in radians.
 */
public class ConstraintsPair {

  private final double velocity;
  private final double acceleration;

  /**
   * Constructs a new ConstraintsPair with the specified max velocity and max acceleration.
   *
   * @param maxVelocity the maximum velocity in units/s
   * @param maxAcceleration the maximum velocity in units/s^2
   */
  public ConstraintsPair(double maxVelocity, double maxAcceleration) {
    this.velocity = maxVelocity;
    this.acceleration = maxAcceleration;
  }

  /** Returns the maximum velocity of the constraints. */
  public double getMaxVelocity() {
    return velocity;
  }

  /** Returns the maximum acceleration of the constraints. */
  public double getMaxAcceleration() {
    return acceleration;
  }
}
