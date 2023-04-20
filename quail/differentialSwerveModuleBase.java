package quail;

public class differentialSwerveModuleBase extends swerveModuleBase {

    public differentialSwerveModuleBase(Vec2d position, double steeringRatio, double driveRatio) {
        super(position, steeringRatio, driveRatio);
    }

    public differentialSwerveModuleBase(Vec2d position, double steeringRatio, double driveRatio, boolean optimized) {
        super(position, steeringRatio, driveRatio, optimized);
    }
}
