package com.badlogic.debugthugs.headless;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.badlogic.debugthugs.AchievementManager;
import com.badlogic.debugthugs.Bus;
import com.badlogic.debugthugs.BusStop;
import com.badlogic.debugthugs.DuoAuth;
import com.badlogic.debugthugs.EnergyDrink;
import com.badlogic.debugthugs.Key;
import com.badlogic.debugthugs.Player;
import com.badlogic.debugthugs.WetFloor;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer.Cell;

public class DoorTest extends AbstractHeadlessTest {

    /*
     * Tests Door interaction logic:
     * 1. Open door with key
     * 2. Fail to open door without key
     * 3. Detect door collision
     */

    private AchievementManager mockAchievementManager;
    private Input mockInput;
    private Player player;
    private TiledMapTileLayer mockDoorLayer;
    private TiledMapTileLayer mockWallLayer;
    private Cell mockCell;

    // Dummies for playerInput
    private EnergyDrink dummyEnergyDrink;
    private Bus dummyBus;
    private List<BusStop> dummyBusStops;
    private DuoAuth dummyDuoAuth;
    private WetFloor dummyWetFloor;

    @BeforeEach
    public void setUp() throws Exception {
        // Mock AchievementManager
        mockAchievementManager = mock(AchievementManager.class);
        setMockAchievementManager(mockAchievementManager);

        // Mock Input
        mockInput = mock(Input.class);
        Gdx.input = mockInput;

        // Mock Layers
        mockDoorLayer = mock(TiledMapTileLayer.class);
        mockWallLayer = mock(TiledMapTileLayer.class);
        mockCell = mock(Cell.class);
        player = new Player(0, 0, mockWallLayer, mockDoorLayer);
        player.playerX = 32;
        player.playerY = 32;
        player.playerWidth = 32;
        player.playerHeight = 32;

        Player.doorInfront = false;
        Player.open = false;

        // Setup Dummies
        dummyEnergyDrink = new EnergyDrink(0, 0, false);
        dummyBus = new Bus(-100, -100);
        dummyBusStops = new ArrayList<>();
        dummyDuoAuth = new DuoAuth(false, false);
        dummyWetFloor = new WetFloor(false, false);

        when(mockWallLayer.getCell(anyInt(), anyInt())).thenReturn(null);
    }

    @AfterEach
    public void tearDown() {
        try {
            setMockAchievementManager(null);
        } catch (Exception e) {
            throw new RuntimeException("Failed to reset mock AchievementManager", e);
        }
        Gdx.input = null;
        Player.doorInfront = false;
        Player.open = false;
    }

    private void setMockAchievementManager(AchievementManager mock) throws Exception {
        Field instanceField = AchievementManager.class.getDeclaredField("instance");
        instanceField.setAccessible(true);
        instanceField.set(null, mock);
    }

    @Test
    public void testOpenDoor_WithKey_And_DoorInFront() {
        Key key = new Key(true);
        when(mockDoorLayer.getCell(1, 1)).thenReturn(mockCell);

        Player.doorInfront = true;

        when(mockInput.isKeyPressed(Input.Keys.E)).thenReturn(true);

        player.playerInput(key, dummyEnergyDrink, dummyBus, dummyBusStops, dummyDuoAuth, dummyWetFloor, 1f);

        assertTrue(Player.open, "Door should be open");

        verify(mockAchievementManager).unlock("UNLOCKED_DOOR");
    }

    @Test
    public void testCannotOpenDoor_WithoutKey() throws Exception {
        Key key = new Key(false); // Not collected
        Player.doorInfront = true;
        when(mockInput.isKeyPressed(Input.Keys.E)).thenReturn(true);

        player.playerInput(key, dummyEnergyDrink, dummyBus, dummyBusStops, dummyDuoAuth, dummyWetFloor, 1f);

        assertFalse(Player.open, "Door should not open without key");

        Field needsKeyMessageField = Player.class.getDeclaredField("needsKeyMessage");
        needsKeyMessageField.setAccessible(true);
        boolean needsKeyMessage = needsKeyMessageField.getBoolean(player);

        assertTrue(needsKeyMessage, "Should show needs key message");
    }

    @Test
    public void testDoorInFront_Detection() {
        // Test that Collision.door actually sets doorInfront
        // Mock door at (1,1). Player at (32,32). Right side is at 48.
        when(mockDoorLayer.getCell(1, 1)).thenReturn(mockCell);

        // Directly call Collision.door(player)
        boolean isDoor = com.badlogic.debugthugs.Collision.door(player);
        assertTrue(isDoor, "Collision.door should return true");
        assertTrue(Player.doorInfront, "Player.doorInfront should be true");
    }
}
