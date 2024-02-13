package com.mineinjava.quail;

import com.mineinjava.quail.util.geometry.Vec2d;

/**
 * Can represent a lot of things. Generally represents a vector (robot position, movement, etc.),
 * plus an angle (robot rotation, robot desired rotation, etc.) Everything should be fairly
 * self-explanatory.
 */
public class RobotMovement {
  public double rotation;
  public Vec2d translation;

  public RobotMovement(double rotation, Vec2d translation) {
    this.rotation = rotation;
    this.translation = translation;
  }

  public RobotMovement(double rotation, double translationX, double translationY) {
    this(rotation, new Vec2d(translationX, translationY));
  }

  public RobotMovement(Vec2d translation) {
    this(0, translation);
  }

  public RobotMovement(double rotation) {
    this(rotation, 0, 0);
  }

  public RobotMovement() {
    this(0, 0, 0);
  }

  public RobotMovement add(RobotMovement other) {
    return new RobotMovement(
        this.rotation + other.rotation, this.translation.add(other.translation));
  }

  public RobotMovement subtract(RobotMovement other) {
    return new RobotMovement(
        this.rotation - other.rotation, this.translation.subtract(other.translation));
  }
}
