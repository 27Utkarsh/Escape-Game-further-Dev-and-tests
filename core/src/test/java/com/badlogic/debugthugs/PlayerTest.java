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
 * Validates the basic, non-rendering logic. Tests run purely in the JVM without requiring LibGDX native backends.
 */
public class PlayerTest {
    private Player player;
    private TiledMapTileLayer dummyWallLayer;
    private TiledMapTileLayer dummyDoorLayer;
    private Animation<TextureRegion> dummyAnim;

    /**
     * Creates a dummy Player instance using empty tile layers and a single-frame animation.
     */
    @BeforeEach
    void setUp() {
        dummyWallLayer = new TiledMapTileLayer(10, 10, 32, 32);
        dummyDoorLayer = new TiledMapTileLayer(10, 10, 32, 32);

        TextureRegion region = new TextureRegion();
        dummyAnim = new Animation<>(0f, region);

        player = new Player(
            10f,
            20f,
            dummyAnim,
            dummyWallLayer,
            dummyDoorLayer
        );
    }

    /**
     * Checks that player is initialised with the correct position and size.
     */
    @Test
    void testInitialPositionAndSize() {
        assertEquals(10f, player.playerX, 0.0001f);
        assertEquals(20f, player.playerY, 0.0001f);
        assertEquals(24f, player.playerWidth, 0.0001f);
        assertEquals(24f, player.playerHeight, 0.0001f);
    }
}
