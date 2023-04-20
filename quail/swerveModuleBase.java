package quail;

import java.lang.Math;

public class swerveModuleBase {
    public Vec2d position;
    private final double steeringRatio;
    private final double driveRatio;
    public boolean optimized;
    public Vec2d steeringVector;
    private double currentAngle; // in radians
    private int motorFlipper = 1; // optimization for motor rotation

    public swerveModuleBase(Vec2d position, double steeringRatio, double driveRatio) {
        // default optimized value is true
        this(position, steeringRatio, driveRatio, true);
    }

    public swerveModuleBase(Vec2d position, double steeringRatio, double driveRatio, boolean optimized) {
        this.position = position;
        this.steeringRatio = steeringRatio;
        this.driveRatio = driveRatio;
        this.optimized = optimized;
        steeringVector = new Vec2d(this.position.getAngle(), this.position.getLength(), false);
    }

    public double calculateNewAngleSetpoint(double angle) {
        // calculate the angle distance
        double shortestAngle = angle - currentAngle;
        // make sure the angle is between -pi and pi (don't rotate more than 180 degrees)
        if (shortestAngle > Math.PI) {
            shortestAngle -= 2 * Math.PI;
        } else if (shortestAngle < -Math.PI) {
            shortestAngle += 2 * Math.PI;
        }
        // calculate the angle to turn
        return currentAngle = currentAngle + shortestAngle;
    }

    // "optimized" motor rotation: if the angle is greater than 90 degrees, rotate the motor in the opposite direction
    // and rotate less than 90 degrees
    public double calculateOptimizedAngle(double angle) {
        if (Math.abs(angle) > Math.PI / 2) {
            this.motorFlipper = -this.motorFlipper;
            return angle - Math.PI;
        } else {
            return angle;
        }
    }

    public double angle() {
        double setpoint;
        if (optimized) {
            setpoint = calculateOptimizedAngle(currentAngle);
        } else {
            setpoint = currentAngle;
        }
        return calculateNewAngleSetpoint(setpoint);
    }

    public void setAngle(double angle) {
        System.out.println("Default quail.swerveModuleBase.setAngle() called. Override me!");
    }

    public void setRawSpeed(double speed) {
        System.out.println("Default quail.swerveModuleBase.setRawSpeed() called. Override me!");
    }

    public void setSpeed(double speed) {
        setRawSpeed(this.motorFlipper * speed);
    }

    public void set(Vec2d vec) {
        setAngle(vec.getAngle());
        setSpeed(vec.getLength());
    }
}
