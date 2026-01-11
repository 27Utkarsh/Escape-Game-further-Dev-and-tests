package com.badlogic.debugthugs.headless;

import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.Test;

import com.badlogic.debugthugs.Exam;
import com.badlogic.debugthugs.Player;

public class ExamTest extends AbstractHeadlessTest {

    /**
     * Test if exam is initialized with correct position and size.
     */
    @Test
    void examInitialization() {
        Exam exam = new Exam(1184f, 1000f, 200f);
        assertEquals(1184f, exam.bounds.x, 0.0001f);
        assertEquals(1000f, exam.bounds.y, 0.0001f);
        assertEquals(50f, exam.bounds.width, 0.0001f);
        assertEquals(50f, exam.bounds.height, 0.0001f);
    }

    /**
     * Test if encountering Exam increments bad event by one.
     */
    @Test
    void increaseCounter() {
        Exam exam = new Exam(1184f, 1500f, 100f);
        Player player = new Player(1184f, 1500f, null, null);
        player.playerWidth = 50f;
        player.playerHeight = 50f;
        player.badEvent = 0;

        exam.checkCollided(player);

        // Test if badEvent counter gets incremented
        assertEquals(1, player.badEvent,
                "badEvent should increment by 1 when player collides with exam");

        // Ensure exam only triggers once
        exam.checkCollided(player);
        assertEquals(1, player.badEvent,
                "badEvent should not increment again once exam is completed");
    }

    // Check if Exam object patrols correctly
    @Test
    void ExamPatrols() {
        Exam exam = new Exam(1184f, 1000f, 100f);

        // Speed is 60f, direction is 1 (up), delta is 2f
        // Movement: 60 * 1 * 2 = 120 pixels up
        exam.update(2f);
        assertEquals(1100f, exam.bounds.y, 0.0001f);

        // Now direction reverses (hit upper limit at 1100)
        // Speed is 60f, direction is -1 (down), delta is 1f
        // Movement: 60 * -1 * 1 = -60 pixels
        exam.update(1f);
        assertEquals(1040f, exam.bounds.y, 0.0001f);

        // Still moving down, delta is 2f
        // Movement: 60 * -1 * 2 = -120 pixels, but hits lower bound at 1000
        exam.update(2f);
        assertEquals(1000f, exam.bounds.y, 0.0001f);
    }
}
