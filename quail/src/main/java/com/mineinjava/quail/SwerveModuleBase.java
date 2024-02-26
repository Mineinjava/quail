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
   * Represents a swerve module Designed to be inherited. Please override the setAngle() and
   * setRawSpeed() methods at minimum.
   *
   * <p>All angles are in radians. All length is in the unit of your choice
   *
   * <p>Other things that you may want to include: - reset module position (set the current angle to
   * 0) using absolute encoders - re-zero the steering motor using absolute encoders
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
   * calculates the angle to set the steering motor to
   *
   * @param angle the angle to set the steering motor to
   * @return the angle to set the steering motor to
   */
  public double calculateNewAngleSetpoint(double angle) {
    double shortestAngle = MathUtil.deltaAngle(currentAngle, angle);
    return currentAngle = currentAngle + shortestAngle;
  }

  /**
   * "optimized" motor rotation: if the angle is greater than 90 degrees, rotate the motor in the
   * opposite direction and rotate less than 90 degrees
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
   * Calulates the angle to set the steering motor to. Wrapper for both calculateOptimizedAngle and
   * calculateNewAngleSetpoint please use this instead of anything else
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
   * "X-Locks" the modules points all of the wheels toward the center of the robot to make the robot
   * harder to push not very useful if all of your swerve modules are colinear
   */
  public void XLock() {
    this.setSpeed(0);
    this.setAngle(this.position.getAngle() + (Math.PI / 2));
  }

  /**
   * sets the angle of the module OVERRIDE ME!!! This is where you call your motor controllers
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
   * sets the raw speed of the module OVERRIDE ME!!! This is where you call your motor controllers
   *
   * @param speed the speed to set the module to
   */
  public void setRawSpeed(double speed) {
    System.out.println(
        "Default quail.swerveModuleBase.setRawSpeed() called. Override me!. Speed: " + speed);
  }

  /**
   * sets the speed of the module
   *
   * @param speed the speed to set the module to
   */
  public void setSpeed(double speed) {
    setRawSpeed(this.motorFlipper * speed);
  }

  /**
   * sets the module's motion to the specified vector
   *
   * @param vec the vector to set the module to
   */
  public void set(Vec2d vec) {
    setAngle(vec.getAngle());
    setSpeed(vec.getLength());
  }
}
