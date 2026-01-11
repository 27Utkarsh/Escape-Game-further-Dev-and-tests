package com.badlogic.debugthugs;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * Tests the collision system.
 * Checks the collisions work correctly for all directions in edge cases.
 */
public class CollisionTest {
    private final float playerWidth = 16;
    private final float playerHeight = 16;

    @BeforeEach
    void setUp() {
        // Ensure static variables in Player are reset to their initial state.
        Player.doorInfront = false;
        Player.open = false;
    }

    @Test
    void noWallMeansNoCollision() {
        TiledMapTileLayer wallLayer = new TiledMapTileLayer(5, 5, 32, 32);
        TiledMapTileLayer doorLayer = new TiledMapTileLayer(5, 5, 32, 32);
        Player player = new Player(32, 32, wallLayer, doorLayer);

        assertFalse(Collision.collisionCheck(player));
    }

    @Test
    void wallUnderPlayerCollision() {
        TiledMapTileLayer wallLayer = new TiledMapTileLayer(5, 5, 32, 32);
        TiledMapTileLayer doorLayer = new TiledMapTileLayer(5, 5, 32, 32);
        wallLayer.setCell(1, 1, new TiledMapTileLayer.Cell());
        Player player = new Player(32, 63, wallLayer, doorLayer);

        assertTrue(Collision.collisionCheck(player));
    }

    @Test
    void wallUnderPlayerNoCollision() {
        TiledMapTileLayer wallLayer = new TiledMapTileLayer(5, 5, 32, 32);
        TiledMapTileLayer doorLayer = new TiledMapTileLayer(5, 5, 32, 32);
        wallLayer.setCell(1, 1, new TiledMapTileLayer.Cell());
        Player player = new Player(32, 64, wallLayer, doorLayer);

        assertFalse(Collision.collisionCheck(player));
    }

    @Test
    void wallRightPlayerCollision() {
        TiledMapTileLayer wallLayer = new TiledMapTileLayer(5, 5, 32, 32);
        TiledMapTileLayer doorLayer = new TiledMapTileLayer(5, 5, 32, 32);
        wallLayer.setCell(2, 1, new TiledMapTileLayer.Cell());
        Player player = new Player(64 - playerWidth, 32, wallLayer, doorLayer);

        assertTrue(Collision.collisionCheck(player));
    }

    @Test
    void wallAbovePlayerNoCollision() {
        TiledMapTileLayer wallLayer = new TiledMapTileLayer(5, 5, 32, 32);
        TiledMapTileLayer doorLayer = new TiledMapTileLayer(5, 5, 32, 32);
        wallLayer.setCell(1, 2, new TiledMapTileLayer.Cell());
        Player player = new Player(32, 63 - playerHeight, wallLayer, doorLayer);

        assertFalse(Collision.collisionCheck(player));
    }

    @Test
    void wallAbovePlayerCollision() {
        TiledMapTileLayer wallLayer = new TiledMapTileLayer(5, 5, 32, 32);
        TiledMapTileLayer doorLayer = new TiledMapTileLayer(5, 5, 32, 32);
        wallLayer.setCell(1, 2, new TiledMapTileLayer.Cell());
        Player player = new Player(32, 64 - playerHeight, wallLayer, doorLayer);

        assertTrue(Collision.collisionCheck(player));
    }

    @Test
    void wallLeftPlayerCollision() {
        TiledMapTileLayer wallLayer = new TiledMapTileLayer(5, 5, 32, 32);
        TiledMapTileLayer doorLayer = new TiledMapTileLayer(5, 5, 32, 32);
        wallLayer.setCell(0, 1, new TiledMapTileLayer.Cell());
        Player player = new Player(31, 32, wallLayer, doorLayer);

        assertTrue(Collision.collisionCheck(player));
    }

    @Test
    void wallLeftPlayerNoCollision() {
        TiledMapTileLayer wallLayer = new TiledMapTileLayer(5, 5, 32, 32);
        TiledMapTileLayer doorLayer = new TiledMapTileLayer(5, 5, 32, 32);
        wallLayer.setCell(0, 1, new TiledMapTileLayer.Cell());
        Player player = new Player(32, 0, wallLayer, doorLayer);

        assertFalse(Collision.collisionCheck(player));
    }

    @Test
    void diagonalCornerCollision() {
        TiledMapTileLayer wallLayer = new TiledMapTileLayer(5, 5, 32, 32);
        TiledMapTileLayer doorLayer = new TiledMapTileLayer(5, 5, 32, 32);
        wallLayer.setCell(2, 2, new TiledMapTileLayer.Cell());
        Player player = new Player(64 - playerWidth, 64 - playerHeight, wallLayer, doorLayer);

        assertTrue(Collision.collisionCheck(player));
    }

    @Test
    void doorCollision() {
        TiledMapTileLayer wallLayer = new TiledMapTileLayer(5, 5, 32, 32);
        TiledMapTileLayer doorLayer = new TiledMapTileLayer(5, 5, 32, 32);
        doorLayer.setCell(1, 0, new TiledMapTileLayer.Cell());
        Player player = new Player(32, 31, wallLayer, doorLayer);

        assertFalse(Player.open);
        assertTrue(Collision.door(player));
        assertTrue(Player.doorInfront);
    }

    @Test
    void doorRemovedWhenOpened() {
        TiledMapTileLayer wallLayer = new TiledMapTileLayer(5, 5, 32, 32);
        TiledMapTileLayer doorLayer = new TiledMapTileLayer(5, 5, 32, 32);
        doorLayer.setCell(1, 0, new TiledMapTileLayer.Cell());
        Player player = new Player(32, 31, wallLayer, doorLayer);

        Player.open = true;
        assertFalse(Collision.door(player));
        assertNull(doorLayer.getCell(1, 1));
    }
}
