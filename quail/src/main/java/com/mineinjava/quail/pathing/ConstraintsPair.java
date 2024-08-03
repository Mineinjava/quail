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

public class ConstraintsPair {

  private double velocity;
  private double acceleration;

  /**
   * Represents a pair of constraints (max velocity and max acceleration) for use in the path
   * follower.
   *
   * <p>Can be either translational or angular velocity. If angular velocity, units are in radians.
   *
   * @param maxVelocity your units/s
   * @param maxAcceleration your units/s^2
   */
  public ConstraintsPair(double maxVelocity, double maxAcceleration) {
    this.velocity = maxVelocity;
    this.acceleration = maxAcceleration;
  }

  /** Returns the constraints' max velocity. */
  public double getMaxVelocity() {
    return velocity;
  }

  /** Returns the constraints' max acceleration. */
  public double getMaxAcceleration() {
    return acceleration;
  }
}
