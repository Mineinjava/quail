// Tests for the Angle class

package quail.util.geometry;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

import com.mineinjava.quail.util.geometry.Angle;

class AngleTest {

  @Test
  void angleConstructorsAndGetters() {
    Angle a = Angle.fromRadians(0);
    assertEquals(0, a.getRadians());
    assertEquals(0, a.getDegrees());

    a = Angle.fromRadians(Math.PI);
    assertEquals(Math.PI, a.getRadians());
    assertEquals(180, a.getDegrees());

    a = Angle.fromDegrees(0);
    assertEquals(0, a.getRadians());
    assertEquals(0, a.getDegrees());

    a = Angle.fromDegrees(180);
    assertEquals(Math.PI, a.getRadians());
    assertEquals(180, a.getDegrees());
  }

  @Test 
  void angleAddition() {
    Angle a = Angle.fromRadians(0);
    Angle b = Angle.fromRadians(Math.PI);
    Angle c = a.add(b);
    assertEquals(Math.PI, c.getRadians());

    a = Angle.fromDegrees(0);
    b = Angle.fromDegrees(180);
    c = a.add(b);
    assertEquals(Math.PI, c.getRadians());

    a = Angle.fromRadians(Math.PI);
    a = Angle.fromDegrees(-180);
    c = a.add(b);
    assertEquals(0, c.getRadians());
  }

  @Test
  void angleSubtraction() {
    Angle a = Angle.fromRadians(0);
    Angle b = Angle.fromRadians(Math.PI);
    Angle c = a.subtract(b);
    assertEquals(-Math.PI, c.getRadians());

    a = Angle.fromDegrees(0);
    b = Angle.fromDegrees(180);
    c = a.subtract(b);
    assertEquals(-Math.PI, c.getRadians());
  }

  @Test
  void angleNormalization() {
    Angle a = Angle.fromDegrees(800);
    assertEquals(Angle.norm(a.getRadians()), a.norm().getRadians());

    a = Angle.fromDegrees(-800);
    assertEquals(Angle.norm(a.getRadians()), a.norm().getRadians());
  }


  @Test
  void angleNormalizationDelta() {
    Angle a = Angle.fromDegrees(800);
    assertEquals(Angle.normDelta(a.getRadians()), a.normDelta().getRadians(), 0.0001);

    a = Angle.fromDegrees(-800);
    assertEquals(Angle.normDelta(a.getRadians()), a.normDelta().getRadians(), 0.0001);
  }

  @Test
  void angleEquality() {
    Angle a = Angle.fromDegrees(0);
    Angle b = Angle.fromDegrees(0);
    assertEquals(a, b);

    a = Angle.fromDegrees(0);
    b = Angle.fromDegrees(0);
    assertNotEquals(a, b);
  }
}
