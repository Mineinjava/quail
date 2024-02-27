package com.mineinjava.quail.pathing;

import static org.junit.jupiter.api.Assertions.*;

import com.mineinjava.quail.util.geometry.Pose2d;
import com.mineinjava.quail.util.geometry.Vec2d;
import java.util.ArrayList;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class PathTest {

  Pose2d poseStart = new Pose2d(0, 0, 0);
  Pose2d poseSecond = new Pose2d(1, 0, 0);
  Pose2d poseThird = new Pose2d(1, 1, 0);
  Pose2d poseFourth = new Pose2d(0, 1, 0);
  Pose2d poseEnd = new Pose2d(0, 0, 0);

  Path path;

  @BeforeEach
  void setUp() {
    path =
        new Path(
            new ArrayList<Pose2d>() {
              {
                add(poseStart);
                add(poseSecond);
                add(poseThird);
                add(poseFourth);
                add(poseEnd);
              }
            });
  }

  private void movePathIndexForward(int times) {
    for (int i = 0; i < times; i++) {
      path.incrementCurrentPointIndex();
    }
  }

  /**
   * Test the core logic that the path is not finished until we increment the current point index at
   * the last point
   */
  @Test
  void incrementCurrentPointIndex() {
    // Assert we are at the beginning
    assertFalse(path.isFinished());
    assertEquals(0, path.getCurrentPointIndex());

    // Test that incrementing the current point index works
    for (int i = 0; i < 4; i++) {
      path.incrementCurrentPointIndex();
      assertEquals(i + 1, path.getCurrentPointIndex());
      assertFalse(path.isFinished());
    }

    // Test that incrementing the current point index when we are at the end finishes the path
    path.incrementCurrentPointIndex();
    assertTrue(path.isFinished());

    // Test that incrementing the current point index when we are finished throws an exception
    assertThrows(IllegalStateException.class, path::incrementCurrentPointIndex);
  }

  @Test
  void getNextPoint() {
    // Test that at the beginning, the next point is the second point
    assertEquals(poseSecond, path.getNextPoint());

    // Test that if we set to the second to last point, the next point is the last point
    movePathIndexForward(3);
    assertEquals(poseEnd, path.getNextPoint());

    // Test that if we set to the last point, the next point is null
    movePathIndexForward(1);
    assertFalse(
        path.isFinished()); // Make sure we aren't falling into the case the path is finished
    assertNull(path.getNextPoint());

    // Test that if we are finished, the next point is null
    path.finishPath();
    assertNull(path.getNextPoint());
  }

  @Test
  void getCurrentPoint() {
    // Test that at the beginning, the current point is the first point
    assertEquals(poseStart, path.getCurrentPoint());

    // Test that if we set to the second to last point, the current point is the second to last
    // point
    movePathIndexForward(3);
    assertEquals(poseFourth, path.getCurrentPoint());

    // Test that if we set to the last point, the current point is the last point
    movePathIndexForward(1);
    assertFalse(
        path.isFinished()); // Make sure we aren't falling into the case the path is finished
    assertEquals(poseEnd, path.getCurrentPoint());

    // Test that if we are finished, the current point is null
    path.finishPath();
    assertNull(path.getCurrentPoint());
  }

  @Test
  void getPointRelativeToCurrent() {
    // Test that at the beginning, the point 0 relative to the current point is the first point
    assertEquals(poseStart, path.getPointRelativeToCurrent(0));

    // Test that at the beginning, the point -1 relative to the current point is null
    assertThrows(IllegalArgumentException.class, () -> path.getPointRelativeToCurrent(-1));

    // Test that at the beginning, the point 5 relative to the current point throws an exception
    assertThrows(IllegalArgumentException.class, () -> path.getPointRelativeToCurrent(5));

    // Test that in the middle, the 0th relative point to the current point is the current point
    movePathIndexForward(2);
    assertEquals(poseThird, path.getPointRelativeToCurrent(0));

    // Test that in the middle, the point 1 forward to the current point is the next point
    assertEquals(poseFourth, path.getPointRelativeToCurrent(1));

    // Test that in the middle, the point -1 back to the current point is the previous point
    assertEquals(poseSecond, path.getPointRelativeToCurrent(-1));

    // Test that at the end, the point 0 relative to the current point is the last point
    movePathIndexForward(2);
    assertEquals(poseEnd, path.getPointRelativeToCurrent(0));

    // Test that at the end, the point 1 forward to the current point throws an exception
    assertThrows(IllegalArgumentException.class, () -> path.getPointRelativeToCurrent(1));
  }

  @Test
  void vectorToCurrentPoint() {
    // Test going to poseThird from 0, 0
    path.incrementCurrentPointIndex();
    path.incrementCurrentPointIndex();
    assertEquals(poseThird.vec(), path.vectorToCurrentPoint(new Pose2d(0, 0, 0)));

    // Test going to poseThird from 1, 1
    assertEquals(new Vec2d(0, 0), path.vectorToCurrentPoint(new Pose2d(1, 1, 0)));

    // Test that when the path is finished, the vector to the current point is null
    path.finishPath();
    assertNull(path.vectorToCurrentPoint(new Pose2d(0, 0, 0)));
  }

  @Test
  void length() {
    // Test that the length of the path is the sum of the distances between each point
    //  this is a square with side length 1, so the length should be 4
    assertEquals(4, path.length());
  }

  @Test
  void distanceToNextPoint() {}

  @Test
  void distanceToCurrentPoint() {}

  @Test
  void vectorLastToCurrentPoint() {}

  @Test
  void distance_last_to_current_point() {}

  @Test
  void nextMovementVector() {}

  @Test
  void vectorToNearestPoint() {}

  @Test
  void nearestPointIndex() {}

  @Test
  void nearestPoint() {}

  @Test
  void remainingLength() {}
}
