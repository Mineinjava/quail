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
import java.util.List;

public class SwerveDrive<T extends SwerveModuleBase> {
  public final List<T> swerveModules;
  private double maxModuleSpeed = 1.0;

  /**
   * Represents a swerve drive .
   *
   * <p> Designed to be inherited from. 
   * <p> While it will work without being inherited from, you may want to add some features such as:
   *
   * <ul>
   * <li> reset gyro, both from controller and from vision odometry 
   * <li> reset module positions (from absolute encoders)
   * </ul>
   *
   * <p>Normal use of this class would look something like:
   *
   * <ul>
   * <li> Create a list of swerve modules 
   * <li> Create a swerveDrive object with the list of swerve
   * modules 
   * <li> Every time you want to move, call `move()` with the desired movement vector and
   * rotation speed 
   * <li> Obtain the module vectors from the swerve modules and pass them into the
   * swerveOdometry class (optional) 
   * <li> Pass the normalized vectors into the swerve modules
   * </ul>
   *
   * @param swerveModules a list of swerve modules
   */
  public SwerveDrive(List<T> swerveModules) {
    this.swerveModules = swerveModules;
  }

  /**
   * @param swerveModules a list of swerve modules
   * @param maxModuleSpeed maximum allowed speed for swerve modules
   */
  public SwerveDrive(List<T> swerveModules, double maxModuleSpeed) {
    this.swerveModules = swerveModules;
    this.maxModuleSpeed = maxModuleSpeed;
  }

  /**
   * @param moveVector the vector to move in
   * @param rotationSpeed speed of rotation
   * @param centerPoint modified center of rotation. Pass in Vec2d(0, 0) for default center of
   *     rotation
   * @param gyroOffset the gyro rotation in radians
   */
  public Vec2d[] calculateMoveAngles(
      Vec2d moveVector, double rotationSpeed, double gyroOffset, Vec2d centerPoint) {
    moveVector = moveVector.rotate(gyroOffset, false);
    // create a list of four vec2d objects and iterate over them with a for loop (not foreach)
    Vec2d[] moduleVectors = new Vec2d[this.swerveModules.size()];
    for (int i = 0; i < this.swerveModules.size(); i++) {
      SwerveModuleBase module = swerveModules.get(i);
      Vec2d moduleOffCenterVector = module.position.subtract(centerPoint);
      Vec2d moduleRotationVector = moduleOffCenterVector.rotate(Math.PI / 2, false);
      moduleVectors[i] = moveVector.add(moduleRotationVector.scale(rotationSpeed));
    }
    return moduleVectors;
  }

  /**
   * Attempts to move the drivetrain
   */
  public void move(RobotMovement movement, double gyroOffset) {
    Vec2d[] moduleVectors =
        calculateMoveAngles(movement.translation, movement.rotation, gyroOffset);
    moduleVectors = normalizeModuleVectors(moduleVectors, this.maxModuleSpeed);
    for (int i = 0; i < this.swerveModules.size(); i++) {
      swerveModules.get(i).set(moduleVectors[i]);
    }
  }

  /**
   * @param moveVector the vector to move in
   * @param rotationSpeed speed of rotation
   * @param gyroOffset the gyro rotation in radians
   */
  public Vec2d[] calculateMoveAngles(Vec2d moveVector, double rotationSpeed, double gyroOffset) {
    return calculateMoveAngles(moveVector, rotationSpeed, gyroOffset, new Vec2d(0, 0));
  }

  /**
   * @param moduleVectors list of module vectors to be normalized
   * @param maxAllowableMagnitude the maximum magnitude of the vectors (this function clamps the
   *     largest vector to this magnitude and scales the rest--this parameter is the upper limit on
   *     the clamp)
   * @return an array of vectors appropriately scaled so that the largest vector has a magnitude no
   *     greater than `maxAllowableMagnitude`.
   */
  public Vec2d[] normalizeModuleVectors(Vec2d[] moduleVectors, double maxAllowableMagnitude) {
    double maxMagnitude = 0;
    for (Vec2d moduleVector : moduleVectors) {
      if (moduleVector.getLength() > maxMagnitude) {
        maxMagnitude = moduleVector.getLength();
      }
    }
    if ((maxMagnitude > maxAllowableMagnitude) && (maxMagnitude != 0d)) {
      for (int i = 0; i < this.swerveModules.size(); i++) {
        moduleVectors[i] = moduleVectors[i].scale(maxAllowableMagnitude / maxMagnitude);
      }
    }
    return moduleVectors;
  }

  /**
   * @param moduleVectors list of module vectors to be normalized
   * @return an array of vectors appropriately scaled so that the largest vector has a magnitude no
   *     greater than 1.
   */
  public Vec2d[] normalizeModuleVectors(Vec2d[] moduleVectors) {
    return normalizeModuleVectors(moduleVectors, 1);
  }

  public void XLockModules() {
    for (SwerveModuleBase module : this.swerveModules) {
      module.XLock();
    }
  }
}
