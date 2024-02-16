package com.mineinjava.quail.localization;

import com.mineinjava.quail.util.Util;
import com.mineinjava.quail.util.geometry.Pose2d;
import com.mineinjava.quail.util.geometry.Vec2d;
import java.util.ArrayList;

public class KalmanFilterLocalizer implements Localizer {
  private Vec2d poseEstimate = new Vec2d(0, 0);
  private double looptime = 0;
  private ArrayList<KalmanPose2d> velocities =
      new ArrayList<KalmanPose2d>(); // / list of velocities, most recent last
  public double heading = 0;

  public KalmanFilterLocalizer(Vec2d initialPose, double looptime) {
    poseEstimate = initialPose;
    this.looptime = looptime;
  }

  /**
   * Updates the pose estimate based on the current velocity and the time since the last vision
   * update
   *
   * @param observedPose the current pose estimate (get this from vision usually)
   * @param velocity the current velocity
   * @param poseEstimateLatency the time since the last vision update
   */
  public Vec2d update(Vec2d observedPose, Vec2d velocity, double poseEstimateLatency, double w) {
    KalmanPose2d currentVelocity = new KalmanPose2d(velocity, System.currentTimeMillis());
    velocities.add(currentVelocity); // add the current velocity to the list of velocities
    // trim the list of velocities to only include the relevant ones.
    while (velocities.size() > 0
        && velocities.get(0).timestamp < System.currentTimeMillis() - poseEstimateLatency) {
      velocities.remove(0);
    }
    // distance traveled since the vision update
    Vec2d deltaPosSinceVision = new Vec2d(0, 0);
    for (KalmanPose2d vel : velocities) {
      deltaPosSinceVision =
          deltaPosSinceVision.add(
              vel); // don't scale here, we'll do it later (distributive property)
    }
    deltaPosSinceVision =
        deltaPosSinceVision.scale(looptime); // scale by the looptime: distance = velocity * time
    // update the vision pose estimate with the delta from velocity
    Vec2d visionPoseEstimate = observedPose.add(deltaPosSinceVision);
    // update the last pose estimate with the velocity
    Vec2d kinematicsPoseEstimate = this.poseEstimate.add(velocity.scale(this.looptime));
    // update the pose estimate with a weighted average of the vision and kinematics pose estimates
    w = Util.clamp(w, 0, 1); // make sure w is between 0 and 1 (inclusive)
    System.out.println(
        "k "
            + kinematicsPoseEstimate.toString()
            + "v "
            + visionPoseEstimate.toString()
            + "n "
            + this.velocities.size());
    this.poseEstimate = ((visionPoseEstimate.scale(w)).add(kinematicsPoseEstimate.scale(1 - w)));
    return this.poseEstimate;
  }

  public Pose2d getPose() {
    return new Pose2d(poseEstimate.x, poseEstimate.y, this.heading);
  }

  public void setPose(Pose2d pose) {
    this.poseEstimate = new Vec2d(pose.x, pose.y);
  }

  public void setHeading(double heading) {
    this.heading = heading;
  }
}

class KalmanPose2d extends Vec2d {
  public double timestamp = 0;

  public KalmanPose2d(double x, double y, double timestamp) {
    super(x, y);
    this.timestamp = timestamp;
  }

  public KalmanPose2d(Vec2d vec, double timestamp) {
    super(vec.x, vec.y);
    this.timestamp = timestamp;
  }
}
