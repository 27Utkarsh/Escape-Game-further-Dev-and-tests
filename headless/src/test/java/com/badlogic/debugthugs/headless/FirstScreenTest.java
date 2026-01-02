package com.badlogic.debugthugs.headless;

import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;

import com.badlogic.debugthugs.FirstScreen;

public class FirstScreenTest extends AbstractHeadlessTest {

    /**
     * Checks that the timer decreases over time.
     */
    @Test
    void timerDecreases() {
        FakeMain game = new FakeMain();
        FirstScreen screen = new FirstScreen(game);

        screen.timePassed = 50f;
        screen.paused = false;

        screen.logic(1f);
        assertTrue(screen.timePassed < 50f);
    }
}