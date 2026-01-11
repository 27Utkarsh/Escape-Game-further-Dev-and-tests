package com.badlogic.debugthugs;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class HelperCharacterTest {
    private HelperCharacter helper;

    @BeforeEach
    void setUp() {
        helper = new HelperCharacter(50f, 100f);
    }

    @Test
    void testInitialState() {
        assertFalse(helper.collected);
    }

    @Test
    void testBoundsPosition() {
        assertEquals(50f, helper.bounds.x, 0.0001f);
        assertEquals(100f, helper.bounds.y, 0.0001f);
        assertEquals(32f, helper.bounds.width, 0.0001f);
        assertEquals(32f, helper.bounds.height, 0.0001f);
    }

    @Test
    void testCollect() {
        helper.collect();
        assertTrue(helper.isCollected());
    }
}
