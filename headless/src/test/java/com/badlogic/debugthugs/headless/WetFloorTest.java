package com.badlogic.debugthugs.headless;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.badlogic.debugthugs.AchievementManager;
import com.badlogic.debugthugs.Player;
import com.badlogic.debugthugs.WetFloor;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;


/**
 * This test class tests the logic behind the energy WetFloor negative event to ensure the negative
 * event counter is correctly incremented and that the event is correctly registered as already
 * interacted with or not.
 */
public class WetFloorTest extends AbstractHeadlessTest {

    private Player testPlayer;
    private TiledMapTileLayer testWallLayer;
    private TiledMapTileLayer testDoorLayer;
    private AchievementManager testAchievementManager;

    /**
     * Before each test a fresh headless test player is created and the negative events counter
     * is reset to 0.
     */
    @BeforeEach
    public void setUp() {
        testWallLayer = new TiledMapTileLayer(10, 10, 32, 32);
        testDoorLayer = new TiledMapTileLayer(10, 10, 32, 32);
        testPlayer = new Player(0f, 0f, testWallLayer, testDoorLayer);
        testPlayer.badEvent = 0;
    }

    /**
     * Tests that when the WetFloor has not been encountered that triggered is still false and the
     * negative events counter is not incremented.
     */
    @Test
    public void WetFloorNotEncountered() {
        WetFloor testWetFloor = new WetFloor(10,10,false,false);
        testWetFloor.checkTriggered(testPlayer);

        assertFalse(testWetFloor.triggered,"triggered == false");
        assertFalse(testWetFloor.active,"active == false");
        assertEquals(0, testPlayer.badEvent,"counter == 0");
    }

    /**
     * Tests that when the player crosses bounds with the WetFloor that triggered is set to true
     * and the negative events counter has been incremented.
     */
    @Test
    public void WetFloorIsEncountered() {
        
        WetFloor testWetFloor = new WetFloor(0, 0, false, false);
        testWetFloor.checkTriggered(testPlayer);

        assertTrue(testWetFloor.triggered,"triggered == true");
        assertTrue(testWetFloor.active,"active == true");
        assertEquals(1, testPlayer.badEvent,"counter == 1");
        assertEquals(1.1f, testWetFloor.timer, "timer is set to 1.1f");
        
        testWetFloor.update(1.1f);
        
        assertTrue(testWetFloor.triggered,"triggered == true");
        assertFalse(testWetFloor.active,"active == false");

    }

    /** Tests that if the player crosses bounds with the WetFloor twice that triggered is still set to
     * true and the negative events counter has not been incremented twice.
     */
    @Test
    public void notTriggeredTwice() {
       
        WetFloor testWetFloor = new WetFloor(0, 0, false, false);
        testWetFloor.checkTriggered(testPlayer);
        testWetFloor.checkTriggered(testPlayer);
        testWetFloor.update(1.1f);

        assertEquals(1, testPlayer.badEvent,"counter == 1");
        assertTrue(testWetFloor.triggered,"triggered == true");
        assertFalse(testWetFloor.active,"active == false");

    }

    /**
     * Tests that the corresponding achievement is unlocked when the WetFloor is encountered after
     * the achievement manager is instantiated and reset.
     */
    @Test
    public void achievementUnlocked() {
        testAchievementManager = AchievementManager.get();
        testAchievementManager.resetAll();

        WetFloor testWetFloor = new WetFloor(0, 0, false,false);
        testWetFloor.checkTriggered(testPlayer);

        assertTrue(testAchievementManager.isUnlocked("WATCH_YOUR_STEP"));
    }

}