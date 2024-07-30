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

package quail.util;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.mineinjava.quail.util.MathUtil;
import com.mineinjava.quail.util.geometry.Pose2d;
import java.util.List;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

public class MathUtilTest {
  @Test
  void EpsilionEquals() {
    assertTrue(
        MathUtil.epsilonEquals(0, 1e-7),
        "a difference of less than the epsilon should return true");
    assertFalse(
        MathUtil.epsilonEquals(0, 1e-5),
        "a difference of greater than the epsilon should return false");
  }

  @Test
  void SkewerCircle() {
    assertTrue(
        MathUtil.LineSegHitCircle(
            new Pose2d(0, -1, 0), new Pose2d(0, 1, 0), new Pose2d(0, 0, 0), 0.1),
        "skewer");
  }

  @Test
  @Disabled
  void PopCircle() { // TODO: Fix the function do make this work
    assertTrue(
        MathUtil.LineSegHitCircle(new Pose2d(0, 0, 0), new Pose2d(1, 1, 0), new Pose2d(0, 0, 0), 1),
        "pop");
  }

  @Test
  void InsideCircle() {
    assertFalse(
        MathUtil.LineSegHitCircle(
            new Pose2d(-1, -1, 0), new Pose2d(1, 1, 0), new Pose2d(0, 0, 0), 10),
        "inside");
  }

  @Test
  void MissCircle() {
    assertFalse(
        MathUtil.LineSegHitCircle(
            new Pose2d(-1, -1, 0), new Pose2d(1, 1, 0), new Pose2d(0, -10, 0), 1),
        "miss");
  }

  @Test
  void Lerp() {
    assertEquals(5, MathUtil.lerp(0, 10, 0.5));
  }

  @Test
  void Clamp() {
    assertEquals(0, MathUtil.clamp(-50, 0, 5), "Too Small");
    assertEquals(5, MathUtil.clamp(50, 0, 5), "Too Large");
    assertEquals(2, MathUtil.clamp(2, 0, 5), "Inside");
  }

  @Test
  void GetPairs() {
    Double[] i = {1d, 2d, 3d};
    List<List<Double>> pairs = MathUtil.getPairs(i);
    assertEquals(3, pairs.size());
  }
}
