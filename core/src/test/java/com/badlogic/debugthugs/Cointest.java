package com.badlogic.debugthugs;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class Cointest {
    private Coin coin;

    /**
     * Set up the coin instance before each test.
     */
    @BeforeEach
    void setUp() {
        // Use the headless constructor
        coin = new Coin(10f, 20f, 5f);
    }

    @Test
    void testInitialPositionAndValue() {
        assertEquals(10f, coin.bounds.x, 0.0001f);
        assertEquals(20f, coin.bounds.y, 0.0001f);
        assertEquals(5f, coin.bonusPoints, 0.0001f);
        assertFalse(coin.collected, "Coin should not be collected initially");
    }

    @Test
    void testCollect() {
        coin.collect();
        assertTrue(coin.collected, "Coin should be marked as collected after collect");
    }

    @Test
    void testMultipleCollects() {
        coin.collect();
        coin.collect();
        assertTrue(coin.collected, "Coin should remain collected after multiple collect calls");
    }
}
