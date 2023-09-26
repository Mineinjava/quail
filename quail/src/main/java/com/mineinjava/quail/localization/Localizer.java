package com.mineinjava.quail.localization;

import com.mineinjava.quail.util.geometry.Pose2d;

public abstract class Localizer {
    
    Pose2d getPoseEstimate() { return null; }

    void setPoseEstimate(Pose2d pose) {};
}
