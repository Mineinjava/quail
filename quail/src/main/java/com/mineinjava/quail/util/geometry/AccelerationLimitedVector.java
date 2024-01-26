package com.mineinjava.quail.util.geometry;

/**
 * Represents a vector with input ramping to control acceleration
 * Useful for joystick ramp limiting
 */
public class AccelerationLimitedVector{
    Vec2d idealVector;
    Vec2d currentVector;
    double maxAcceleration;
    double looptime;
    double lastTime = 0;
    /**
     * Creates a new AccelerationLimitedVector
     * @param idealVector The vector to ramp to
     * @param currentVector The current vector
     * @param maxAcceleration The maximum acceleration
     */
    public AccelerationLimitedVector(Vec2d idealVector, Vec2d currentVector, double maxAcceleration) {
        this.idealVector = idealVector;
        this.currentVector = currentVector;
        this.maxAcceleration = maxAcceleration;
    }
    /**
     * Creates a new AccelerationLimitedVector
     * @param maxAcceleration The maximum acceleration
     */
    public AccelerationLimitedVector(double maxAcceleration) {
        this.idealVector = new Vec2d(0,0);
        this.currentVector = new Vec2d(0,0);
        this.maxAcceleration = maxAcceleration;
    }

    /**
     * Gets the ideal vector
     * @return current ideal vector
     */
    public Vec2d getIdealVector(){
        return idealVector;
    }

    /**
     * calculates a new output vector based on the time since the last update
     * @return
     */
    public Vec2d update(){
        if (this.lastTime == 0){
            this.lastTime = System.currentTimeMillis();
        }
        this.looptime = (System.currentTimeMillis() - this.lastTime)/1000.0;

        Vec2d accelerationVector = idealVector.subtract(currentVector).scale(1/looptime);
        if(accelerationVector.getLength() > maxAcceleration){
            accelerationVector = accelerationVector.normalize().scale(maxAcceleration);
        }
        currentVector = currentVector.add(accelerationVector.scale(looptime));
        return currentVector;
    }

    /**
     * updates the ideal vector and calculates a new output vector based on the time since the last update
     * @param newIdealVector The new ideal vector
     * @return
     */
    public Vec2d update(Vec2d newIdealVector) {
        this.idealVector = newIdealVector;
        return update();
    }
}
