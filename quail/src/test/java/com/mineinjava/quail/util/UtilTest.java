package com.mineinjava.quail.util;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

class UtilTest {

  /** Test the getPairs method returns the correct number of pairs */
  @Test
  void getPairs() {
    Integer[] inputArray = {1, 2, 3, 4};
    assertEquals(6, Util.getPairs(inputArray).size());

    inputArray = new Integer[] {1, 2, 3, 4, 5};
    assertEquals(10, Util.getPairs(inputArray).size());
  }
}
