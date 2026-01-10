package com.badlogic.debugthugs.headless;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import org.junit.jupiter.api.Test;

import com.badlogic.debugthugs.Bus;

public class BusTest extends AbstractHeadlessTest {

    @Test
    public void testBusInitialization() {
        float expectedX = 100f;
        float expectedY = 200f;

        Bus bus = new Bus(expectedX, expectedY);

        assertNotNull(bus.bounds, "Bus bounds are null");
        assertEquals(expectedX, bus.bounds.x, 0.001f, "wrong x for bus");
        assertEquals(expectedY, bus.bounds.y, 0.001f, "wrong y for bus");
        assertEquals(32f, bus.bounds.width, 0.001f, "wrong bus width");
        assertEquals(32f, bus.bounds.height, 0.001f, "wrong bus height");
    }
}
