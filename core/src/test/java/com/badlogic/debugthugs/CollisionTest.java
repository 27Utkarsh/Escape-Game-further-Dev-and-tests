package com.badlogic.debugthugs;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;

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
     * Checks that the player will not collide with a wall even if it is very close to the bottom edge of the player.
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
     * Checks that the player will not collide with a wall even if it is very close to the bottom edge of the player.
     */
    @Test
    void wallRightPlayerNoCollision()
    {
        TiledMapTileLayer wallLayer = new TiledMapTileLayer(5, 5, 32, 32);
        TiledMapTileLayer doorLayer = new TiledMapTileLayer(5, 5, 32, 32);

        wallLayer.setCell(2, 1, new TiledMapTileLayer.Cell());

        Player player = new Player(63 - PLAYER_WIDTH, 32, wallLayer, doorLayer);

        assertFalse(Collision.collisionCheck(player));
    }
}
