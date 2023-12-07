package com.mineinjava.quail.pathing;

import java.util.ArrayList;

import com.mineinjava.quail.RobotMovement;
import com.mineinjava.quail.localization.Localizer;
import com.mineinjava.quail.util.MiniPID;
import com.mineinjava.quail.util.Util;
import com.mineinjava.quail.util.geometry.Pose2d;
import com.mineinjava.quail.util.geometry.Vec2d;

/** class that helps you follow paths
 *
 */
public class PathFollower {
    public Path path;
    public double speed;
    public double maxTurnSpeed;
    public double maxTurnAcceleration;
    public double maxAcceleration;
    public MiniPID turnController;
    public double precision;
    public Localizer localizer;
    public double slowDownDistance;

    public double lastTime;

    public Pose2d lastRobotPose;
    public double kP;

    public PathFollower(Localizer localizer, Path path, double speed, double maxTurnSpeed,
                        double maxTurnAcceleration, double maxAcceleration, MiniPID turnController, double precision, double slowDownDistance, double kP) {
        this.localizer = localizer;
        this.path = path;
        this.speed = speed;
        this.maxTurnSpeed = maxTurnSpeed;
        this.maxTurnAcceleration = maxTurnAcceleration;
        this.maxAcceleration = maxAcceleration;
        this.turnController = turnController;
        this.precision = precision;
        this.slowDownDistance = slowDownDistance; // in the future add an option to calculate it based on max accel.
        this.kP = kP;
    }

    public PathFollower(Localizer localizer, double speed, double maxTurnSpeed, double maxTurnAcceleration,
                        double maxAcceleration, MiniPID turnController, double precision, double slowDownDistance, double kP) {
        this(localizer, new Path(new ArrayList<Pose2d>()), speed, maxTurnSpeed, maxTurnAcceleration, maxAcceleration, turnController, precision, slowDownDistance, kP);
    }

    
    /**
     * Update the path to follow
     * This exists so that you can reuse the path follower between autonomous paths and movements.
     * @param path the path to follow
     */
    public void setPath(Path path) {
        this.path = path;
    }

    /**
     * Calculate the next movement to follow the path
     * This does return a field-centric movement vector. It does not limit acceleration (yet)
     * @return the next movement to follow the path
     */
    public RobotMovement calculateNextDriveMovement() {
        // calculate the next movement to follow the path
        if( this.localizer == null ) {
            throw new NullPointerException("localizer is null, ensure that you have instantiated the localizer object");
        }

        Pose2d currentPose = this.localizer.getPoseEstimate();
        double loopTime = (System.currentTimeMillis() - this.lastTime) / 1000.0;
        double deltaAngle = Util.deltaAngle(currentPose.heading, this.path.getCurrentPoint().heading); // this may or may not work
        if (this.lastRobotPose == null) {
            this.lastRobotPose = currentPose;
        }
        if (this.path.getCurrentPoint().isHit(this.precision, currentPose, this.lastRobotPose)) {
            this.path.currentPointIndex++;
        }
        if (this.isFinished()) {
            return new RobotMovement(0, new Vec2d(0, 0)); // the path is over
        }



        Vec2d idealMovementVector = this.path.vectorToCurrentPoint(currentPose);
        if (this.path.remainingLength(currentPose) < this.slowDownDistance) {
            idealMovementVector = idealMovementVector.normalize().scale(this.path.remainingLength(currentPose)*this.kP);
        }
        if (idealMovementVector.getLength() > this.speed) {
            idealMovementVector = idealMovementVector.normalize().scale(this.speed);
        }
        Vec2d oldVelocity = this.lastRobotPose.vectorTo(currentPose).scale(1/loopTime);
        Vec2d accelerationVector = idealMovementVector.subtract(oldVelocity).scale(1/loopTime);

        if (accelerationVector.getLength() > this.maxAcceleration) {
            accelerationVector = accelerationVector.normalize().scale(this.maxAcceleration);
        }
        Vec2d movementVector = oldVelocity.add(accelerationVector.scale(loopTime));

        double turnSpeed = turnController.getOutput(0, deltaAngle);
        turnSpeed /= this.path.distanceToNextPoint(currentPose);
        turnSpeed = Util.clamp(turnSpeed, -this.maxTurnSpeed, this.maxTurnSpeed);

        this.lastTime = System.currentTimeMillis();
        return new RobotMovement(turnSpeed, movementVector);
    }
    
    /** returns true if the robot is finished following the path.
     */
    public Boolean isFinished() {
        if( this.localizer == null ) {
            throw new NullPointerException("localizer is null, ensure that you have instantiated the localizer object");
        }

       if (this.path.currentPointIndex - 1 > this.path.points.size()) {
           return true;
       }

       Pose2d currentPose = this.localizer.getPoseEstimate();

       return this.path.vectorToNearestPoint(currentPose, this.path.lastPointIndex).getLength() < this.precision;
    }
}