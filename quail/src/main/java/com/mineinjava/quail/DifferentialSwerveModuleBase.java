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

import com.mineinjava.quail.util.MiniPID;
import com.mineinjava.quail.util.geometry.Vec2d;

/** A base class for differential swerve modules. */
public class DifferentialSwerveModuleBase extends SwerveModuleBase {
  Vec2d position;
  double steeringRatio;
  double driveRatio;
  double ticksPerRev;
  boolean optimized;
  MiniPID pid;
  
  private double degrees = ticksPerRev / 360;

  public DifferentialSwerveModuleBase(Vec2d position, double steeringRatio, double driveRatio, double ticksPerRev, MiniPID pid) {
    super(position, steeringRatio, driveRatio);
    this.position = position;
    this.steeringRatio = steeringRatio;
    this.driveRatio = driveRatio;
    this.ticksPerRev = ticksPerRev;
    this.pid = pid;
    
  }

  public DifferentialSwerveModuleBase(
      Vec2d position, double steeringRatio, double driveRatio,double ticksPerRev,MiniPID pid, boolean optimized) {
    super(position, steeringRatio, driveRatio, optimized);
    this.position = position;
    this.steeringRatio = steeringRatio;
    this.driveRatio = driveRatio;
    this.ticksPerRev = ticksPerRev;
    this.pid = pid;
    this.optimized = optimized;
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
  /**
   * Calculates the motor speeds for a differential swerve module. 
   * 
   * @param rotationSpeed the current rotation speed of the pod
   * @param wheelSpeed the current speed of the wheel
   * @return motor speeds (array of length 2)
   */
  public double[]  calculateeMotorSpeeds(double speed, double angle, double motor1pos, double motor2pos){
    double[] motorSpeeds = new double[2];
    double currentAngle = calculateModuleAngle(motor1pos,motor2pos);
    double oppAngle = angleWrap(currentAngle + 180);

    double angleFromTarget = angleWrap(angle - currentAngle);
    double oppAngleFromTarget = angleWrap(angle - oppAngle);

    if(Math.abs(angleFromTarget) > Math.abs(oppAngleFromTarget)){
        angle = oppAngleFromTarget;
    }else{
        angle = angleFromTarget;
    }

    if (Math.abs(oppAngleFromTarget) < 0.3){//margin of allowed error
        speed = -speed;
    }else if (Math.abs(angleFromTarget) > 0.3){
        speed = 0;
    }
    motorSpeeds[0] = (-speed * 1) + pid.getOutput(currentAngle, angle);
    motorSpeeds[1] = (speed * 1) + pid.getOutput(currentAngle, angle);
    return motorSpeeds;
  }

  public double calculateModuleAngle(double motor1pos, double motor2pos){
        double angleTicks = (motor1pos + motor2pos) / 2;
        double angle = ticksToDegrees(angleTicks);
        angle = angleWrap(currentAngle);
        return angle;
  }



  private static double angleWrap(double angle) {
    if (angle > 0)
        return ((angle + Math.PI) % (Math.PI * 2)) - Math.PI;
    else
        return ((angle - Math.PI) % (Math.PI * 2)) + Math.PI;
}
  private double ticksToDegrees(double ticks){
  return ticks * degrees;
  }

}
