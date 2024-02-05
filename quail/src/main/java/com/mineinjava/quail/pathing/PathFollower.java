package com.mineinjava.quail.pathing;

import java.util.ArrayList;

import com.mineinjava.quail.RobotMovement;
import com.mineinjava.quail.localization.Localizer;
import com.mineinjava.quail.util.MathUtil;
import com.mineinjava.quail.util.MiniPID;
import com.mineinjava.quail.util.Util;
import com.mineinjava.quail.util.geometry.Pose2d;
import com.mineinjava.quail.util.geometry.Vec2d;

/** class that helps you follow paths
 * TODO: Don't treat point as reached if angle is outside angular precision
 */
public class PathFollower {
    private Path path;
    private double speed;
    private double maxTurnSpeed;
    private double maxTurnAcceleration;
    private double maxAcceleration;
    private MiniPID turnController;
    private double precision;
    private Localizer localizer;
    private double slowDownDistance;

    private double lastTime;

    private Pose2d lastRobotPose;
    private Pose2d currentPose;
    private double kP;
    private Vec2d lastMovementVector;

    private double minVelocity;

    private double loopTime;

    public PathFollower(Localizer localizer, Path path, double speed, double maxTurnSpeed,
                        double maxTurnAcceleration, double maxAcceleration, MiniPID turnController, double precision, double slowDownDistance, double kP, double minVelocity) {
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
        this.minVelocity = minVelocity;
    }

    public PathFollower(Localizer localizer, double speed, double maxTurnSpeed, double maxTurnAcceleration,
                        double maxAcceleration, MiniPID turnController, double precision, double slowDownDistance, double kP, double minVelocity) {
        this(localizer, new Path(new ArrayList<Pose2d>()), speed, maxTurnSpeed, maxTurnAcceleration, maxAcceleration, turnController, precision, slowDownDistance, kP, minVelocity);
    }

    public PathFollower(Localizer localizer, Path path, ConstraintsPair translationPair, ConstraintsPair rotationPair,
                        MiniPID turnController, double precision, double slowDownDistance, double kP, double minVelocity) {
        this(localizer, path, translationPair.getMaxVelocity(), rotationPair.getMaxVelocity(), rotationPair.getMaxAcceleration(),
                translationPair.getMaxAcceleration(), turnController, precision, slowDownDistance, kP, minVelocity);
    }

    /**
     * Calculate the next movement to follow the path
     * This does return a field-centric movement vector.
     * @return the next movement to follow the path
     */
    public RobotMovement calculateNextDriveMovement() {
        // calculate the next movement to follow the path
        if( this.localizer == null ) {
            throw new NullPointerException("localizer is null, ensure that you have instantiated the localizer object");
        }
        if (this.isFinished()) {
            return new RobotMovement(0, new Vec2d(0, 0)); // the path is over
        }

        this.currentPose = this.localizer.getPose();
        this.loopTime = (System.currentTimeMillis() - this.lastTime) / 1000.0;
        this.lastTime = System.currentTimeMillis();

        if (currentPose == null) {
            return new RobotMovement(0, new Vec2d(0, 0)); // error?
        }
        double deltaAngle = Util.deltaAngle(currentPose.heading, this.path.getCurrentPoint().heading);

        if(this.path.getCurrentPoint() == null){
            return new RobotMovement(0, new Vec2d(0, 0)); // the path is over
        }
        if (this.lastRobotPose == null) {
            this.lastRobotPose = currentPose;
        }
        if (this.path.getCurrentPoint().isHit(this.precision, currentPose, this.lastRobotPose)) {
            this.path.currentPointIndex++;
        }

        Vec2d idealMovementVector = this.path.vectorToCurrentPoint(this.currentPose);
        if (idealMovementVector == null) {
            return new RobotMovement(0, new Vec2d(0, 0)); // the path is over
        }
        if (this.path.remainingLength(this.currentPose) < this.slowDownDistance) {
            idealMovementVector = idealMovementVector.normalize().scale(this.path.remainingLength(currentPose)*this.kP);
        }
        else {
            if (this.path.distanceToCurrentPoint(currentPose) < this.precision){
                Vec2d lastVector = this.path.vector_last_to_current_point();
                double angleDiff = lastVector.angleSimilarity(idealMovementVector);
                double desiredSpeed = MathUtil.lerp(this.speed, this.speed * angleDiff, this.path.distanceToCurrentPoint(currentPose) / (this.slowDownDistance - this.precision));
                idealMovementVector = idealMovementVector.normalize().scale(desiredSpeed);
            }
        }

        if (idealMovementVector.getLength() > this.speed) {
            idealMovementVector = idealMovementVector.normalize().scale(this.speed);
        }
        if (idealMovementVector.getLength() < this.minVelocity){
            idealMovementVector = idealMovementVector.normalize().scale(this.minVelocity);
        }

        //Vec2d oldVelocity = this.lastRobotPose.vectorTo(currentPose).scale(1/this.loopTime);
        if (this.lastMovementVector == null) {
            this.lastMovementVector = new Vec2d(0, 0);
        }
        Vec2d oldVelocity = this.lastMovementVector;
        Vec2d accelerationVector = idealMovementVector.subtract(oldVelocity).scale(1/this.loopTime);

        if (accelerationVector.getLength() > this.maxAcceleration) {
            accelerationVector = accelerationVector.normalize().scale(this.maxAcceleration);
        }
        Vec2d movementVector = oldVelocity.add(accelerationVector.scale(this.loopTime));

        double turnSpeed = turnController.getOutput(0, deltaAngle);
        turnSpeed /= this.path.distanceToNextPoint(currentPose);
        turnSpeed = Util.clamp(turnSpeed, -this.maxTurnSpeed, this.maxTurnSpeed);

        this.lastRobotPose = currentPose;
        this.lastMovementVector = movementVector;
        return new RobotMovement(turnSpeed, movementVector);
    }

