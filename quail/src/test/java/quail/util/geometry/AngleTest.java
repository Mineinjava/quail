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

import com.mineinjava.quail.util.geometry.Angle;
import org.junit.jupiter.api.Test;

public class AngleTest {
  @Test
  void Norm() {
    assertEquals(Math.PI, Angle.norm(Math.PI), "Angles less than 2pi are unchanged");
    assertEquals(0, Angle.norm(Math.PI * 2), "2pi should wrap down to 0");
    assertEquals(
        Math.PI / 2, Angle.norm(Math.PI * 5 / 2), "Angles greater than 2pi should wrap correctly");
    assertEquals(
        Math.PI * 3 / 2, Angle.norm(Math.PI / -2), "Angles less than 2pi should wrap correctly");
  }

  @Test
  void normDelta() {
    assertEquals(Math.PI, Angle.normDelta(Math.PI), "Math.PI is unchanged");
    assertEquals(Math.PI, Angle.normDelta(-Math.PI), "-pi wraps to pi");
    assertEquals(
        Math.PI / 2,
        Angle.normDelta(Math.PI * 5 / 2),
        "Angles greater than pi should wrap correctly");
    assertEquals(
        Math.PI / -2, Angle.normDelta(Math.PI / -2), "Angles less than pi should wrap correctly");
  }
}
