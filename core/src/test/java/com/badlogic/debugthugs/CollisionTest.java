package com.badlogic.debugthugs;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;

/**
 * Tests the collision system.
 * 
 * Checks the collisions work correctly for all directions in edge cases.
 * This includes checking diagonally. 
 * 
 * Also checks door collisions and that they are disabled when the door is open.
 */
public class CollisionTest {

    private final float PLAYER_WIDTH = 16;
    private final float PLAYER_HEIGHT = 16;

    @BeforeEach
    void setUp()
    {
        // Ensure static variables (in Player) are reset to their initial state.
        Player.doorInfront = false;
        Player.open = false;
    }

    /**
     * Checks that there will be no collision when no walls (or doors) are present.
     */
    @Test
    void noWallMeansNoCollision(){
        TiledMapTileLayer wallLayer = new TiledMapTileLayer(5, 5, 32, 32);
        TiledMapTileLayer doorLayer = new TiledMapTileLayer(5, 5, 32, 32);
        Player player = new Player(32, 32, wallLayer, doorLayer);
        assertFalse(Collision.collisionCheck(player));
    }

    /**
     * Checks that the player will collide with a wall which overlaps slightly on the bottom edge.
     */
    @Test
    void wallUnderPlayerCollision()
    {
        TiledMapTileLayer wallLayer = new TiledMapTileLayer(5, 5, 32, 32);
        TiledMapTileLayer doorLayer = new TiledMapTileLayer(5, 5, 32, 32);
        wallLayer.setCell(1, 1, new TiledMapTileLayer.Cell());
        Player player = new Player(32, 63, wallLayer, doorLayer);
        assertTrue(Collision.collisionCheck(player));
    }

    /**
     * Checks that the player will not collide with a wall even if it is very close to the right side of the player.
     */
    @Test
    void wallUnderPlayerNoCollision()
    {
        TiledMapTileLayer wallLayer = new TiledMapTileLayer(5, 5, 32, 32);
        TiledMapTileLayer doorLayer = new TiledMapTileLayer(5, 5, 32, 32);
        wallLayer.setCell(1, 1, new TiledMapTileLayer.Cell());
        Player player = new Player(32, 64, wallLayer, doorLayer);
        assertFalse(Collision.collisionCheck(player));
    }

    /**
     * Checks that the player will collide with a wall on the right.
     */
    @Test
    void wallRightPlayerCollision()
    {
        TiledMapTileLayer wallLayer = new TiledMapTileLayer(5, 5, 32, 32);
        TiledMapTileLayer doorLayer = new TiledMapTileLayer(5, 5, 32, 32);
        wallLayer.setCell(2, 1, new TiledMapTileLayer.Cell());
        Player player = new Player(64 - PLAYER_WIDTH, 32, wallLayer, doorLayer);
        assertTrue(Collision.collisionCheck(player));
    }

    /**
     * Checks that the player will not collide with a close wall above.
     */
    @Test
    void wallAbovePlayerNoCollision()
    {
        TiledMapTileLayer wallLayer = new TiledMapTileLayer(5, 5, 32, 32);
        TiledMapTileLayer doorLayer = new TiledMapTileLayer(5, 5, 32, 32);
        wallLayer.setCell(1, 2, new TiledMapTileLayer.Cell());
        Player player = new Player(32, 63 - PLAYER_HEIGHT, wallLayer, doorLayer);
        assertFalse(Collision.collisionCheck(player));
    }

    /**
     * Checks that the player will collide with a wall above.
     */
    @Test
    void wallAbovePlayerCollision()
    {
        TiledMapTileLayer wallLayer = new TiledMapTileLayer(5, 5, 32, 32);
        TiledMapTileLayer doorLayer = new TiledMapTileLayer(5, 5, 32, 32);
        wallLayer.setCell(1, 2, new TiledMapTileLayer.Cell());
        Player player = new Player(32, 64 - PLAYER_HEIGHT, wallLayer, doorLayer);
        assertTrue(Collision.collisionCheck(player));
    }

    /**
     * Checks that the player will collide with a wall on the left.
     */
    @Test
    void wallLeftPlayerCollision()
    {
        TiledMapTileLayer wallLayer = new TiledMapTileLayer(5, 5, 32, 32);
        TiledMapTileLayer doorLayer = new TiledMapTileLayer(5, 5, 32, 32);
        wallLayer.setCell(0, 1, new TiledMapTileLayer.Cell());
        Player player = new Player(31, 32, wallLayer, doorLayer);
        assertTrue(Collision.collisionCheck(player));
    }

    /**
     * Checks that the player will not collide with a close wall to the left.
     */
    @Test
    void wallLeftPlayerNoCollision()
    {
        TiledMapTileLayer wallLayer = new TiledMapTileLayer(5, 5, 32, 32);
        TiledMapTileLayer doorLayer = new TiledMapTileLayer(5, 5, 32, 32);
        wallLayer.setCell(0, 1, new TiledMapTileLayer.Cell());
        Player player = new Player(32, 0, wallLayer, doorLayer);
        assertFalse(Collision.collisionCheck(player));
    }

    /**
     * Checks that collisions still work on corners.
     */
    @Test
    void diagonalCornerCollision() {
        TiledMapTileLayer wallLayer = new TiledMapTileLayer(5, 5, 32, 32);
        TiledMapTileLayer doorLayer = new TiledMapTileLayer(5, 5, 32, 32);
        wallLayer.setCell(2, 2, new TiledMapTileLayer.Cell());
        Player player = new Player(64-PLAYER_WIDTH, 64-PLAYER_HEIGHT, wallLayer, doorLayer);
        assertTrue(Collision.collisionCheck(player));
    }

    /**
     * Checks that the player can collide with the door (assuming that the door is closed).
     */
    @Test
    void doorCollision()
    {
        TiledMapTileLayer wallLayer = new TiledMapTileLayer(5, 5, 32, 32);
        TiledMapTileLayer doorLayer = new TiledMapTileLayer(5, 5, 32, 32);
        doorLayer.setCell(1, 0, new TiledMapTileLayer.Cell());
        Player player = new Player(32, 31, wallLayer, doorLayer);
        assertFalse(Player.open);
        assertTrue(Collision.door(player));
        assertTrue(Player.doorInfront);
    }

    /**
     * Checks that the player can collide with the door (assuming that the door is closed).
     */
    @Test
    void doorRemovedWhenOpened()
    {
        TiledMapTileLayer wallLayer = new TiledMapTileLayer(5, 5, 32, 32);
        TiledMapTileLayer doorLayer = new TiledMapTileLayer(5, 5, 32, 32);
        doorLayer.setCell(1, 0, new TiledMapTileLayer.Cell());
        Player player = new Player(32, 31, wallLayer, doorLayer);
        Player.open = true;

        assertFalse(Collision.door(player));
        assertNull(doorLayer.getCell(1, 1));
    }
}
