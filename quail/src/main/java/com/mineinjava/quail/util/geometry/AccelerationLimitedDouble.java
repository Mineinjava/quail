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

public class AccelerationLimitedDouble {

  double idealValue = 0;
  double currentValue = 0;
  double maxAcceleration = 0;
  double looptime = 0.02;
  double lastTime = 0;

  /**
   * Creates a new AccelerationLimitedValue
   *
   * @param idealValue The Value to ramp to
   * @param currentValue The current Value
   * @param maxAcceleration The maximum acceleration
   */
  public AccelerationLimitedDouble(double idealValue, double currentValue, double maxAcceleration) {
    this.idealValue = idealValue;
    this.currentValue = currentValue;
    this.maxAcceleration = maxAcceleration;
  }

  /**
   * Creates a new AccelerationLimitedValue
   *
   * @param maxAcceleration The maximum acceleration
   */
  public AccelerationLimitedDouble(double maxAcceleration) {
    this.idealValue = 0;
    this.currentValue = 0;
    this.maxAcceleration = maxAcceleration;
  }

  /**
   * Gets the ideal Value
   *
   * @return current ideal Value
   */
  public double getIdealValue() {
    return idealValue;
  }

  /**
   * calculates a new output Value based on the time since the last update
   *
   * @return
   */
  public double update() {
    if (this.lastTime == 0) {
      this.lastTime = System.currentTimeMillis() - 1;
    }
    this.looptime = (System.currentTimeMillis() - this.lastTime) / 1000.0;

    double accelerationValue =
        (idealValue - currentValue)
            / looptime; // idealValue.subtract(currentValue).scale(1/looptime);
    if (accelerationValue > maxAcceleration) {
      accelerationValue = maxAcceleration;
    }
    currentValue = currentValue + accelerationValue;
    return currentValue;
  }

  /**
   * updates the ideal Value and calculates a new output Value based on the time since the last
   * update
   *
   * @param newIdealValue The new ideal Value
   * @return
   */
  public double update(double newIdealValue) {
    this.idealValue = newIdealValue;
    return update();
  }
}
