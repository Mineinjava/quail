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

package com.mineinjava.quail.localization;

import com.mineinjava.quail.RobotMovement;
import com.mineinjava.quail.SwerveDrive;
import com.mineinjava.quail.SwerveModuleBase;
import com.mineinjava.quail.util.MathUtil;
import com.mineinjava.quail.util.geometry.Pose2d;
import com.mineinjava.quail.util.geometry.Vec2d;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Represents a swerve drive position on the field.
 *
 * <p>Also provides methods for calculating the robot's position. A standard usage would look
 * something like this:
 *
 * <ul>
 *   <li>Check if you have vision and can use it to update the robot pose, use it to update the
 *       robot's position and then return.
 *   <li>If you don't have vision, get a "real life" vector from the modules **IF AT ALL POSSIBLE,
 *       READ FROM ENCODERS--DO NOT ASSUME MODULES ARE IN THE RIGHT PLACE**.
 *   <li>Pass those vectors into the `calculateOdometry` or `calculateFastOdometry` method. If
 *       unsure which one to use, use `calculateOdometry`
 *   <li>rotate the returned vector
 *   <li>pass the returned vector into one of the updateDelta methods
 *   <li>set the robot's heading to the gyro heading.
 * </ul>
 *
 * <p>When constructing this class, pass in the positions of the modules relative to robot center
 *
 * <p>TODO: Make it possible to update odometry via a velocity vector.
 */
public class SwerveOdometry implements Localizer {
  public ArrayList<Vec2d> moduleVectors;
  public double x = 0;
  public double y = 0;
  public double theta = 0;
  public RobotMovement lastSpeedVector = new RobotMovement(0, 0, 0);

  /**
   * Instantiates the SwerveOdometry object
   *
   * @param moduleVectors module position vectors
   */
  public SwerveOdometry(Vec2d[] moduleVectors) {
    this.moduleVectors = new ArrayList<Vec2d>(Arrays.asList(moduleVectors));
    assert moduleVectors.length >= 2;
  }

  /**
   * Instantiates the SwerveOdometry object
   *
   * @param moduleVectors module position vectors
   */
  public SwerveOdometry(List<Vec2d> moduleVectors) {
    this.moduleVectors = new ArrayList<Vec2d>(moduleVectors);
    assert moduleVectors.size() >= 2;
  }

  /**
   * Instantiates the SwerveOdometry object
   *
   * @param moduleVectors module position vectors
   */
  public SwerveOdometry(ArrayList<Vec2d> moduleVectors) {
    this.moduleVectors = moduleVectors;
    assert moduleVectors.size() >= 2;
  }

  /**
   * Instantiates the SwerveOdometry object
   *
   * @param drivetrain drivetrain from which to extract the module vectors
   */
  public SwerveOdometry(SwerveDrive drivetrain) {
    this(extractModuleVectors(drivetrain));
  }

  /**
   * Extracts the module vectors from a drivetrain.
   *
   * @param drivetrain The drivetrain from which to extract the module vectors
   * @return An array of module vectors.
   */
  private static Vec2d[] extractModuleVectors(SwerveDrive drivetrain) {
    ArrayList<Vec2d> moduleVectors = new ArrayList<>();
    for (Object module : drivetrain.swerveModules) {
      SwerveModuleBase newmodule = (SwerveModuleBase) module;
      moduleVectors.add(newmodule.position);
    }
    return moduleVectors.toArray(new Vec2d[0]);
  }

  /**
   * Updates the robot's position based on a change in x, y, and a theta.
   *
   * <p>Note that these values represent a change in position, not velocity.
   *
   * <p>Multiply velocity by delta time to get delta position. All values must be field-centric.
   *
   * @param dx
   * @param dy
   * @param dtheta
   */
  public void updateDeltaOdometry(double dx, double dy, double dtheta) {
    x += dx;
    y += dy;
    theta += dtheta;
  }

  /**
   * Updates the robot's position based on a change in x, y, (represented by a vector) and a theta.
   *
   * <p>Note that these values represent a change in position, not velocity.
   *
   * <p>Multiply velocity by delta time to get delta position. All values must be field-centric.
   *
   * @param dpos
   * @param dtheta
   */
  public void updateDeltaOdometry(Vec2d dpos, double dtheta) {
    this.updateDeltaOdometry(dpos.x, dpos.y, dtheta);
  }

  /**
   * Sets the robot's position to a specific x, y, and theta.
   *
   * <p>All values must be field-centric.
   *
   * @param mx
   * @param my
   * @param mtheta
   */
  public void updateOdometry(double mx, double my, double mtheta) {
    x = mx;
    y = my;
    theta = mtheta;
  }

  /**
   * Sets the robot's position to a specific x, y, (represented by a vector) and theta.
   *
   * <p>All values must be field-centric.
   *
   * @param pos
   * @param mtheta
   */
  public void updateOdometry(Vec2d pos, double mtheta) {
    this.updateOdometry(pos.x, pos.y, mtheta);
  }

  /**
   * Sets the robot's position to a specific x, y (represented by a vector).
   *
   * <p>All values must be field-centric.
   *
   * @param pos
   */
  public void updateOdometry(Vec2d pos) {
    this.x = pos.x;
    this.y = pos.y;
  }

  /**
   * Updates the robot's position based on a change in x, y (represented by a vector).
   *
   * <p>All values must be field-centric. Note that these values represent a change in position, not
   * velocity. Multiply velocity by delta time to get delta position.
   *
   * @param dpos
   */
  public void updateDeltaPoseEstimate(Vec2d dpos) {
    this.updateDeltaOdometry(dpos.x, dpos.y, 0);
  }

