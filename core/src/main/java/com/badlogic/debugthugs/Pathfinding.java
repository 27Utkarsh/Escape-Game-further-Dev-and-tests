package com.badlogic.debugthugs;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.PriorityQueue;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.math.Vector2;

/**
 * Performs A* pathfinding on a {@code TiledMapTileLayer}.
 */
public class Pathfinding {
    private final TiledMapTileLayer collisionLayer;
    private final int tileWidth;
    private final int tileHeight;
    private final int mapCols;
    private final int mapRows;

    public Pathfinding(TiledMapTileLayer collisionLayer) {
        this.collisionLayer = collisionLayer;
        this.tileWidth = collisionLayer.getTileWidth();
        this.tileHeight = collisionLayer.getTileHeight();
        this.mapCols = collisionLayer.getWidth();
        this.mapRows = collisionLayer.getHeight();
    }

    /**
     * Node represents a tile in the A* graph.
     * x,y are tile coords.
     * g = cost/distance from start.
     * h = cost estimate (heuristic) of how far the goal is.
     * f = cost (g+h) = total estimated cost of traveling through this tile to the goal.
     * parent is used to reconstruct the path when the goal is reached.
     */
    private static class Node {
        int x;
        int y;
        float g;
        float h;
        Node parent;

        public Node(int x, int y) {
            this.x = x;
            this.y = y;
        }

        /**
         * Returns the A* priority.
         *
         * @return g (distance from start) + h (heuristic).
         */
        public float getF() {
            return g + h;
        }
    }

    /**
     * Returns whether a tile is walkable (i.e. no collisionLayer tiles present).
     * Also checks whether the tile coords are within the bounds of the map.
     *
     * @param tx x-pos of the tile position to check.
     * @param ty y-pos of the tile position to check.
     * @return whether tile is walkable
     */
    private boolean isWalkable(int tx, int ty) {
        if (tx < 0 || ty < 0 || tx >= mapCols || ty >= mapRows) {
            return false;
        }
        return collisionLayer.getCell(tx, ty) == null;
    }

    /**
     * Returns the heuristic between two points, using the octile distance to account for diagonals.
     */
    private float heuristic(int ax, int ay, int bx, int by) {
        float dx = Math.abs(ax - bx);
        float dy = Math.abs(ay - by);
        float min = Math.min(dx, dy);
        float max = Math.max(dx, dy);
        // 1.41421356 is approx sqrt(2)
        return 1.41421356f * min + (max - min);
    }

    public List<Vector2> findPath(float sx, float sy, float tx, float ty) {
        // Convert world coords into tile coords
        int startX = (int) (sx / tileWidth);
        int startY = (int) (sy / tileHeight);
        int goalX = (int) (tx / tileWidth);
        int goalY = (int) (ty / tileHeight);

        // Checks whether there is a walkable tile at the goal.
        if (!isWalkable(goalX, goalY)) {
            return null;
        }

        // Min-heap - nodes are ordered by f-cost value.
        PriorityQueue<Node> open = new PriorityQueue<>(Comparator.comparingDouble(Node::getF));
        boolean[][] closed = new boolean[mapCols][mapRows];
        Node[][] all = new Node[mapCols][mapRows];

        Node start = new Node(startX, startY);
        start.g = 0;
        start.h = heuristic(startX, startY, goalX, goalY);
        all[startX][startY] = start;
        open.add(start);

        final int[][] dirs = {
            {1, 0}, {-1, 0}, {0, 1}, {0, -1}, // Orthogonal
            {1, 1}, {1, -1}, {-1, 1}, {-1, -1} // Diagonal
        };

        while (!open.isEmpty()) {
            Node current = open.poll();

            if (current.x == goalX && current.y == goalY) {
                // Reconstruct path
                List<Vector2> path = new ArrayList<>();
                Node n = current;
                while (n != null) {
                    // Calculates world coords for each tile, and adds to path.
                    path.add(new Vector2(n.x * tileWidth, n.y * tileHeight));
                    n = n.parent;
                }
                Collections.reverse(path);
                prunePath(path, sx, sy);
                return path;
            }

            closed[current.x][current.y] = true;

            // neighbour exploration
            for (int[] d : dirs) {
                int nx = current.x + d[0];
                int ny = current.y + d[1];

                if (d[0] != 0 && d[1] != 0) { // diagonal movement
                    // if either adjacent orthogonal tile is blocked,
                    if (!isWalkable(current.x + d[0], current.y)
                            || !isWalkable(current.x, current.y + d[1])) {
                        continue; // disallow diagonal movement as corner is blocked.
                    }
                }

                if (!isWalkable(nx, ny)) {
                    continue;
                }
                if (closed[nx][ny]) {
                    continue;
                }

                float moveCost = (d[0] != 0 && d[1] != 0) ? 1.41421356f : 1f;
                float tentativeG = current.g + moveCost;

                Node neighbour = all[nx][ny];
                if (neighbour == null) {
                    neighbour = new Node(nx, ny);
                    all[nx][ny] = neighbour;
                    neighbour.g = tentativeG;
                    neighbour.h = heuristic(nx, ny, goalX, goalY);
                    neighbour.parent = current;
                    open.add(neighbour);
                } else if (tentativeG < neighbour.g) {
                    neighbour.g = tentativeG;
                    neighbour.parent = current;
                    // Update priority
                    open.remove(neighbour);
                    open.add(neighbour);
                }
            }
        }

        return null;
    }

    private void prunePath(List<Vector2> path, float sx, float sy) {
        if (path == null || path.size() < 2) {
            return;
        }

        while (path.size() > 1) {
            Vector2 first = path.get(0);
            float dx = first.x - sx;
            float dy = first.y - sy;

            // If path point closer than half a tile, remove it
            if (dx * dx + dy * dy < (tileWidth * tileWidth * 0.25f)) {
                path.remove(0);
            } else {
                break;
            }
        }
    }
}
