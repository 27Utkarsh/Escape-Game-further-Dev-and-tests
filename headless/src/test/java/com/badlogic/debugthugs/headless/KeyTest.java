package com.badlogic.debugthugs.headless;

import java.lang.reflect.Field;

import org.junit.jupiter.api.AfterEach;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import com.badlogic.debugthugs.AchievementManager;
import com.badlogic.debugthugs.Key;
import com.badlogic.debugthugs.Player;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.math.Rectangle;

public class KeyTest extends AbstractHeadlessTest {

    private AchievementManager mockAchievementManager;
    private Player dummyPlayer;

    @BeforeEach
    public void setUp() throws Exception {
        // Mock the AchievementManager singleton
        mockAchievementManager = mock(AchievementManager.class);
        setMockAchievementManager(mockAchievementManager);

        // Setup dummy player
        TiledMapTileLayer dummyLayer = new TiledMapTileLayer(10, 10, 32, 32);
        dummyPlayer = new Player(0, 0, dummyLayer, dummyLayer);
        dummyPlayer.playerX = 0;
        dummyPlayer.playerY = 0;
        dummyPlayer.playerWidth = 32;
        dummyPlayer.playerHeight = 32;
        dummyPlayer.hiddenEvent = 0;
    }

    @AfterEach
    public void tearDown() {
        try {
            setMockAchievementManager(null);
        } catch (Exception e) {
            throw new RuntimeException("Failed to reset mock AchievementManager", e);
        }
    }

    private void setMockAchievementManager(AchievementManager mock) throws Exception {
        Field instanceField = AchievementManager.class.getDeclaredField("instance");
        instanceField.setAccessible(true);
        instanceField.set(null, mock);
    }

    @Test
    public void testKeyInitialization() {
        Key key = new Key(false);
        assertFalse(key.collected, "Key should not be collected initially");
    }

    @Test
    public void testCheckCollected_Collision() {
        Key key = new Key(false);
        // Manually set bounds and testMode since we are using the test constructor
        key.bounds = new Rectangle(0, 0, 32, 32);
        try {
            Field testModeField = Key.class.getDeclaredField("testMode");
            testModeField.setAccessible(true);
            testModeField.setBoolean(key, false);
        } catch (Exception e) {
            throw new RuntimeException("Failed to set testMode on Key", e);
        }

        // Player is at 0,0 (overlapping)
        key.checkCollected(dummyPlayer);

        assertTrue(key.collected, "key collected before collision");
        assertEquals(1, dummyPlayer.hiddenEvent, "Player hiddenEvent not incremented");
        verify(mockAchievementManager).unlock("FOUND_KEY");
    }

    @Test
    public void testCheckCollected_NoCollision() {
        Key key = new Key(false);
        key.bounds = new Rectangle(100, 100, 32, 32);
        try {
            Field testModeField = Key.class.getDeclaredField("testMode");
            testModeField.setAccessible(true);
            testModeField.setBoolean(key, false);
        } catch (Exception e) {
            throw new RuntimeException("Failed to set testMode on Key", e);
        }

        // Player is at 0,0 (not overlapping)
        key.checkCollected(dummyPlayer);

        assertFalse(key.collected, "Key collected without collision");
        assertEquals(0, dummyPlayer.hiddenEvent, "Player hiddenEvent should not change");
    }

    @Test
    public void testCheckCollected_AlreadyCollected() {
        Key key = new Key(true); // Already collected
        key.bounds = new Rectangle(0, 0, 32, 32);
        try {
            Field testModeField = Key.class.getDeclaredField("testMode");
            testModeField.setAccessible(true);
            testModeField.setBoolean(key, false);
        } catch (Exception e) {
            throw new RuntimeException("Failed to set testMode on Key", e);
        }

        int initialEvents = dummyPlayer.hiddenEvent;

        // Overlapping but already collected
        key.checkCollected(dummyPlayer);

        assertTrue(key.collected);
        assertEquals(initialEvents, dummyPlayer.hiddenEvent, "increment hiddenEvent when already collected");
    }
}
