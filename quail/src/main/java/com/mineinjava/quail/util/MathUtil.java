package com.mineinjava.quail.util;

public class MathUtil {
    public static boolean epsilonEquals(double value1, double value2) {
        double epsilon = 1e-6; // Adjust the epsilon value as needed
        return Math.abs(value1 - value2) < epsilon;
    }
}
