package com.badlogic.debugthugs.headless;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.badlogic.debugthugs.Bus;
import com.badlogic.debugthugs.BusStop;
import com.badlogic.debugthugs.DuoAuth;
import com.badlogic.debugthugs.EnergyDrink;
import com.badlogic.debugthugs.FirstScreen;
import com.badlogic.debugthugs.Key;
import com.badlogic.debugthugs.LoseScreen;
import com.badlogic.debugthugs.Player;
import com.badlogic.debugthugs.WetFloor;
import com.badlogic.debugthugs.WinScreen;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.math.Rectangle;

public class FirstScreenTest extends AbstractHeadlessTest {
    private Player player;
    private TiledMapTileLayer dummyWallLayer;
    private TiledMapTileLayer dummyDoorLayer;

    private EnergyDrink drink;
    private Key key;
    private BusStop busStop;
    private java.util.List<BusStop> busStops;
    private DuoAuth duoAuth;
    private WetFloor wetFloor;
    private Bus bus;

    @BeforeEach
    void setUp() {
        dummyWallLayer = new TiledMapTileLayer(10, 10, 32, 32);
        dummyDoorLayer = new TiledMapTileLayer(10, 10, 32, 32);

        player = new Player(10f, 20f, dummyWallLayer, dummyDoorLayer);

        drink = new EnergyDrink(200f, 200f, false);
        key = new Key(false);
        busStop = new BusStop(false, "Test");
        busStops = java.util.Collections.singletonList(busStop);
        duoAuth = new DuoAuth(false, false);
        wetFloor = new WetFloor(false, false);
        bus = new Bus(0, 0);
    }

    /**
     * Checks that the timer decreases over time.
     */
    @Test
    void timerDecreases() {
        FakeMain game = new FakeMain();
        FirstScreen screen = new FirstScreen(game);
        screen.initLogic(player, key, drink, bus, busStops, duoAuth, wetFloor, 1000, 1000);
        screen.timePassed = 50f;

        screen.logic(1f);
        assertTrue(screen.timePassed < 50f);
    }

    
    /**
     * Checks that after 5 minutes, the game will change screen to the lose screen.
     */
    @Test
    void loseAfter5Mins() {
        FakeMain game = new FakeMain();
        FirstScreen screen = new FirstScreen(game);
        screen.initLogic(player, key, drink, bus, busStops, duoAuth, wetFloor, 1000, 1000);
        screen.timePassed = 300f;
        
        //Wait for 299 seconds.
        for (int i = 0; i < 299; i++)
        {
            screen.logic(1f);
        }
        // check that lose screen still hasn't been loaded yet.
        assertFalse(screen.timePassed <= 0f);

        // Wait 1 more second
        screen.logic(1f);

        //Check that the lose screen has now been loaded.
        assertTrue(screen.timePassed <= 0f);
        assertTrue(game.lastScreen instanceof LoseScreen);
    }

    /**
     * Checks that when the player overlaps with the exit area, the game will change screen to the 
     * winscreen.
     */
    @Test
    void winOnExitReached()
    {
        FakeMain game = new FakeMain();
        FirstScreen screen = new FirstScreen(game);
        player = new Player(110, 110, dummyWallLayer, dummyDoorLayer);
        screen.initLogic(player, key, drink, bus, busStops, duoAuth, wetFloor, 1000, 1000);
        screen.exitArea = new Rectangle(100, 100, 50, 50);

        screen.timePassed = 50f;
        screen.paused = false;
        assertTrue(screen.isPlayerOverlappingExit());
        screen.logic(0.1f);
        assertTrue(screen.isPlayerOverlappingExit());
        assertTrue(game.lastScreen instanceof WinScreen);
    }

    /**
     * Checks that when the game is paused, time cannot pass.
     */
    @Test
    void pauseHaltsTime()
    {
        FakeMain game = new FakeMain();
        FirstScreen screen = new FirstScreen(game);
        screen.initLogic(player, key, drink, bus, busStops, duoAuth, wetFloor, 1000, 1000);
        screen.timePassed = 300f;
        screen.paused = true;

        screen.logic(1f);
        assertEquals(screen.timePassed, 300f);
    }

    /**
     * Checks that when the game is paused, the player can't move.
     */
    @Test
    void pauseHaltsPlayer()
    {
        FakeMain game = new FakeMain();
        FirstScreen screen = new FirstScreen(game);
        screen.initLogic(player, key, drink, bus, busStops, duoAuth, wetFloor, 1000, 1000);
        screen.timePassed = 300f;
        screen.paused = true;

        FakeInput input = new FakeInput();
        Gdx.input = input;

        assertEquals(player.playerX, 10f, 0.0001f);
        input.press(Input.Keys.RIGHT);
        for (int i = 0; i < 10; i++) {
            screen.logic(0.1f);
        }
        assertEquals(player.playerX, 10f, 0.0001f);
    }

    /**
     * Checks that the score decreases over time.
     */
    @Test
    void scoreDecreasesWithTime()
    {
        FakeMain game = new FakeMain();
        FirstScreen screen = new FirstScreen(game);
        screen.initLogic(player, key, drink, bus, busStops, duoAuth, wetFloor, 1000, 1000);
        screen.timePassed = 300f;

        assertEquals(screen.maxScore, screen.playerScore);
        for (int i = 0; i < 50; i++) {
            screen.logic(0.1f);
        }
        assertTrue(screen.playerScore < screen.maxScore);
    }
}