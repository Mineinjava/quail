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

package com.mineinjava.quail.util.geometry;

/** 
 * Various utilities for working with angles. 
 * <p> ALL angles MUST be in ccw+ radians 
 * */
public class Angle {
  private static final double TAU = 2 * Math.PI;

  /**
   * Returns {@code angle} clamped to {@code [0, 2pi]}.
   *
   * @param angle angle measure in radians
   */
  public static double norm(double angle) {
    double modifiedAngle = angle % TAU;

    modifiedAngle = (modifiedAngle + TAU) % TAU;

    return modifiedAngle;
  }

  /**
   * Returns {@code angleDelta} clamped to {@code [-pi, pi]}.
   *
   * @param angleDelta angle delta in radians
   */
  public static double normDelta(double angleDelta) {
    double modifiedAngleDelta = norm(angleDelta);

    if (modifiedAngleDelta > Math.PI) {
      modifiedAngleDelta -= TAU;
    }

    return modifiedAngleDelta;
  }
}
