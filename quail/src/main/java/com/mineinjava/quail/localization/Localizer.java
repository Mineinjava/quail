package com.mineinjava.quail.localization;

import com.mineinjava.quail.util.geometry.Pose2d;

public interface Localizer {

    public Pose2d getPoseEstimate();

    public ArrayList<Pose2d> poseHistory;

    public void setPoseEstimate(Pose2d pose);
}
