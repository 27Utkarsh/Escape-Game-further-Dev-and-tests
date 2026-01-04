package com.badlogic.debugthugs.headless;

import static org.junit.jupiter.api.Assertions.*;

import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import com.badlogic.debugthugs.EnergyDrink;
import com.badlogic.debugthugs.Player;
import com.badlogic.debugthugs.AchievementManager;

/**
 * This test class tests the logic behind the energy drink positive event to ensure the positive
 * event counter is correctly incremented and that the event is correctly registered as already
 * interacted with or not.
 */
public class EnergyDrinkTests extends AbstractHeadlessTest {

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
        testPlayer.goodEvent = 0;
    }

    /**
     * Tests that when the drink has not been obtained that drank is still false and the
     * positive events counter is not incremented.
     */
    @Test
    public void drinkNotObtained() {
        EnergyDrink testDrink = new EnergyDrink(50f, 50f, false);
        testDrink.checkDrank(testPlayer);

        assertFalse(testDrink.drank);
        assertEquals(0, testPlayer.goodEvent);
    }

    /**
     * Tests that when the player crosses bounds with the drink that drank is set to true
     * and the positive events counter has been incremented.
     */
    @Test
    public void drinkIsObtained() {
        EnergyDrink testDrink = new EnergyDrink(0f, 0f, false);
        testDrink.checkDrank(testPlayer);

        assertTrue(testDrink.drank);
        assertEquals(1, testPlayer.goodEvent);
    }

    /** Tests that if the player crosses bounds with the drink twice that drank is still set to
     * true and the positive events counter has not been incremented twice.
     */
    @Test
    public void notObtainedTwice() {
        EnergyDrink testDrink = new EnergyDrink(0f, 0f, false);
        testDrink.checkDrank(testPlayer);

        //Check drank called a second time to simulate player interaction
        testDrink.checkDrank(testPlayer);

        assertTrue(testDrink.drank);
        assertEquals(1, testPlayer.goodEvent);
    }

    /**
     * Tests that if drank is set to true before the player interacts with the drink that drank
     * remains true and the positive events counter is not incremented.
     */
    @Test
    public void drinkPreDrunk() {
        EnergyDrink testDrink = new EnergyDrink(0f, 0f, true);
        testDrink.checkDrank(testPlayer);

        assertTrue(testDrink.drank);
        assertEquals(0, testPlayer.goodEvent);
    }

    /**
     * Tests that the corresponding achievement is unlocked when the drink is obtained after
     * the achievement manager is instantiated and reset.
     */
    @Test
    public void achievementUnlocked() {
        testAchievementManager = AchievementManager.get();
        testAchievementManager.resetAll();

        EnergyDrink testDrink = new EnergyDrink(0f, 0f, false);
        testDrink.checkDrank(testPlayer);

        assertTrue(testAchievementManager.isUnlocked("ENERGISED"));
    }

}
