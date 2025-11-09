package com.badlogic.debugthugs;

import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;

public class Collision {
    /**
     * Checks whether the player is colliding with a wall tile
     * @param player The player whose collision boundaries are being checked
     * @return true if the player collides with a wall tile at any of the checked points, false otherwise
     */
    public static boolean collisionCheck(Player player) {

        //checks 3 corners of the sprite to see if it's colliding with a wall.
        //I originally checked 4 corners but it made movement around corners slightly smoother if I checked 3
        //The last check allows for the player to be walking in front of the wall we're facing slightly
        if (check_wall(player,player.playerX + 10, player.playerY)) {
            return true;
        }
        if (check_wall(player,player.playerX + player.playerWidth, player.playerY )) {
            return true;
        }
        if (check_wall(player,player.playerX + player.playerWidth, player.playerY + (player.playerHeight/2))) {
            return true;
        }
        return false;
    }

    /**
     * Checks whether there is a wall tile in the wallLayer at the player's coordinates, using the fact that the tiles are 32x32 pixels
     * if this function returns true, it indicates that there is a wall tile and the player should collide with it
     *
     * @param player Player whose wallLayer is referenced
     * @param x The X-Coordinate thats being checked for a wall tile
     * @param y The Y-Coordinate thats being checked for a wall tile
     * @return true if the tile at the given coordinates contains a wall tile, false otherwise
     */
    private static boolean check_wall(Player player, float x, float y) {
        //Figures out the x and y coordinate of the tile the sprite is on
        int tileX = (int) (x / 32);
        int tileY = (int) (y / 32);

        //checks the tile at position (x,y)
        //if there is no tile there, return null
        //its checking the wall layer of the map, meaning if there isnt a tile in that location theres nothing to collide with
        TiledMapTileLayer.Cell cell = player.wallLayer.getCell(tileX, tileY);
        if (cell == null) {
            return false;
        } else {
            return true;
        }
    }

    /**
     * Checks whether the player is colliding with a door tile
     *
     * @param player The player whose collision boundaries are being checked
     * @return true if the player collides with a door tile at any of the checked points, false otherwise
     */
    public static boolean door(Player player) {
        Player.doorInfront = false;
        if (check_door(player,player.playerX + 10, player.playerY)) {
            Player.doorInfront = true;
            return true;
        }
        if (check_door(player,player.playerX + player.playerWidth, player.playerY)) {
            Player.doorInfront = true;
            return true;
        }
        if (check_door(player,player.playerX + player.playerWidth, player.playerY + (player.playerHeight/2))) {
            Player.doorInfront = true;
            return true;
        }
        return false;
    }

    /**
     * Checks whether there is a door tile in the doorLayer at the player's coordinates, using the fact that the tiles are 32x32 pixels
     * if this function returns true, it indicates that there is a door tile and the player should collide with it
     * if the player has previously opened the door by interacting with it, the door will disappear and the player can walk through
     * @param player Player whose doorLayer is referenced
     * @param x The X-Coordinate thats being checked for a door tile
     * @param y The Y-Coordinate thats being checked for a door tile
     * @return true if the tile at the given coordinates contains a door tile, false otherwise
     */
    private static boolean check_door(Player player, float x, float y) {
        int tileX = (int) (x / 32);
        int tileY = (int) (y / 32);
        // Look up the tile at (tileX, tileY) in the door layer of the map
        // getCell() returns a tile cell if one exists here, otherwise it returns null
        TiledMapTileLayer.Cell cell = player.doorLayer.getCell(tileX, tileY);
        //if there is no door tile, return false
        if (cell == null) {
            return false;
        } else {
            //if player opens the door, remove the door tile at those coordinates
            if (player.open) {
                player.doorLayer.setCell(tileX, tileY, null);
                return false;
            }
            return true;
        }
    }
}
