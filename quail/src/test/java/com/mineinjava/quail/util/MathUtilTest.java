package com.mineinjava.quail.util;

import static org.junit.jupiter.api.Assertions.*;

import com.mineinjava.quail.util.geometry.Pose2d;
import org.junit.jupiter.api.Test;

class MathUtilTest {

    @Test
    void epsilonEquals() {
        // Compare values within constants.EPSILON
        assertTrue(MathUtil.epsilonEquals(1.0, 1.0 + Constants.EPSILON / 2));

        // Compare values outside of constants.EPSILON
        assertFalse(MathUtil.epsilonEquals(1.0, 1.0 + Constants.EPSILON * 2));
    }

    @Test
    void lineSegHitCircle() {
        // Test that a line segment hits a circle on the line
        Pose2d start = new Pose2d(0, 0, 0);
        Pose2d end = new Pose2d(1, 1, 0);

        assertTrue(MathUtil.lineSegHitCircle(start, end, new Pose2d(0.5, 0.5, 0), 0.0));

        // Test a line segment with a circle within radius of the middle of the line
        assertTrue(MathUtil.lineSegHitCircle(start, end, new Pose2d(0.45, 0.45, 0), 0.1));

        // Test a line segment with a circle outside of the radius of the middle of the line
        assertFalse(MathUtil.lineSegHitCircle(start, end, new Pose2d(0.5, 0.25, 0), 0.1));

        // Test a circle within radius of the start point
        assertTrue(MathUtil.lineSegHitCircle(start, end, new Pose2d(0.01, 0.01, 0), 0.1));

        // Test a circle outside the radius of the start point
        assertFalse(MathUtil.lineSegHitCircle(start, end, new Pose2d(0, -0.05, 0), 0.001));

        // Test a circle within radius of the end point
        assertTrue(MathUtil.lineSegHitCircle(start, end, new Pose2d(0.95, 0.95, 0), 0.1));

        // Test a circle outside of the radius of the end point
        assertFalse(MathUtil.lineSegHitCircle(start, end, new Pose2d(1, 1.05, 0), 0.025));

        // Test a point far away but with a radius that intersects the line
        assertTrue(MathUtil.lineSegHitCircle(start, end, new Pose2d(0, 50, 0), 100));
    }

    @Test
    void lerp() {
        // Test lerp at 0
        assertEquals(0, MathUtil.lerp(0, 1, 0));

        // Test lerp at 1
        assertEquals(1, MathUtil.lerp(0, 1, 1));

        // Test lerp at 0.5
        assertEquals(0.5, MathUtil.lerp(0, 1, 0.5));
    }

    /**
     * Basically test that our double floormod function works the same as the built-in floormod for ints
     */
    @Test
    void floormod() {
        assertEquals(Math.floorMod(1, 2), MathUtil.floormod(1, 2));
        assertEquals(Math.floorMod(2, 2), MathUtil.floormod(2, 2));
        assertEquals(Math.floorMod(3, 2), MathUtil.floormod(3, 2));
        assertEquals(Math.floorMod(4, -2), MathUtil.floormod(4, -2));
        assertEquals(Math.floorMod(-1, 2), MathUtil.floormod(-1, 2));
        assertEquals(Math.floorMod(-2, 2), MathUtil.floormod(-2, 2));
        assertEquals(Math.floorMod(-3, 2), MathUtil.floormod(-3, 2));
    }

    /**
     * Test that we get the right smallest angle in radians`
     */
    @Test
    void deltaAngle() {
        assertEquals(0, MathUtil.deltaAngle(0, 0));
        assertEquals(0, MathUtil.deltaAngle(0, 2 * Math.PI));
        assertEquals(0, MathUtil.deltaAngle(0, -2 * Math.PI));
        assertEquals(-Math.PI, MathUtil.deltaAngle(0, Math.PI));
        assertEquals(-Math.PI, MathUtil.deltaAngle(Math.PI, 0));
    }

    @Test
    void clamp() {
        assertEquals(0, MathUtil.clamp(-1, 0, 1));
        assertEquals(0, MathUtil.clamp(0, 0, 1));
        assertEquals(1, MathUtil.clamp(1, 0, 1));
        assertEquals(1, MathUtil.clamp(2, 0, 1));
    }
}
