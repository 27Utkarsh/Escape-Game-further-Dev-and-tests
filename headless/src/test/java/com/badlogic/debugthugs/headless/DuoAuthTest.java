package com.badlogic.debugthugs.headless;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import java.lang.reflect.Field;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.badlogic.debugthugs.AchievementManager;
import com.badlogic.debugthugs.DuoAuth;
import com.badlogic.debugthugs.Player;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;

public class DuoAuthTest extends AbstractHeadlessTest {

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
    }

    @AfterEach
    public void tearDown() {
        // Reset the singleton to null to avoid polluting other tests
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
    public void testDuoAuthInitialization() {
        DuoAuth duoAuth = new DuoAuth(false, false);
        assertFalse(duoAuth.triggered, "Should not be triggered initially");
        assertFalse(duoAuth.active, "Should not be active initially");
        assertEquals(0f, duoAuth.timer, 0.001f, "Timer should start at 0");
    }

    @Test
    public void testCheckTriggered_Collision() {

        DuoAuth duoAuth = new DuoAuth(false, false);

        try {
            Field testModeField = DuoAuth.class.getDeclaredField("testMode");
            testModeField.setAccessible(true);
            testModeField.setBoolean(duoAuth, false);
            duoAuth.bounds = new com.badlogic.gdx.math.Rectangle(100f, 100f, 32f, 32f);

        } catch (Exception e) {
            throw new RuntimeException("Failed to setup DuoAuth for collision test", e);
        }

        // Place player overlapping
        dummyPlayer.playerX = 100f;
        dummyPlayer.playerY = 100f;
        dummyPlayer.playerWidth = 32f;
        dummyPlayer.playerHeight = 32f;

        // Act
        duoAuth.checkTriggered(dummyPlayer);

        // Assert
        assertTrue(duoAuth.triggered, "Should be triggered after collision");
        assertTrue(duoAuth.active, "Should be active after collision");
        assertEquals(10f, duoAuth.timer, 0.001f, "Timer should be set to 10s");
        assertEquals(1, dummyPlayer.badEvent, "Player badEvent should be incremented");
    }

    @Test
    public void testCheckTriggered_NoCollision() {
        DuoAuth duoAuth = new DuoAuth(false, false);

        try {
            Field testModeField = DuoAuth.class.getDeclaredField("testMode");
            testModeField.setAccessible(true);
            testModeField.setBoolean(duoAuth, false);
            duoAuth.bounds = new com.badlogic.gdx.math.Rectangle(200f, 200f, 32f, 32f);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        // Place player far away
        dummyPlayer.playerX = 0f;
        dummyPlayer.playerY = 0f;

        // Act
        duoAuth.checkTriggered(dummyPlayer);

        // Assert
        assertFalse(duoAuth.triggered, "Should not be triggered without collision");
    }

    @Test
    public void testUpdate_TimerDecreases() {
        DuoAuth duoAuth = new DuoAuth(true, true);
        duoAuth.timer = 5f;

        duoAuth.update(1f);

        assertTrue(duoAuth.active, "Should still be active");
        assertEquals(4f, duoAuth.timer, 0.001f, "Timer should decrease by delta");
    }

    @Test
    public void testUpdate_TimerExpires() {
        DuoAuth duoAuth = new DuoAuth(true, true);
        duoAuth.timer = 0.5f;

        duoAuth.update(0.6f);

        assertFalse(duoAuth.active, "Should deactivate when timer expires");
        verify(mockAchievementManager, times(1)).unlock("DUO_AUTHENTICATED");
    }
}
