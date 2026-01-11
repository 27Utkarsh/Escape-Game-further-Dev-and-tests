package com.badlogic.debugthugs.headless;

import com.badlogic.debugthugs.Duck;
import com.badlogic.debugthugs.Player;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class DuckTest extends AbstractHeadlessTest {

    // Test if duck is initialized with correct position and size
    @Test
    void DuckInitialization() {
        Duck duck = new Duck();

        assertEquals(680f, duck.bounds.x, 0.0001f);
        assertEquals(520f, duck.bounds.y, 0.0001f);
        assertEquals(50f, duck.bounds.width, 0.0001f);
        assertEquals(55f, duck.bounds.height, 0.0001f);
    }

    // Test if encountering duck increments bad event counter by one
    // Test if encountering duck resets player's position
    @Test
    void EncounteringDuck() {

        Duck duck = new Duck();
        duck.bounds.setPosition(680f, 520f);

        Player dummyPlayer = new Player(
                680f, 520f,
                null, null);

        dummyPlayer.playerWidth = 50f;
        dummyPlayer.playerHeight = 55f;
        dummyPlayer.badEvent = 0;

        duck.checkCollided(dummyPlayer);

        assertEquals(1, dummyPlayer.badEvent, "badEvent should increment by 1 on collision");
        assertEquals(710f, dummyPlayer.playerX, 0.0001f, "playerX should reset after collision");
        assertEquals(1730f, dummyPlayer.playerY, 0.0001f, "playerY should reset after collision");
    }
}
