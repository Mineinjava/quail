package com.mineinjava.quail.localization;

import com.mineinjava.quail.util.geometry.Pose2d;

public interface Localizer {

    public Pose2d getPose();

    public void setPose(Pose2d pose);
}
