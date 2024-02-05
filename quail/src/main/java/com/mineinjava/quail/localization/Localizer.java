package com.mineinjava.quail.localization;

import com.mineinjava.quail.util.geometry.Pose2d;
/**
 * Interface for code that gets the robot's position and/or velocity
 * TODO: make a getVelocity method
 */
public interface Localizer {

    public Pose2d getPose();

    public void setPose(Pose2d pose);
}
