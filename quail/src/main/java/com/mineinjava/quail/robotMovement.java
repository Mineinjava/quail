package com.mineinjava.quail;

import com.mineinjava.quail.util.Vec2d;

/** Can represent a lot of things. Generally represents a vector (robot position, movement, etc.), plus an angle (robot rotation, robot desired rotation, etc.)
 * Everything should be fairly self-explanatory.
 */
public class robotMovement {
    public double rotation;
    public Vec2d translation;

    public robotMovement(double rotation, Vec2d translation) {
        this.rotation = rotation;
        this.translation = translation;
    }

    public robotMovement(double rotation, double translationX, double translationY) {
        this(rotation, new Vec2d(translationX, translationY));
    }

    public robotMovement(Vec2d translation) {
        this(0, translation);
    }

    public robotMovement(double rotation) {
        this(rotation, 0, 0);
    }

    public robotMovement() {
        this(0, 0, 0);
    }

    public robotMovement add(robotMovement other) {
        return new robotMovement(this.rotation + other.rotation, this.translation.add(other.translation));
    }

    public robotMovement subtract(robotMovement other) {
        return new robotMovement(this.rotation - other.rotation, this.translation.subtract(other.translation));
    }
}
