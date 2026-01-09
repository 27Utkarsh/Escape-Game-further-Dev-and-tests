package com.badlogic.debugthugs;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.backends.headless.HeadlessApplication;
import com.badlogic.gdx.backends.headless.HeadlessApplicationConfiguration;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;

class DuckTest {

    @BeforeAll
    public static void init() {
        if (Gdx.files == null) {
            HeadlessApplicationConfiguration config = new HeadlessApplicationConfiguration();
            new HeadlessApplication(new com.badlogic.gdx.ApplicationAdapter() {}, config);
        }
    }

    //Test if duck is initialized with correct position and size
    @Test
    void duckInitialization() {
        Duck duck = new Duck();
        assertEquals(680f, duck.bounds.x, 0.0001f);
        assertEquals(520f, duck.bounds.y, 0.0001f);
        assertEquals(50f, duck.bounds.width, 0.0001f);
        assertEquals(55f, duck.bounds.height, 0.0001f);
    }

    //Test if encountering duck increments bad event counter by one
    //Test if encountering duck resets player's position
    @Test
    void encounteringDuck() {
        Duck duck = new Duck();
        duck.bounds.setPosition(680f, 520f);
        
        Player dummyPlayer = new Player(680f, 520f, null, null);
        dummyPlayer.playerWidth = 50f;
        dummyPlayer.playerHeight = 55f;
        dummyPlayer.badEvent = 0;

        duck.checkCollided(dummyPlayer);

        assertEquals(1, dummyPlayer.badEvent, "badEvent should increment by 1 on collision");
        assertEquals(710f, dummyPlayer.playerX, 0.0001f, "playerX should reset after collision");
        assertEquals(1730f, dummyPlayer.playerY, 0.0001f, "playerY should reset after collision");
    }
}
