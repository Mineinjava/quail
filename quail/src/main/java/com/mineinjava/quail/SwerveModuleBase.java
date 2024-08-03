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

import com.mineinjava.quail.util.MathUtil;
import com.mineinjava.quail.util.geometry.Vec2d;

public class SwerveModuleBase {
  public Vec2d position;
  protected final double steeringRatio;
  protected final double driveRatio;
  public boolean optimized;
  public Vec2d steeringVector;
  protected double currentAngle; // in radians
  protected int motorFlipper = 1; // optimization for motor rotation

  /**
   * Represents a swerve module Designed to be inherited.
   *
   * <p>Please override the setAngle() and setRawSpeed() methods at minimum.
   *
   * <p>All angles are in radians. All length is in the unit of your choice
   *
   * <p>Other things that you may want to include:
   *
   * <ul>
   *   <li>reset module position (set the current angle to 0) using absolute encoders
   *   <li>re-zero the steering motor using absolute encoders
   * </ul>
   *
   * @param position the position of the module relative to the center of rotation
   * @param steeringRatio gear ratio of the steering motor
   * @param driveRatio gear ratio of the drive motor
   */
  public SwerveModuleBase(Vec2d position, double steeringRatio, double driveRatio) {
    // default optimized value is true
    this(position, steeringRatio, driveRatio, true);
  }

  public SwerveModuleBase(
      Vec2d position, double steeringRatio, double driveRatio, boolean optimized) {
    this.position = position;
    this.steeringRatio = steeringRatio;
    this.driveRatio = driveRatio;
    this.optimized = optimized;
    steeringVector =
        new Vec2d(this.position.getAngle() - (Math.PI / 2), this.position.getLength(), false);
  }

  /**
   * Calculates the angle to set the steering motor to,
   *
   * @param angle the angle to set the steering motor to
   * @return the angle to set the steering motor to
   */
  public double calculateNewAngleSetpoint(double angle) {
    double shortestAngle = MathUtil.deltaAngle(currentAngle, angle);
    return currentAngle = currentAngle + shortestAngle;
  }

  /**
   * "Optimized" motor rotation.
   *
   * <p>if the angle is greater than 90 degrees, rotate the motor in the opposite direction and
   * rotate less than 90 degrees
   */
  public double calculateOptimizedAngle(double angle) {
    double deltaAngle = MathUtil.deltaAngle(currentAngle, angle);
    if (Math.abs(deltaAngle) > Math.PI / 2) {
      motorFlipper = -1;
      return angle + Math.PI;
    } else {
      motorFlipper = 1;
      return angle;
    }
  }

  /**
   * Calulates the angle to set the steering motor to.
   *
   * <p>Wrapper for both {@link calculateOptimizedAngle} and {@link calculateNewAngleSetpoint}
   *
   * <p>please use this instead of anything else
   */
  public double calculateDesiredAngleWrapper(double angle) {
    double setpoint;
    if (optimized) {
      setpoint = calculateOptimizedAngle(angle);
    } else {
      setpoint = angle;
    }
    return calculateNewAngleSetpoint(setpoint);
  }

  /**
   * "X-Locks" the modules.
   *
   * <p>Points all of the wheels toward the center of the robot to make the robot harder to push.
   *
   * <p>not very useful if all of your swerve modules are colinear
   */
  public void XLock() {
    this.setSpeed(0);
    this.setAngle(this.position.getAngle() + (Math.PI / 2));
  }

  /**
   * Sets the angle of the module.
   *
   * <p>OVERRIDE ME!!! This is where you call your motor controllers
   *
   * @param angle the angle to set the module to
   */
  public void setRawAngle(double angle) {
    System.out.println(
        "Default quail.swerveModuleBase.setAngle() called. Override me!.    Angle: " + angle);
  }

  public void setAngle(double angle) {
    setRawAngle(calculateDesiredAngleWrapper(angle));
  }

  /**
   * Sets the raw speed of the module.
   *
   * <p>OVERRIDE ME!!! This is where you call your motor controllers
   *
   * @param speed the speed to set the module to
   */
  public void setRawSpeed(double speed) {
    System.out.println(
        "Default quail.swerveModuleBase.setRawSpeed() called. Override me!. Speed: " + speed);
  }

  /**
   * Sets the speed of the module.
   *
   * @param speed the speed to set the module to
   */
  public void setSpeed(double speed) {
    setRawSpeed(this.motorFlipper * speed);
  }

  /**
   * Sets the module's motion to the specified vector;
   *
   * @param vec the vector to set the module to
   */
  public void set(Vec2d vec) {
    setAngle(vec.getAngle());
    setSpeed(vec.getLength());
  }
}
