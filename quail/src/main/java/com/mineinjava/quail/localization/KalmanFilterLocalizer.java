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

import com.mineinjava.quail.util.MathUtil;
import com.mineinjava.quail.util.geometry.Pose2d;
import com.mineinjava.quail.util.geometry.Vec2d;
import java.util.ArrayList;

/**
 * Localizer that uses a Kalman Filter.
 *
 * <p>
 *
 * @see https://astr0clad.github.io/quail_docs/localization/vision/
 */
public class KalmanFilterLocalizer implements Localizer {
  private Pose2d poseEstimate = new Pose2d();
  private double looptime = 0;
  private ArrayList<KalmanPose2d> velocities =
      new ArrayList<KalmanPose2d>(); // / list of velocities, most recent last
  public double heading = 0;

  public KalmanFilterLocalizer(Pose2d initialPose, double looptime) {
    poseEstimate = initialPose;
    this.looptime = looptime;
  }

  /**
   * Updates the pose estimate.
   *
   * <p>Updates based on the current velocity and the time since the last vision update
   *
   * @param observedPose the current pose estimate (get this from vision usually)
   * @param velocity the current velocity
   * @param poseEstimateLatency the time since the last vision update
   * @param w how much weight to "trust" the vision estimate
   * @param timestampMillis the current system time in ms
   */
  public Pose2d update(
      Pose2d observedPose,
      Pose2d velocity,
      double poseEstimateLatency,
      double w,
      double hw,
      double timestampMillis) {
    KalmanPose2d currentVelocity = new KalmanPose2d(velocity, timestampMillis);
    velocities.add(currentVelocity); // add the current velocity to the list of velocities
    // trim the list of velocities to only include the relevant ones.
    while (velocities.size() > 0
        && velocities.get(0).timestamp < timestampMillis - poseEstimateLatency) {
      velocities.remove(0);
    }
    // distance traveled since the vision update
    Vec2d deltaTranslationSinceVision = new Vec2d(0, 0);
    double deltaRotationSinceVision = 0d;
    for (KalmanPose2d vel : velocities) {
      deltaTranslationSinceVision =
        deltaTranslationSinceVision.add(
            vel.vec()); // don't scale here, we'll do it later (distributive property)
      deltaRotationSinceVision += vel.heading;
    }

    deltaTranslationSinceVision =
      deltaTranslationSinceVision.scale(this.looptime); // scale by the looptime: distance = velocity * time
    deltaRotationSinceVision = deltaRotationSinceVision * this.looptime;

    // update the vision pose estimate with the delta from velocity
    Vec2d updatedPoseSinceVision = observedPose.vec().add(deltaTranslationSinceVision );
    double updatedRotationSinceVision = observedPose.heading + deltaRotationSinceVision;
    // update the last pose estimate with the velocity
    Vec2d kinematicsTranslationEstimate = this.poseEstimate.vec().add(velocity.vec().scale(this.looptime));
    double kinematicsRotationEstimate = this.poseEstimate.heading + (velocity.heading * this.looptime);

    // update the pose estimate with a weighted average of the vision and kinematics pose estimates
    w = MathUtil.clamp(w, 0, 1); // make sure w is between 0 and 1 (inclusive)

    Vec2d translationEstimate = ((updatedPoseSinceVision.scale(w)).add(kinematicsTranslationEstimate.scale(1 - w)));
    double rotationEstimate = (updatedRotationSinceVision*hw) + (kinematicsRotationEstimate * (1-hw));
    this.poseEstimate = new Pose2d(translationEstimate, rotationEstimate);
    return this.poseEstimate;
      }

  /** Returns the current pose estimate */
  public Pose2d getPose() {
    return new Pose2d(poseEstimate.x, poseEstimate.y, this.heading);
  }

  /**
   * Sets the current position.
   *
   * <p>Completely overrides the old position
   *
   * @param pose new translational pose to use
   */
  public void setPose(Pose2d pose) {
    this.poseEstimate = pose;
  }

  /**
   * Sets the current heading.
   *
   * <p>Call this on every frame with the robot's heading
   *
   * @param heading Robot heading
   */
  public void setHeading(double heading) {
    this.heading = heading;
  }
}

/** Modified translational pose but it has a timestamp. */
class KalmanPose2d extends Pose2d {
  public double timestamp = 0;

  public KalmanPose2d(double x, double y, double heading, double timestamp) {
    super(x, y, heading);
    this.timestamp = timestamp;
  }

  public KalmanPose2d(Pose2d pos, double timestamp) {
    super(pos.x, pos.y, pos.heading);
    this.timestamp = timestamp;
  }
}
