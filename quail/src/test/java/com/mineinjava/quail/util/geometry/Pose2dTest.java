package com.mineinjava.quail.util.geometry;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

class Pose2dTest {
  private final Pose2d poseXY = new Pose2d(1, 2, 90);
  ;

  /** Test the various pose constructors */
  @Test
  void constructors() {
    Pose2d pose = new Pose2d();
    assertEquals(0, pose.x);
    assertEquals(0, pose.y);
    assertEquals(0, pose.heading);

    pose = new Pose2d(1, 2, 90);
    assertEquals(1, pose.x);
    assertEquals(2, pose.y);
    assertEquals(90, pose.heading);

    pose = new Pose2d(new Vec2d(1, 2), 90);
    assertEquals(1, pose.x);
    assertEquals(2, pose.y);
    assertEquals(90, pose.heading);
  }

  /** Check converting a pose to a position vector */
  @Test
  void vec() {
    assertEquals(new Vec2d(1, 2), poseXY.vec());
  }

  @Test
  void isHit() {
    // Test that we can went through a pose
    Pose2d oldPose = new Pose2d(0, 0, 0);
    Pose2d newPose = new Pose2d(1, 1, 0);
    Pose2d currentPose = new Pose2d(0.5, 0.5, 0);
    assertTrue(currentPose.isHit(0.5, newPose, oldPose));

    // Test that we went through a pose backwards
    oldPose = new Pose2d(1, 1, 0);
    newPose = new Pose2d(0, 0, 0);
    currentPose = new Pose2d(0.5, 0.5, 0);
    assertTrue(currentPose.isHit(0.5, newPose, oldPose));

    // Test that we are at a pose
    oldPose = new Pose2d(0, 0, 0);
    newPose = new Pose2d(1, 1, 0);
    currentPose = new Pose2d(1, 1, 0);
    assertTrue(currentPose.isHit(0.0, newPose, oldPose));

    // Test that radius is enforced if we are too far away
    oldPose = new Pose2d(0, 0, 0);
    newPose = new Pose2d(1, 1, 0);
    currentPose = new Pose2d(0.5, 0.4, 0);
    assertFalse(currentPose.isHit(0.05, newPose, oldPose));
  }
}
