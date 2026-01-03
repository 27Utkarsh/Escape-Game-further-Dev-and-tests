package com.badlogic.debugthugs.headless;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.badlogic.debugthugs.DuoAuth;
import com.badlogic.debugthugs.EnergyDrink;
import com.badlogic.debugthugs.Key;
import com.badlogic.debugthugs.Player;
import com.badlogic.debugthugs.Player.State;
import com.badlogic.debugthugs.Bus;
import com.badlogic.debugthugs.BusStop;
import com.badlogic.debugthugs.WetFloor;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;

public class HeadlessPlayerTests extends AbstractHeadlessTest {
    private Player player;
    private TiledMapTileLayer dummyWallLayer;
    private TiledMapTileLayer dummyDoorLayer;

    /**
     * Creates a dummy Player instance using empty tile layers and a single-frame
     * animation before each Test.
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
        EnergyDrink drink = new EnergyDrink(100f, 100f, true);
        Key key = new Key(false);
        BusStop busStop = new BusStop(false, "Test");
        java.util.List<BusStop> busStops = java.util.Collections.singletonList(busStop);
        DuoAuth duoAuth = new DuoAuth(false, false);
        WetFloor wetFloor = new WetFloor(false, false);
        Bus bus = new Bus(0, 0);

        assertEquals(player.speed, 128f, 0.0001f);
        player.playerInput(key, drink, bus, busStops, duoAuth, wetFloor, 0.1f);
        assertEquals(player.speed, 160f, 0.0001f);
    }

    /**
     * Checks that the player can move right.
     */
    @Test
    void testMoveRight() {
        FakeInput input = new FakeInput();
        Gdx.input = input;
        EnergyDrink drink = new EnergyDrink(100f, 100f, false);
        Key key = new Key(false);
        BusStop busStop = new BusStop(false, "Test");
        java.util.List<BusStop> busStops = java.util.Collections.singletonList(busStop);
        DuoAuth duoAuth = new DuoAuth(false, false);
        WetFloor wetFloor = new WetFloor(false, false);
        Bus bus = new Bus(0, 0);

        assertEquals(player.playerX, 10f, 0.0001f);
        input.press(Input.Keys.RIGHT);
        for (int i = 0; i < 10; i++) {
            player.playerInput(key, drink, bus, busStops, duoAuth, wetFloor, 0.1f);
        }
        assertSame(player.playerAnimation, State.WALK_R);
        assertTrue(player.playerX > 10f);
    }

    /**
     * Checks that the player can move left.
     */
    @Test
    void testMoveLeft() {
        FakeInput input = new FakeInput();
        Gdx.input = input;
        EnergyDrink drink = new EnergyDrink(100f, 100f, false);
        Key key = new Key(false);
        BusStop busStop = new BusStop(false, "Test");
        java.util.List<BusStop> busStops = java.util.Collections.singletonList(busStop);
        DuoAuth duoAuth = new DuoAuth(false, false);
        WetFloor wetFloor = new WetFloor(false, false);
        Bus bus = new Bus(0, 0);

        assertEquals(player.playerX, 10f, 0.0001f);
        input.press(Input.Keys.LEFT);
        for (int i = 0; i < 10; i++) {
            player.playerInput(key, drink, bus, busStops, duoAuth, wetFloor, 0.1f);
        }
        assertSame(player.playerAnimation, State.WALK_L);
        assertTrue(player.playerX < 10f);
    }

    /**
     * Checks that the player can move up.
     */
    @Test
    void testMoveUp() {
        FakeInput input = new FakeInput();
        Gdx.input = input;
        EnergyDrink drink = new EnergyDrink(100f, 100f, false);
        Key key = new Key(false);
        BusStop busStop = new BusStop(false, "Test");
        java.util.List<BusStop> busStops = java.util.Collections.singletonList(busStop);
        DuoAuth duoAuth = new DuoAuth(false, false);
        WetFloor wetFloor = new WetFloor(false, false);
        Bus bus = new Bus(0, 0);

        assertEquals(player.playerY, 20f, 0.0001f);
        input.press(Input.Keys.UP);
        for (int i = 0; i < 10; i++) {
            player.playerInput(key, drink, bus, busStops, duoAuth, wetFloor, 0.1f);
        }
        assertSame(player.playerAnimation, State.WALK_UP);
        assertTrue(player.playerY > 20f);
    }

    /**
     * Checks that the player can move down.
     */
    @Test
    void testMoveDown() {
        FakeInput input = new FakeInput();
        Gdx.input = input;
        EnergyDrink drink = new EnergyDrink(100f, 100f, false);
        Key key = new Key(false);
        BusStop busStop = new BusStop(false, "Test");
        java.util.List<BusStop> busStops = java.util.Collections.singletonList(busStop);
        DuoAuth duoAuth = new DuoAuth(false, false);
        WetFloor wetFloor = new WetFloor(false, false);
        Bus bus = new Bus(0, 0);

        assertEquals(player.playerY, 20f, 0.0001f);
        input.press(Input.Keys.DOWN);
        for (int i = 0; i < 10; i++) {
            player.playerInput(key, drink, bus, busStops, duoAuth, wetFloor, 0.1f);
        }
        assertSame(player.playerAnimation, State.WALK);
        assertTrue(player.playerY < 20f);
    }

    /**
     * Checks that when DuoAuth is active, the player can't move.
     */
    @Test
    void testInputDuoAuth() {
        FakeInput input = new FakeInput();
        Gdx.input = input;

        EnergyDrink drink = new EnergyDrink(100f, 100f, false);
        Key key = new Key(false);
        BusStop busStop = new BusStop(false, "Test");
        java.util.List<BusStop> busStops = java.util.Collections.singletonList(busStop);
        DuoAuth duoAuth = new DuoAuth(true, true);
        WetFloor wetFloor = new WetFloor(false, false);
        Bus bus = new Bus(0, 0);

        assertEquals(player.playerX, 10f, 0.0001f);
        input.press(Input.Keys.RIGHT);
        for (int i = 0; i < 10; i++) {
            player.playerInput(key, drink, bus, busStops, duoAuth, wetFloor, 0.1f);
        }
        assertEquals(player.playerX, 10f, 0.0001f);
    }

    /**
     * Checks that when WetFloor is active, the player can't move.
     */
    @Test
    void testInputWetFloor() {
        FakeInput input = new FakeInput();
        Gdx.input = input;

        EnergyDrink drink = new EnergyDrink(100f, 100f, false);
        Key key = new Key(false);
        BusStop busStop = new BusStop(false, "Test");
        java.util.List<BusStop> busStops = java.util.Collections.singletonList(busStop);
        DuoAuth duoAuth = new DuoAuth(false, false);
        WetFloor wetFloor = new WetFloor(true, true);
        Bus bus = new Bus(0, 0);

        assertEquals(player.playerX, 10f, 0.0001f);
        input.press(Input.Keys.RIGHT);
        for (int i = 0; i < 10; i++) {
            player.playerInput(key, drink, bus, busStops, duoAuth, wetFloor, 0.1f);
        }
        assertEquals(player.playerX, 10f, 0.0001f);
    }
}
