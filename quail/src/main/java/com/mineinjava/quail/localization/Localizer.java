package com.mineinjava.quail.localization;

import com.mineinjava.quail.util.geometry.Pose2d;

public abstract class Localizer {
    
    public Pose2d getPoseEstimate() { return new Pose2d(0,0,0); }

    public void setPoseEstimate(Pose2d pose) {};
}
