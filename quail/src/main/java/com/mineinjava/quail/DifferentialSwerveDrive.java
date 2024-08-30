package com.mineinjava.quail;

import java.util.List;

import com.mineinjava.quail.util.geometry.Vec2d;

public class DifferentialSwerveDrive extends SwerveDrive {
    List<DifferentialSwerveModuleBase> swerveModules;
    double maxSpeed;
    public DifferentialSwerveDrive(List swerveModules) {
        super(swerveModules);
        this.swerveModules = swerveModules;
    }
    public DifferentialSwerveDrive(List swerveModules, double maxSpeed){
        super(swerveModules,maxSpeed);
        this.swerveModules = swerveModules;
        this.maxSpeed = maxSpeed;
    }   

    @Override
    public void move(RobotMovement movement, double gyroOffset) {
    Vec2d[] moduleVectors =
        calculateMoveAngles(movement.translation, movement.rotation, gyroOffset);
    moduleVectors = normalizeModuleVectors(moduleVectors, this.maxSpeed);
    for (int i = 0; i < this.swerveModules.size(); i++) {
      swerveModules.get(i).set(moduleVectors[i]);
    }
    }


}
