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
