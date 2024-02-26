package com.mineinjava.quail.pathing;

import org.junit.jupiter.api.Test;


class ConstraintsPairTest {

    /**
     * Test class for ConstraintsPair, cover constructor + getters
     */
    @Test
    void testConstraintsPair() {
        ConstraintsPair constraintsPair = new ConstraintsPair(1, 2);
        assert(constraintsPair.getMaxVelocity() == 1);
        assert(constraintsPair.getMaxAcceleration() == 2);
    }
}