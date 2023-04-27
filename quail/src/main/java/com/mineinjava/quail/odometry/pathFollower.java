package com.mineinjava.quail.odometry;

import com.mineinjava.quail.robotMovement;
import com.mineinjava.quail.util.PIDController;
import com.mineinjava.quail.util.util;
import com.mineinjava.quail.util.Vec2d;

/** class that helps you follow paths
 *
 */
public class pathFollower {
    public swerveOdometry odometry;
    public com.mineinjava.quail.odometry.path path;
    public double speed;
    public double maxTurnSpeed;
    public double maxTurnAcceleration;
    public double maxAcceleration;
    public PIDController turnController;
    public double precision;

    public pathFollower(swerveOdometry odometry, com.mineinjava.quail.odometry.path path, double speed, double maxTurnSpeed,
                        double maxTurnAcceleration, double maxAcceleration, PIDController turnController, double precision) {
        this.odometry = odometry;
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
     * @param elapsedTime time since last call
     * @return the next movement to follow the path
     */
    public robotMovement calculateNextDriveMovement(double elapsedTime) {
        // calculate the next movement to follow the path
        double deltaAngle = util.deltaAngle(this.odometry.theta, this.path.finalHeading);
        double turnSpeed = turnController.update(0, deltaAngle, elapsedTime);
        turnSpeed /= this.path.length();
        turnSpeed = util.clamp(turnSpeed, -this.maxTurnSpeed, this.maxTurnSpeed);
        Vec2d movementVector = this.path.vectorToNearestPoint(this.odometry.x, this.odometry.y, this.path.currentPoint);
        movementVector.scale(this.speed/movementVector.getLength());
        movementVector.rotate(-this.odometry.theta, false);
        if (movementVector.getLength() < this.precision) {
            this.path.currentPoint++;
        }
        return new robotMovement(turnSpeed, movementVector);
    }
}