    /** returns true if the robot is finished following the path.
     */
    public Boolean isFinished() {
        if( this.localizer == null ) {
            throw new NullPointerException("localizer is null, ensure that you have instantiated the localizer object");
        }

       if (this.path.currentPointIndex + 1 > this.path.points.size()) {
           return true;
       }
       if (this.path.getCurrentPoint() == null) {
           return true;
       }

       Pose2d currentPose = this.localizer.getPose();

       return this.path.vectorToNearestPoint(currentPose, this.path.lastPointIndex).getLength() < this.precision;
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
     * Returns the path that the robot is following
     * @return
     */
    public Path getPath() {
        return this.path;
    }

    /**
     * Sets the speed of the robot
     * @param speed the goal of the robot (in your units)
     */
    public void setSpeed(double speed) {
        this.speed = speed;
    }

    /**
     * Returns the speed of the robot
     * @return the speed of the robot
     */
    public double getSpeed() {
        return this.speed;
    }

    /**
     * Sets the maximum turn speed of the robot
     * @param maxTurnSpeed the maximum turn speed of the robot (rad/s)
     */
    public void setMaxTurnSpeed(double maxTurnSpeed) {
        this.maxTurnSpeed = maxTurnSpeed;
    }

    /**
     * Returns the maximum turn speed of the robot
     * @return the maximum turn speed of the robot (rad/s)
     */
    public double getMaxTurnSpeed() {
        return this.maxTurnSpeed;
    }

    /**
     * Sets the maximum turn acceleration of the robot
     * @param maxTurnAcceleration the maximum turn acceleration of the robot (rad/s^2)
     */
    public void setMaxTurnAcceleration(double maxTurnAcceleration) {
        this.maxTurnAcceleration = maxTurnAcceleration;
    }

    /**
     * Returns the maximum turn acceleration of the robot
     * @return the maximum turn acceleration of the robot (rad/s^2)
     */
    public double getMaxTurnAcceleration() {
        return this.maxTurnAcceleration;
    }

    /**
     * Sets the maximum acceleration of the robot
     * @param maxAcceleration the maximum acceleration of the robot (your units/s^2)
     */
    public void setMaxAcceleration(double maxAcceleration) {
        this.maxAcceleration = maxAcceleration;
    }

    /**
     * Returns the maximum acceleration of the robot
     * @return the maximum acceleration of the robot (your units/s^2)
     */
    public double getMaxAcceleration() {
        return this.maxAcceleration;
    }

    /**
     * Sets the turning PID controller of the robot
     * @param turnController the turn controller of the robot
     */
    public void setTurnController(MiniPID turnController) {
        this.turnController = turnController;
    }

    /**
     * Sets the precision of the path follower (how close the robot needs to be to the point to move on)
     * @param precision the precision of the path follower (your units)
     */
    public void setPrecision(double precision) {
        this.precision = precision;
    }

    /**
     * Set the localizer of the robot
     * @param localizer
     */
    public void setLocalizer(Localizer localizer) {
        this.localizer = localizer;
    }

    /**
     * Returns the localizer of the robot
     * @return the localizer of the robot
     */
    public Localizer getLocalizer() {
        return this.localizer;
    }

    /**
     * Sets the distance from the last point that the robot will begin to slowdown
     * @param slowDownDistance the slow down distance of the robot (your units)
     */
    public void setSlowDownDistance(double slowDownDistance) {
        this.slowDownDistance = slowDownDistance;
    }

    /**
     * Sets the kP of the slowdown distance (proporational to the distance)
     * @param kP
     */
    public void setSlowdownKP(double kP) {
        this.kP = kP;
    }

    /**
     * Returns the measured looptime of the path follower (useful for simulation)
     * @return the measured looptime of the path follower (seconds)
     */
    public double getLoopTime() {
        return this.loopTime;
    }

    /**
     * Sets the translation constraints of the path follower
     * @param constraints
     */
    public void setTranslationConstraints(ConstraintsPair constraints) {
        this.speed = constraints.getMaxVelocity();
        this.maxAcceleration = constraints.getMaxAcceleration();
    }

    /**
     * Sets the rotation constraints of the path follower
     * @param constraints
     */
    public void setRotationConstraints(ConstraintsPair constraints) {
        this.maxTurnSpeed = constraints.getMaxVelocity();
        this.maxTurnAcceleration = constraints.getMaxAcceleration();
    }
}
