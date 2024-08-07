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

package quail;

import static org.junit.jupiter.api.Assertions.assertTrue;

import com.mineinjava.quail.util.MathUtil;

/** Utilities to make writing tests easier */
public class TestUtil {
  public static void assertEpsilonEquals(Double expected, Double actual, String result) {
    assertTrue(MathUtil.epsilonEquals(expected, actual), result);
  }

  public static void assertEpsilonEquals(Double expected, Double actual) {
    assertTrue(MathUtil.epsilonEquals(expected, actual));
  }
}
