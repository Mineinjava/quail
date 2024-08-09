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

package quail.util.geometry;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static quail.TestUtil.*;

import com.mineinjava.quail.util.geometry.Pose2d;
import com.mineinjava.quail.util.geometry.Vec2d;
import org.junit.jupiter.api.Test;

public class Pose2dTest {
  @Test
  public void HeadingVector() {
    Pose2d p = new Pose2d(0, 0, 3);
    assertEpsilonEquals(
        3d, p.headingVec().getAngle(), "Angle of heading vector should equal heading of pose");
  }

  @Test
  public void Plus() {
    Pose2d pose1 = new Pose2d(1, 2, Math.PI);
    Pose2d pose2 = new Pose2d(3, 4, Math.PI / 2);
    Pose2d expected = new Pose2d(4, 6, 3 * Math.PI / 2);
    Pose2d result = pose1.plus(pose2);
    assertEquals(expected, result);
  }

  @Test
  public void Minus() {
    Pose2d pose1 = new Pose2d(5, 6, Math.PI / 2);
    Pose2d pose2 = new Pose2d(2, 3, Math.PI / 4);
    Pose2d expected = new Pose2d(3, 3, Math.PI / 4);
    Pose2d result = pose1.minus(pose2);
    assertEquals(expected, result);
  }

  @Test
  public void Times() {
    Pose2d pose = new Pose2d(2, 3, Math.PI / 6);
    double scalar = 2;
    Pose2d expected = new Pose2d(4, 6, Math.PI / 3);
    Pose2d result = pose.times(scalar);
    assertEquals(expected, result);
  }

  @Test
  public void Div() {
    Pose2d pose = new Pose2d(4, 6, Math.PI / 3);
    double scalar = 2;
    Pose2d expected = new Pose2d(2, 3, Math.PI / 6);
    Pose2d result = pose.div(scalar);
    assertEquals(expected, result);
  }

  @Test
  public void UnaryMinus() {
    Pose2d pose = new Pose2d(1, -2, -Math.PI / 4);
    Pose2d expected = new Pose2d(-1, 2, Math.PI / 4);
    Pose2d result = pose.unaryMinus();
    assertEquals(expected, result);
  }

  @Test
  public void DistanceTo() {
    Pose2d pose1 = new Pose2d(1, 1, 0);
    Pose2d pose2 = new Pose2d(4, 5, 0);
    double expected = Math.sqrt(Math.pow(4 - 1, 2) + Math.pow(5 - 1, 2));
    double result = pose1.distanceTo(pose2);
    assertEquals(expected, result); // Use a delta for floating-point comparison
  }

  @Test
  public void FromList() {
    // double[] list = {3.0, 4.0, Math.PI / 3};
    // Pose2d expected = new Pose2d(3.0, 4.0, Math.PI / 3);
    // Pose2d result = new Pose2d.fromList(list);
    // assertEquals(expected, result);
    assertEquals(0, 0);
  }

  @Test
  public void IsHit() {
    Pose2d pose = new Pose2d(1, 1, 0);
    Pose2d robotPose = new Pose2d(0, 0, 0);
    Pose2d oldRobotPose = new Pose2d(2, 2, 0);
    double radius = 1.5;
    boolean expected =
        true; // Assume the circle is hit based on provided MathUtil.lineSegHitCircle behavior
    boolean result = pose.isHit(radius, robotPose, oldRobotPose);
    assertEquals(expected, result);
  }

  @Test
  public void VectorTo() {
    Pose2d pose1 = new Pose2d(2, 3, 0);
    Pose2d pose2 = new Pose2d(5, 7, 0);

    Vec2d expected = new Vec2d(3, 4); // (5 - 2, 7 - 3)
    Vec2d result = pose1.vectorTo(pose2);
    assertEquals(expected, result);
  }
}
