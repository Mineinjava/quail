package quail;
import java.util.List;

public class swerveDrive {
    public final List<swerveModuleBase> swerveModules;
    public swerveDrive(List<swerveModuleBase> swerveModules) {
        this.swerveModules = swerveModules;
    }
    /**
     * @param moveVector the vector to move in
     * @param rotationSpeed speed of rotation
     * @param gyroOffset the gyro rotation in radians
     */
    public Vec2d[] calculateMoveAngles(Vec2d moveVector, double rotationSpeed, double gyroOffset) {
        moveVector = moveVector.rotate(gyroOffset, false);
         // create a list of four vec2d objects and iterate over them with a for loop (not foreach)
        Vec2d[] moduleVectors = new Vec2d[this.swerveModules.size()];
        for (int i = 0; i < 4; i++) {
            swerveModuleBase module = swerveModules.get(i);
            moduleVectors[i] = moveVector.add(module.steeringVector.scale(rotationSpeed));
        }
        return moduleVectors;
    }
}