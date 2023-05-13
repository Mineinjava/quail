package com.mineinjava.quail;

import java.util.List;

import com.mineinjava.quail.util.Vec2d;


public class swerveDrive<T extends swerveModuleBase> {
    public final List<T> swerveModules;
    /** Represents a swerve drive
     * Designed to be inherited from. While it will work without being inherited from, you may want to add some features such as:
     * - reset gyro, both from controller and from vision odometry
     * - reset module positions
     * - X-lock modules
     * - set module positions (hopefully your swerve modules are not moving relative to your robot)
     * - acceleration limiting
     * Normal use of this class would look something like:
     * - create a list of swerve modules
     * - create a swerveDrive object with the list of swerve modules
     * - every time you want to move, call calculateMoveAngles() and normalize the returned values
     * - obtain the module vectors from the swerve modules and pass them into the swerveOdometry class (optional)
     * - pass the normalized vectors into the swerve modules
     *
     * @param swerveModules a list of swerve modules
     */
    public swerveDrive(List<T> swerveModules) {
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
        for (int i = 0; i < this.swerveModules.size(); i++) {
            swerveModuleBase module = swerveModules.get(i);
            Vec2d moduleOffCenterVector = module.position.subtract(centerPoint);
            moduleVectors[i] = moveVector.add(moduleOffCenterVector.scale(rotationSpeed));
        }
        return moduleVectors;
    }
    public void move(robotMovement movement, double gyroOffset){
        Vec2d[] moduleVectors = calculateMoveAngles(movement.translation, movement.rotation, gyroOffset);
        moduleVectors = normalizeModuleVectors(moduleVectors);
        for (int i=0; i < this.swerveModules.size(); i++){
            swerveModules.get(i).set(moduleVectors[i]);
        }
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
     * @param maxAllowableMagnitude the maximum magnitude of the vectors (this function clamps the largest vector to
     *                              this magnitude and scales the rest--this parameter is the upper limit on the clamp)
     * @return an array of vectors appropriately scaled so that the largest vector has a magnitude no greater than `maxAllowableMagnitude`.
     */
    public Vec2d[] normalizeModuleVectors(Vec2d[] moduleVectors, double maxAllowableMagnitude) {
        double maxMagnitude = 0;
        for (Vec2d moduleVector : moduleVectors) {
            if (moduleVector.getLength() > maxMagnitude) {
                maxMagnitude = moduleVector.getLength();
            }
        }
        if ((maxMagnitude > maxAllowableMagnitude) && (maxMagnitude !=0d)) {
            for (int i = 0; i < this.swerveModules.size(); i++) {
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
    public void XLockModules() {
        for (swerveModuleBase module : this.swerveModules) {
            module.XLock();
        }
    }
}