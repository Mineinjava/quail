package quail;

public class differentialSwerveModuleBase extends swerveModuleBase {

    public differentialSwerveModuleBase(Vec2d position, double steeringRatio, double driveRatio) {
        super(position, steeringRatio, driveRatio);
    }

    public differentialSwerveModuleBase(Vec2d position, double steeringRatio, double driveRatio, boolean optimized) {
        super(position, steeringRatio, driveRatio, optimized);
    }

    /**
     * Calculates the motor speeds for a differential swerve module
     * @param rotationSpeed the current rotation speed of the pod
     * @param wheelSpeed the current speed of the wheel
     * @return motor speeds (array of length 2)
     */
    public double[] calculateMotorSpeeds(double rotationSpeed, double wheelSpeed) {

        double [] motorSpeeds = new double[2];

        double adjustedRotationSpeed = rotationSpeed / (2 * steeringRatio);
        double adjustedWheelSpeed = wheelSpeed / (2 * driveRatio);

        motorSpeeds[0] = adjustedRotationSpeed + adjustedWheelSpeed;
        motorSpeeds[1] = adjustedRotationSpeed - adjustedWheelSpeed;
        
        return motorSpeeds;
    }
}
