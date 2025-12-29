package com.badlogic.debugthugs.headless;

import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.badlogic.debugthugs.DuoAuth;
import com.badlogic.debugthugs.EnergyDrink;
import com.badlogic.debugthugs.Key;
import com.badlogic.debugthugs.Player;
import com.badlogic.debugthugs.Portal;
import com.badlogic.debugthugs.WetFloor;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;

public class HeadlessPlayerTests extends AbstractHeadlessTest {
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
        player.playerInput(key, drink, portal, duoAuth, wetFloor, 0.1f);
        assertEquals(player.speed, 160f, 0.0001f);
    }
}
