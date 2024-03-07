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

package com.mineinjava.quail.util;

import java.util.ArrayList;
import java.util.List;

public class Util {

  public static double floormod(double a, double b) {
    return a - (b * Math.floor(a / b));
  }

  /**
   * Calculates the smallest angle between two angles Useful for determining how far and in which
   * direction to rotate anything angle returned is in radians, counterclockwise from angle 1 to
   * angle 2
   *
   * @param angle1
   * @param angle2
   * @return
   */
  public static double deltaAngle(double angle1, double angle2) {
    // calculate the smallest angle to rotate between the current angle and the target angle
    double deltaAngle = angle2 - angle1;
    deltaAngle = floormod(deltaAngle + Math.PI, 2 * Math.PI) - Math.PI;
    return deltaAngle % (2 * Math.PI);
  }

  /**
   * clamps a value between a min and a max
   *
   * @param value
   * @param min
   * @param max
   * @return
   */
  public static double clamp(double value, double min, double max) {
    // clamp a value between a minimum and maximum
    if (value < min) {
      return min;
    } else if (value > max) {
      return max;
    } else {
      return value;
    }
  }

  /** returns a list of all the pairs that can be formed from a list */
  public static <T> List<List<T>> getPairs(T[] inputArray) {
    List<List<T>> pairs = new ArrayList<>();
    for (int i = 0; i < inputArray.length; i++) {
      for (int j = i + 1; j < inputArray.length; j++) {
        List<T> pair = new ArrayList<>();
        pair.add(inputArray[i]);
        pair.add(inputArray[j]);
        pairs.add(pair);
      }
    }
    return pairs;
  }
}
