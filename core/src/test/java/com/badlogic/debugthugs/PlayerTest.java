package com.badlogic.debugthugs;

import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;

/**
 * Unit tests for the {@link Player}.
 * 
 * Validates the basic, non-rendering logic. The tests don't require LibGDX native backends.
 */
public class PlayerTest {
    private Player player;
    private TiledMapTileLayer dummyWallLayer;
    private TiledMapTileLayer dummyDoorLayer;

    /**
     * Creates a dummy Player instance using empty tile layers and a single-frame animation before each Test.
     */
    @BeforeEach
    void setUp() {
        dummyWallLayer = new TiledMapTileLayer(10, 10, 32, 32);
        dummyDoorLayer = new TiledMapTileLayer(10, 10, 32, 32);

        player = new Player(10f, 20f, dummyWallLayer, dummyDoorLayer);
    }

    /**
     * Checks that player is initialised with the correct position and size.
     */
    @Test
    void testInitialPositionAndSize() {
        assertEquals(10f, player.playerX, 0.0001f);
        assertEquals(20f, player.playerY, 0.0001f);
        assertEquals(12f, player.playerWidth, 0.0001f);
        assertEquals(14f, player.playerHeight, 0.0001f);
    }

    @Test
    void testInitialEventCounters() {
        assertEquals(0, player.goodEvent);
        assertEquals(0, player.badEvent);
        assertEquals(0, player.hiddenEvent);
    }

    /**
     * Checks the player's speed increases when picking up an energy drink.
     */
    @Test
    void testEnergyDrinkSpeed() {
        EnergyDrink drink = new EnergyDrink(true);
        Key key = new Key(false);
        Portal portal = new Portal(false);
        DuoAuth duoAuth = new DuoAuth(false, false);
        WetFloor wetFloor = new WetFloor(false, false);

        assertEquals(player.speed, 128f, 0.0001f);
        player.playerInput(key, drink, portal, duoAuth, wetFloor);
        assertEquals(player.speed, 160f, 0.0001f);
    }
}
