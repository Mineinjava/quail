// Copyright (C) Marcus Kauffman 2023-Present

// This work would not have been possible without the work of many
// contributors, most notably Colin Montigel. See ACKNOWLEDGEMENT.md for
// more details.

// This file is part of Quail.

// Quail is free software: you can redistribute it and/or modify it
// underthe terms of the GNU General Public License as published by the
// Free Software Foundation, version 3.

// Quail is distributed in the hope that it will be useful, but WITHOUT
// ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
// FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License
// for more details.

// You should have received a copy of the GNU General Public License
// along with Quail. If not, see <https://www.gnu.org/licenses/>

package com.mineinjava.quail;

import com.mineinjava.quail.util.geometry.Vec2d;

/**
 * Can represent a lot of things.
 * <p>Generally represents a vector (robot position, movement, etc.),
 * plus an angle (robot rotation, robot desired rotation, etc.) 
 * <p>Everything should be fairly self-explanatory.
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
