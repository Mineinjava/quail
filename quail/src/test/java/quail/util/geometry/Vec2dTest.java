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

    assertEquals(0, vec1.getAngle());
    assertEquals(Math.PI / 2, vec2.getAngle());
    assertEquals(Math.PI * 3 / 2, vec3.getAngle());
  }
}
