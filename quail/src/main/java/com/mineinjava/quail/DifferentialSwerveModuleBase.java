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

package com.mineinjava.quail;

import com.mineinjava.quail.util.geometry.Vec2d;

/** A base class for differential swerve modules. */
public class DifferentialSwerveModuleBase extends SwerveModuleBase {

  public DifferentialSwerveModuleBase(Vec2d position, double steeringRatio, double driveRatio) {
    super(position, steeringRatio, driveRatio);
  }

  public DifferentialSwerveModuleBase(
      Vec2d position, double steeringRatio, double driveRatio, boolean optimized) {
    super(position, steeringRatio, driveRatio, optimized);
  }

  /**
   * Calculates the motor speeds for a differential swerve module.
   *
   * @param rotationSpeed the current rotation speed of the pod
   * @param wheelSpeed the current speed of the wheel
   * @return motor speeds (array of length 2)
   */
  public double[] calculateMotorSpeeds(double rotationSpeed, double wheelSpeed) {

    double[] motorSpeeds = new double[2];

    double adjustedRotationSpeed = rotationSpeed / (2 * steeringRatio);
    double adjustedWheelSpeed = wheelSpeed / (2 * driveRatio);

    motorSpeeds[0] = adjustedRotationSpeed + adjustedWheelSpeed;
    motorSpeeds[1] = adjustedRotationSpeed - adjustedWheelSpeed;

    return motorSpeeds;
  }

  /** Calculates the module angle based on the positions of the two motors. */
  public double calculateModuleAngle(double motor1pos, double motor2pos) {
    double averageRotation = (motor1pos + motor2pos) / 2;
    return averageRotation / steeringRatio;
  }
}
