package com.mineinjava.quail.odometry;

import com.mineinjava.quail.robotMovement;
import com.mineinjava.quail.localization.Localizer;
import com.mineinjava.quail.util.MiniPID;
import com.mineinjava.quail.util.util;
import com.mineinjava.quail.util.geometry.Pose2d;
import com.mineinjava.quail.util.geometry.Vec2d;

/** class that helps you follow paths
 *
 */
public class pathFollower {
    public Localizer localizer;
    public com.mineinjava.quail.odometry.path path;
    public double speed;
    public double maxTurnSpeed;
    public double maxTurnAcceleration;
    public double maxAcceleration;
    public MiniPID turnController;
    public double precision;

    public pathFollower(Localizer localizer, com.mineinjava.quail.odometry.path path, double speed, double maxTurnSpeed,
                        double maxTurnAcceleration, double maxAcceleration, MiniPID turnController, double precision) {
        this.localizer = localizer;
        this.path = path;
        this.speed = speed;
        this.maxTurnSpeed = maxTurnSpeed;
        this.maxTurnAcceleration = maxTurnAcceleration;
        this.maxAcceleration = maxAcceleration;
        this.turnController = turnController;
        this.precision = precision;
    }

    /**
     * Update the path to follow
     * This exists so that you can reuse the path follower between autonomous paths and movements.
     * @param path the path to follow
     */
    public void setPath(com.mineinjava.quail.odometry.path path) {
        this.path = path;
    }

    /**
     * Calculate the next movement to follow the path
     * This does return a field-centric movement vector. It does not limit acceleration (yet)
     * @return the next movement to follow the path
     */
    public robotMovement calculateNextDriveMovement() {
        // calculate the next movement to follow the path
        Pose2d currentPose = this.localizer.getPoseEstimate();
        double deltaAngle = util.deltaAngle(currentPose.heading, this.path.finalHeading);
        double turnSpeed = turnController.getOutput(0, deltaAngle);
        turnSpeed /= this.path.length();
        turnSpeed = util.clamp(turnSpeed, -this.maxTurnSpeed, this.maxTurnSpeed);
        Vec2d movementVector = this.path.vectorToNearestPoint(currentPose.x, currentPose.y, this.path.currentPoint);
        movementVector.scale(this.speed/movementVector.getLength());
        movementVector.rotate(-currentPose.heading, false);
        if (movementVector.getLength() < this.precision) {
            this.path.currentPoint++;
        }
        return new robotMovement(turnSpeed, movementVector);
    }
}
