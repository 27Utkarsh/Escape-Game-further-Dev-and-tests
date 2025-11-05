package com.badlogic.debugthugs;

import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;

import static com.badlogic.debugthugs.FirstScreen.collisionLayer;

public class Collision {
    public static boolean collisionCheck() {

        //checks 3 corners of the sprite to see if it's colliding with a wall.
        //I originally checked 4 corners but it made movement around corners slightly smoother if I checked 3
        //The last check allows for the player to be walking in front of the wall we're facing slightly
        if (check_wall(FirstScreen.playerX + 10, FirstScreen.playerY)) {
            return true;
        }
        if (check_wall(FirstScreen.playerX + FirstScreen.playerWidth, FirstScreen.playerY)) {
            return true;
        }
        if (check_wall(FirstScreen.playerX + FirstScreen.playerWidth, FirstScreen.playerY + (FirstScreen.playerHeight/2))) {
            return true;
        }
        return false;
    }

    private static boolean check_wall(float x, float y) {
        //Figures out the x and y coordinate of the tile the sprite is on
        int tileX = (int) (x / 32);
        int tileY = (int) (y / 32);

        //checks the tile at position (x,y)
        //if there is no tile there, return null
        //its checking the wall layer of the map, meaning if there isnt a tile in that location theres nothing to collide with
        TiledMapTileLayer.Cell cell = collisionLayer.getCell(tileX, tileY);
        if (cell == null) {
            return false;
        } else {
            return true;
        }
    }

    public static boolean door() {
        FirstScreen.doorInfront = false;
        if (check_door(FirstScreen.playerX + 10, FirstScreen.playerY)) {
            FirstScreen.doorInfront = true;
            return true;
        }
        if (check_door(FirstScreen.playerX + FirstScreen.playerWidth, FirstScreen.playerY)) {
            FirstScreen.doorInfront = true;
            return true;
        }
        if (check_door(FirstScreen.playerX + FirstScreen.playerWidth, FirstScreen.playerY + (FirstScreen.playerHeight/2))) {
            FirstScreen.doorInfront = true;
            return true;
        }
        return false;
    }

    private static boolean check_door(float x, float y) {
        int tileX = (int) (x / 32);
        int tileY = (int) (y / 32);
        TiledMapTileLayer.Cell cell = FirstScreen.doorLayer.getCell(tileX, tileY);
        if (cell == null) {
            return false;
        } else {
            if (FirstScreen.open) {
                FirstScreen.doorLayer.setCell(tileX, tileY, null);
                return false;
            }
            return true;
        }
    }
}
