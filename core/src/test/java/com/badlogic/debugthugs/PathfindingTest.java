package com.badlogic.debugthugs;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.math.Vector2;
import java.util.List;
import org.junit.jupiter.api.Test;

/**
 * Unit tests for {@link Pathfinding}.
 * Checks pathfinding finds the shortest path correctly, even with obstacles,
 * and returns null when no path exists.
 */
public class PathfindingTest {

    /**
     * Checks the pathfinding works in a basic case where there is a straight
     * line between the start and the target.
     */
    @Test
    void findStraightLinePath() {
        TiledMapTileLayer tilemap = new TiledMapTileLayer(5, 5, 32, 32);
        Pathfinding pathfinding = new Pathfinding(tilemap);

        List<Vector2> path = pathfinding.findPath(0, 0, 32 * 4, 0);

        assertNotNull(path);
        assertFalse(path.isEmpty());
        Vector2 last = path.get(path.size() - 1);
        assertEquals(128, last.x, 0.01f);
        assertEquals(0, last.y, 0.01f);
    }

    /**
     * Checks pathfinding when there is an obstacle blocking a straight line path.
     */
    @Test
    void avoidWallTile() {
        TiledMapTileLayer tilemap = new TiledMapTileLayer(5, 5, 32, 32);
        tilemap.setCell(1, 0, new TiledMapTileLayer.Cell());

        Pathfinding pathfinding = new Pathfinding(tilemap);
        List<Vector2> path = pathfinding.findPath(0, 0, 128, 0);

        assertNotNull(path);
        Vector2 last = path.get(path.size() - 1);
        assertEquals(128, last.x, 0.01f);
        assertEquals(0, last.y, 0.01f);

        // Check that the wall tile is not included in the path.
        for (Vector2 p : path) {
            assertFalse(p.x == 32 && p.y == 0);
        }
    }

    /**
     * Checks pathfinding returns null when the goal is blocked i.e. no path exists.
     */
    @Test
    void returnNullIfGoalBlocked() {
        TiledMapTileLayer tilemap = new TiledMapTileLayer(5, 5, 32, 32);
        tilemap.setCell(4, 0, new TiledMapTileLayer.Cell());

        Pathfinding pathfinding = new Pathfinding(tilemap);
        List<Vector2> path = pathfinding.findPath(0, 0, 128, 0);

        assertNull(path);
    }

    /**
     * Checks that the path can include diagonals.
     */
    @Test
    void allowsDiagonalMovement() {
        TiledMapTileLayer tilemap = new TiledMapTileLayer(5, 5, 32, 32);
        Pathfinding pathfinding = new Pathfinding(tilemap);

        List<Vector2> path = pathfinding.findPath(0, 0, 64, 64);
        // Check that path size is short so diagonal movement must have been used.
        assertTrue(path.size() < 3);
    }

    /**
     * Checks that the path doesn't cut corners using diagonals.
     * e.g. XT
     *      SX
     * Where X = Walls, S = Start Position, T = Target Position
     */
    @Test
    void disablesCornerCuttingAtDiagonals() {
        TiledMapTileLayer tilemap = new TiledMapTileLayer(5, 5, 32, 32);
        tilemap.setCell(1, 0, new TiledMapTileLayer.Cell());
        tilemap.setCell(0, 1, new TiledMapTileLayer.Cell());

        Pathfinding pathfinding = new Pathfinding(tilemap);
        List<Vector2> path = pathfinding.findPath(0, 0, 32, 32);

        assertNull(path);
    }
}
