package com.mineinjava.quail.util;

import java.util.ArrayList;
import java.util.List;

public class Util {

  /** returns a list of all the pairs that can be formed from a list */
  public static <T> List<List<T>> getPairs(T[] inputArray) {
    List<List<T>> pairs = new ArrayList<>();
    for (int i = 0; i < inputArray.length; i++) {
      for (int j = i + 1; j < inputArray.length; j++) {
        List<T> pair = new ArrayList<>();
        pair.add(inputArray[i]);
        pair.add(inputArray[j]);
        pairs.add(pair);
      }
    }
    return pairs;
  }
}
