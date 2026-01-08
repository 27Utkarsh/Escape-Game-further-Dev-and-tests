package com.badlogic.debugthugs.headless;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.badlogic.debugthugs.AchievementManager;
import com.badlogic.debugthugs.LongBoi;
import com.badlogic.debugthugs.Player;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;

/**
 * This test class tests the logic behind the energy drink positive event to ensure the positive
 * event counter is correctly incremented and that the event is correctly registered as already
 * interacted with or not.
 */
public class LongBoiTest extends AbstractHeadlessTest {

    private Player testPlayer;
    private TiledMapTileLayer testWallLayer;
    private TiledMapTileLayer testDoorLayer;
    private AchievementManager testAchievementManager;

    /**
     * Before each test a fresh headless test player is created and the positive events counter
     * is reset to 0.
     */
    @BeforeEach
    public void setUp() {
        testWallLayer = new TiledMapTileLayer(10, 10, 32, 32);
        testDoorLayer = new TiledMapTileLayer(10, 10, 32, 32);
        testPlayer = new Player(0f, 0f, testWallLayer, testDoorLayer);
        testPlayer.hiddenEvent = 0;
    }

    /**
     * Tests that when longBoi has not been encountered that triggered is still false and the
     * hidden events counter is not incremented.
     */
    @Test
    public void LongBoiNotEncountered() {
        LongBoi testLongBoi = new LongBoi(10,10,false,true);
        testLongBoi.checkTriggered(testPlayer);

        assertFalse(testLongBoi.triggered,"triggered == false");
        assertTrue(testLongBoi.active,"active == true");
        assertEquals(0, testPlayer.hiddenEvent,"couner == 0");
    }

    /**
     * Tests that when the player crosses bounds with LongBoi that triggered is set to true
     * and the hidden events counter has been incremented.
     */
    @Test
    public void LongBoiIsEncountered() {
        LongBoi testLongBoi = new LongBoi(0, 0, false, true);
        testLongBoi.checkTriggered(testPlayer);

        assertTrue(testLongBoi.triggered,"triggered == true");
        assertTrue(testLongBoi.active,"active == true");
        assertEquals(1, testPlayer.hiddenEvent,"counter == 1");
        assertEquals(0f, testLongBoi.eventTime,"eventTime == 0f");

        testLongBoi.update(3f);

        assertTrue(testLongBoi.triggered,"triggered == true");
        assertFalse(testLongBoi.active,"active == false");




    }

    /** Tests that if the player crosses bounds with LongBoi twice that triggered is still set to
     * true and the positive events counter has not been incremented twice.
     */
    @Test
    public void notTriggeredTwice() {
        LongBoi testLongBoi = new LongBoi(0, 0, false, true);
        
        testLongBoi.checkTriggered(testPlayer);
        testLongBoi.checkTriggered(testPlayer);
        testLongBoi.update(3f);

        assertEquals(1, testPlayer.hiddenEvent,"counter == 1");
        assertTrue(testLongBoi.triggered,"triggered == true");
        assertFalse(testLongBoi.active,"active == false");
    }



    /**
     * Tests that the corresponding achievement is unlocked when LongBoi is encountered after
     * the achievement manager is instantiated and reset.
     */
    @Test
    public void achievementUnlocked() {
        testAchievementManager = AchievementManager.get();
        testAchievementManager.resetAll();

        LongBoi testLongBoi = new LongBoi(0, 0, false,true);
        testLongBoi.checkTriggered(testPlayer);

        assertTrue(testAchievementManager.isUnlocked("QUACK"));
    }

}