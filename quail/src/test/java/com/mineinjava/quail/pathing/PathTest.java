package com.mineinjava.quail.pathing;

import com.mineinjava.quail.util.geometry.Pose2d;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

class PathTest {

    Pose2d poseStart = new Pose2d(0, 0, 0);
    Pose2d poseSecond = new Pose2d(1, 0, 0);
    Pose2d poseThird = new Pose2d(1, 1, 0);
    Pose2d poseFourth = new Pose2d(0, 1, 0);
    Pose2d poseEnd = new Pose2d(0, 0, 0);




    Path path = new Path(new ArrayList<Pose2d>() {{
        add(poseStart);
        add(poseSecond);
        add(poseThird);
        add(poseFourth);
        add(poseEnd);
    }}
    );

    @BeforeEach
    void setUp() {
        // Reset the path to the beginning
        path.setCurrentPointIndex(0);
    }


    @Test
    void getNextPoint() {
        // Test that at the beginning, the next point is the second point
        assertEquals(poseSecond, path.getNextPoint());

        // Test that if we set to the second to last point, the next point is the last point
        path.setCurrentPointIndex(3);
        assertEquals(poseEnd, path.getNextPoint());

        // Test that if we set to the last point, the next point is null
        path.setCurrentPointIndex(4);
        assertNull(path.getNextPoint());
    }

    @Test
    void getCurrentPoint() {
        // Test that at the beginning, the current point is the first point
        assertEquals(poseStart, path.getCurrentPoint());

        // Test that if we set to the second to last point, the current point is the second to last point
        path.setCurrentPointIndex(3);
        assertEquals(poseFourth, path.getCurrentPoint());

        // Test that if we set to the last point, the current point is the last point
        path.setCurrentPointIndex(4);
        assertEquals(poseEnd, path.getCurrentPoint());

        // Test that
    }

    @Test
    void getPointRelativeToCurrent() {
        // Test that at the beginning, the point 0 relative to the current point is the first point
        assertEquals(poseStart, path.getPointRelativeToCurrent(0));

        // Test that at the beginning, the point -1 relative to the current point is null
        assertNull(path.getPointRelativeToCurrent(-1));

        // Test that at the beginning, the point 5 relative to the current point is null
        assertNull(path.getPointRelativeToCurrent(5));

        // Test that in the middle, the point 0 relative to the current point is the current point
        path.setCurrentPointIndex(2);
        assertEquals(poseThird, path.getPointRelativeToCurrent(0));


    }

    @Test
    void vectorToCurrentPoint() {
    }

    @Test
    void length() {
    }

    @Test
    void distanceToNextPoint() {
    }

    @Test
    void distanceToCurrentPoint() {
    }

    @Test
    void vectorLastToCurrentPoint() {
    }

    @Test
    void distance_last_to_current_point() {
    }

    @Test
    void nextMovementVector() {
    }

    @Test
    void vectorToNearestPoint() {
    }

    @Test
    void nearestPointIndex() {
    }

    @Test
    void nearestPoint() {
    }

    @Test
    void remainingLength() {
    }
}