package com.badlogic.debugthugs.headless;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;

import com.badlogic.debugthugs.BusStop;

public class BusStopTest extends AbstractHeadlessTest {

    @Test
    public void testBusStopInitialization() {
        String expectedName = "TestStop";
        BusStop stop = new BusStop(false, expectedName);

        assertNotNull(stop.bounds, "BusStop bounds are null");

        assertEquals(608f, stop.bounds.x, 0.001f, "x value wrong for bus stop");
        assertEquals(512f, stop.bounds.y, 0.001f, "y value wrong for bus stop");
        assertEquals(32f, stop.bounds.width, 0.001f, "wrong width");
        assertEquals(32f, stop.bounds.height, 0.001f, "wrong height");

        assertEquals(expectedName, stop.name, "BusStop name not matching");
        assertFalse(stop.used, "BusStop should not be used initially");
    }

    @Test
    public void testBusStopUsedState() {
        BusStop stop = new BusStop(true, "UsedStop");
        assertTrue(stop.used, "BusStop isnt initialized as used");
    }
}