  /**
   * Returns the robot's pose when given a change in position as a vector and a change in rotation.
   *
   * @return the robot's pose
   */
  @Override
  public Pose2d getPose() {
    return new Pose2d(this.x, this.y, this.theta);
  }

  /**
   * Sets the angle of the odometry.
   *
   * @param angle robot heading in radians
   */
  public void setAngle(double angle) {
    this.theta = angle;
  }

  /**
   * Sets the robot's pose.
   *
   * <p>Use this method to override with Vision data or for initial pose.
   *
   * @param pose the robot's pose
   */
  @Override
  public void setPose(Pose2d pose) {
    this.updateOdometry(pose.x, pose.y, pose.heading);
  }

  /**
   * Calculates the robot's velocity based on the module positions.
   *
   * <p>NOTE: due to drift, it is recommended not to use the robot's rotation value given from this
   * method. Instead, use the robot's gyro.
   *
   * <p>Translation units are somewhat arbitrary (depends on the units of the module positions and
   * vectors passed in) Rotation units are radians
   *
   * <p>Also note that this method is not field-centric. You will have to rotate the returned
   * vector.
   *
   * @param modules the positions (current angle and current velocity) of the modules
   * @return the robot's movement (velocity and angular velocity)
   */
  public RobotMovement calculateOdometry(ArrayList<Vec2d> modules) {
    // to account for errors, we will take the average of all the module pairs
    List<List<Vec2d>> modulePairs = MathUtil.getPairs(modules.toArray(new Vec2d[0]));
    // lists to be averaged over
    List<Double> rotationValues = new ArrayList<>();
    List<Vec2d> movementVectors = new ArrayList<>();
    // for each pair of modules, calculate the movement vector and rotation speed
    for (List<Vec2d> pair : modulePairs) {
      // make it easier to access modules
      Vec2d module1 = pair.get(0);
      Vec2d module2 = pair.get(1);
      // also calculate the rotation vectors (needed for calculations)
      Vec2d module1RotationVector =
          this.moduleVectors.get(modules.indexOf(module1)).rotate(-Math.PI / 2, false);
      Vec2d module2RotationVector =
          this.moduleVectors.get(modules.indexOf(module2)).rotate(-Math.PI / 2, false);

      // calculate the difference between the module vectors
      Vec2d moduleDifference = module1.subtract(module2);
      // calculate the difference between the rotation vectors
      Vec2d rotationDifference = module1RotationVector.subtract(module2RotationVector);

      // calculate the rotation speeed
      double rotationSpeed = moduleDifference.x / rotationDifference.x;
      rotationValues.add(rotationSpeed);
      // calculate the movement vector using substitution
      movementVectors.add(module1.subtract(module1RotationVector.scale(rotationSpeed)));
    }
    // average the rotation values
    double rotation =
        rotationValues.stream().mapToDouble(Double::doubleValue).average().getAsDouble();
    // calculate the sum of the movement vectors
    Vec2d translation = new Vec2d(0, 0);
    for (Vec2d movementVector : movementVectors) {
      translation = translation.add(movementVector);
    }
    // average the movement vectors
    translation = translation.scale((double) 1 / movementVectors.size());
    this.lastSpeedVector = new RobotMovement(rotation, translation);
    return new RobotMovement(rotation, translation);
  }

  public RobotMovement calculateOdometry(Vec2d[] modules) {
    return this.calculateOdometry(new ArrayList<Vec2d>(Arrays.asList(modules)));
  }

  /**
   * Calculates the robot's movement based on the module positions.
   *
   * <p>This is the recommended method to use if:
   *
   * <ul>
   *   <li>The sum of all the module placement vectors is (0, 0)
   *       <ul>
   *         <li>This means that the sum of each module's rotation vectors will equal (0, 0), so the
   *             average of the module vectors will be the robot movement vector
   *       </ul>
   * </ul>
   *
   * <p>Again, due to drift, it is recommended not to use the robot's rotation value given from this
   * method.
   *
   * <p>Translation values are again arbitrary, but are the same as the ones given by the other
   * method. Rotation values are radians.
   *
   * <p>Also note that this method is not field-centric. You will have to rotate the returned
   * vector.
   *
   * @param modules the positions of the modules (Vec2d)
   * @return the robot's movement (velocity and angular velocity)
   */
  public RobotMovement calculateFastOdometry(ArrayList<Vec2d> modules) {
    // take average of all modules (this is the robot's movement vector)
    Vec2d averageModulePosition = new Vec2d(0, 0);
    for (Vec2d module : modules) {
      averageModulePosition = averageModulePosition.add(module);
    }
    averageModulePosition = averageModulePosition.scale((double) 1 / modules.size());

    // calculate the rotation speed
    double rotation = 0;
    for (int i = 0; i < modules.size(); i++) {
      Vec2d module = modules.get(i);
      Vec2d moduleVector = this.moduleVectors.get(i);
      // inverse of inverse (forwards) kinematics: undo the addition of rotation + movement
      Vec2d scaledRotationVector = module.subtract(averageModulePosition);
      // add the scalar value
      rotation += scaledRotationVector.x / moduleVector.x;
    }
    // average the rotation speed
    rotation /= modules.size();
    this.lastSpeedVector = new RobotMovement(rotation, averageModulePosition);
    return new RobotMovement(rotation, averageModulePosition);
  }
}
