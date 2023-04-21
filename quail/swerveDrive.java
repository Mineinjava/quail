package quail;
import java.util.List;


public class swerveDrive {
    public final List<swerveModuleBase> swerveModules;
    /** Represents a swerve drive
     * @param swerveModules a list of swerve modules
     */
    public swerveDrive(List<swerveModuleBase> swerveModules) {
        this.swerveModules = swerveModules;
    }
    /**
     * @param moveVector the vector to move in
     * @param rotationSpeed speed of rotation
     * @param centerPoint modified center of rotation. Pass in Vec2d(0, 0) for default center of rotation
     * @param gyroOffset the gyro rotation in radians
     */
    public Vec2d[] calculateMoveAngles(Vec2d moveVector, double rotationSpeed, double gyroOffset, Vec2d centerPoint) {
        moveVector = moveVector.rotate(gyroOffset, false);
         // create a list of four vec2d objects and iterate over them with a for loop (not foreach)
        Vec2d[] moduleVectors = new Vec2d[this.swerveModules.size()];
        for (int i = 0; i < 4; i++) {
            swerveModuleBase module = swerveModules.get(i);
            Vec2d moduleOffCenterVector = module.position.subtract(centerPoint);
            moduleVectors[i] = moveVector.add(moduleOffCenterVector.scale(rotationSpeed));
        }
        return moduleVectors;
    }

    /**
     * @param moveVector the vector to move in
     * @param rotationSpeed speed of rotation
     * @param gyroOffset the gyro rotation in radians
     */
    public Vec2d[] calculateMoveAngles(Vec2d moveVector, double rotationSpeed, double gyroOffset) {
        return calculateMoveAngles(moveVector, rotationSpeed, gyroOffset, new Vec2d(0, 0));
    }

    /**
     * @param moduleVectors list of module vectors to be normalized
     * @param maxAllowableMagnitude the maximum magnitude of the vectors
     * @return an array of vectors appropriately scaled so that the largest vector has a magnitude no greater than `maxAllowableMagnitude`.
     */
    public Vec2d[] normalizeModuleVectors(Vec2d[] moduleVectors, double maxAllowableMagnitude) {
        double maxMagnitude = 0;
        for (Vec2d moduleVector : moduleVectors) {
            if (moduleVector.getLength() > maxMagnitude) {
                maxMagnitude = moduleVector.getLength();
            }
        }
        if (maxMagnitude > maxAllowableMagnitude) {
            for (int i = 0; i < 4; i++) {
                moduleVectors[i] = moduleVectors[i].scale(maxAllowableMagnitude / maxMagnitude);
            }
        }
        return moduleVectors;
    }
    /**
     * @param moduleVectors list of module vectors to be normalized
     * @return an array of vectors appropriately scaled so that the largest vector has a magnitude no greater than 1.
     */
    public Vec2d[] normalizeModuleVectors(Vec2d[] moduleVectors) {
        return normalizeModuleVectors(moduleVectors, 1);
    }
}