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

import static org.junit.jupiter.api.Assertions.*;

import com.mineinjava.quail.util.MathUtil;
import com.mineinjava.quail.util.geometry.Vec2d;
import org.junit.jupiter.api.Test;

public class Vec2dTest {
  @Test
  void UndefinedResolution() {
    assertEquals(new Vec2d(0, 0), new Vec2d(Double.NaN, Double.NaN));
  }

  @Test
  void PolarToCartesianInstantiation() {
    Vec2d vec1 = new Vec2d(0, 1);
    Vec2d vec2 = new Vec2d(Math.PI / 2, 1, false);
    assertTrue(MathUtil.epsilonEquals(vec1.x, vec2.x));
    assertTrue(MathUtil.epsilonEquals(vec1.y, vec2.y));
  }

  @Test
  void VecFromArray() {
    Vec2d vec1 = new Vec2d(1, 1);
    Vec2d vec2 = new Vec2d(new double[] {1, 1});
    assertEquals(vec1, vec2);
  }

  @Test
  void Normalize() {
    Vec2d vec = new Vec2d(1, 1);
    vec = vec.normalize();
    assertTrue(MathUtil.epsilonEquals(vec.getLength(), 1));
  }

  @Test
  void ZeroLengthIsNaNAngle() {
    Vec2d vec = new Vec2d(0, 0);
    assertEquals(vec.getAngle(), Double.NaN);
  }

  @Test
  void AngleSimilarity() {
    Vec2d vec1 = new Vec2d(1, 1);
    Vec2d vec2 = new Vec2d(2, 2);
    Vec2d vec3 = new Vec2d(-0.5, -0.5);

    assertTrue(MathUtil.epsilonEquals(1, vec1.angleSimilarity(vec2)));
    assertTrue(MathUtil.epsilonEquals(0, vec1.angleSimilarity(vec3)));
  }

  @Test
  void Scale() {
    Vec2d vec = new Vec2d(0.3, 1);
    assertEquals(vec.scale(10), new Vec2d(3, 10));
  }

  @Test
  void Length() {
    Vec2d vec1 = new Vec2d(1, 1);
    Vec2d vec2 = new Vec2d(0, 0);
    Vec2d vec3 = new Vec2d(1, 0);

    assertEquals(Math.sqrt(2), vec1.getLength());
    assertEquals(0, vec2.getLength());
    assertEquals(1, vec3.getLength());
  }

  @Test
  void LengthSquared() {
    Vec2d vec1 = new Vec2d(1, 1);
    Vec2d vec2 = new Vec2d(0, 0);
    Vec2d vec3 = new Vec2d(1, 0);

    assertEquals(2, vec1.getLengthSquared());
    assertEquals(0, vec2.getLengthSquared());
    assertEquals(1, vec3.getLengthSquared());
  }

  @Test
  void Add() {
    Vec2d vec1 = new Vec2d(10, 1);
    Vec2d vec2 = new Vec2d(2, 20);

    assertEquals(new Vec2d(12, 21), vec1.add(vec2));
    assertEquals(new Vec2d(12, 21), vec2.add(vec1));
    assertEquals(new Vec2d(10.1, 1.2), vec1.add(0.1, 0.2));
  }

  @Test
  void Subtract() {
    Vec2d vec1 = new Vec2d(10, 1);
    Vec2d vec2 = new Vec2d(2, 20);

    assertEquals(new Vec2d(8, -19), vec1.subtract(vec2));
    assertEquals(new Vec2d(-8, 19), vec2.subtract(vec1));
    assertEquals(new Vec2d(9.9, 0.8), vec1.subtract(0.1, 0.2));
  }

  @Test
  void GetAngle() {
    Vec2d vec1 = new Vec2d();
    Vec2d vec2 = new Vec2d(0, 1);
    Vec2d vec3 = new Vec2d(0, -1);

    assertEquals(0, vec1.getAngle(), "<0,1> has 0 angle");
    assertEquals(Math.PI / 2, vec2.getAngle(), "<0,1> has pi/2 angle");
    assertEquals(Math.PI * 3 / 2, vec3.getAngle(), "<0, -1> has 3pi/2 angle");
  }

  @Test
  void DistanceTo() {
    Vec2d vec1 = new Vec2d(0, 0);
    Vec2d vec2 = new Vec2d(1, 0);
    Vec2d vec3 = new Vec2d(0, 1);
    Vec2d vec4 = new Vec2d(-1, 0);
    Vec2d vec5 = new Vec2d(0, -1);

    assertEquals(Double.NaN, vec1.distanceTo(vec1), "Angular distance between zero vectors is NaN");
    assertEquals(
        Double.NaN, vec1.distanceTo(vec2), "Angular distance involving zero vector is NaN");

    assertEquals(
        Math.PI / 2, vec2.distanceTo(vec3), "Angular distance of a cw perpendicular is pi/2");
    assertEquals(
        Math.PI / 2, vec5.distanceTo(vec4), "Angular distance of a ccw perpendicular is pi/2");
    assertEquals(
        Math.PI / 2,
        vec3.distanceTo(vec4),
        "Angular distance of a cw perpendicular when y<0 is pi/2");

    assertEquals(Math.PI, vec2.distanceTo(vec4));
  }

  @Test
  public void testDotProduct() {
    Vec2d v = new Vec2d(3, 4);
    Vec2d vPerp = new Vec2d(-4, 3);
    Vec2d vZero = new Vec2d(0, 0);
    Vec2d vNeg = new Vec2d(-1, -1);

    // Dot product with the same vector
    double result = v.dot(v);
    assertEquals(25, result, "Dot product of the same vector should be the squared length");

    // Dot product with a perpendicular vector
    result = v.dot(vPerp);
    assertEquals(0, result, "Dot product of perpendicular vectors should be zero");

    // Dot product with a zero vector
    result = v.dot(vZero);
    assertEquals(0, result, "Dot product with zero vector should be zero");

    // Dot product with negative coordinates
    result = v.dot(vNeg);
    assertEquals(-7, result, "Dot product with negative coordinates should match expected value");
  }

  @Test
  public void testCrossProduct() {
    Vec2d v = new Vec2d(3, 4);
    Vec2d vPerp = new Vec2d(-4, 3);
    Vec2d vZero = new Vec2d(0, 0);

    // Cross product with the same vector
    double result = v.cross(v);
    assertEquals(0, result, "Cross product of the same vector should be zero");

    // Cross product with a perpendicular vector
    result = v.cross(vPerp);
    assertEquals(25, result, "Cross product of perpendicular vectors should be the squared length");

    // Cross product with a zero vector
    result = v.cross(vZero);
    assertTrue(MathUtil.epsilonEquals(0, result), "Cross product with zero vector should be zero");
  }
}
