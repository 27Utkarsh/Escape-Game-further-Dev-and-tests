package com.badlogic.debugthugs;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;

import static com.badlogic.gdx.scenes.scene2d.ui.Table.Debug.cell;

public class Player {

    public float playerX;
    public float playerY;
    public float playerWidth;
    public float playerHeight;

    private float stateTime = 0f;
    boolean isMoving = false;

    Animation<TextureRegion> walkCycle;

    TiledMapTileLayer wallLayer;
    static TiledMapTileLayer doorLayer;

    public static boolean doorInfront = false;
    public static boolean open = false; // door opened

    public Player(float startX, float startY,
                  Animation<TextureRegion> walkCycle,
                  TiledMapTileLayer wallLayer,
                  TiledMapTileLayer doorLayer) {

        this.playerX = startX;
        this.playerY = startY;
        this.walkCycle = walkCycle;
        this.wallLayer = wallLayer;
        this.doorLayer = doorLayer;
        //hi

        TextureRegion first = walkCycle.getKeyFrame(0);
        this.playerWidth = 24;
        this.playerHeight = 24;
    }

    /*public void update(float delta) {
        isMoving = false;
        float speed = 128f;
        float moveAmount = speed * delta;

        if (Gdx.input.isKeyPressed(Input.Keys.RIGHT)) {
            isMoving = true;
            playerX += moveAmount;
            if (collision()) {
                playerX -= moveAmount;
            }
        }
        if (Gdx.input.isKeyPressed(Input.Keys.LEFT)) {
            isMoving = true;
            playerX -= moveAmount;
            if (collision()) {
                playerX += moveAmount;
            }
        }
        if (Gdx.input.isKeyPressed(Input.Keys.UP)) {
            isMoving = true;
            playerY += moveAmount;
            if (collision()) {
                playerY -= moveAmount;
            }
        }
        if (Gdx.input.isKeyPressed(Input.Keys.DOWN)) {
            isMoving = true;
            playerY -= moveAmount;
            if (collision()) {
                playerY += moveAmount;
            }
        }

        if (isMoving) {
            stateTime += delta;
        } else {
            stateTime = 0;
        }

        if (doorInfront) {
            if (Gdx.input.isKeyJustPressed(Input.Keys.E)) {
                open = true;
            }
        }
    }*/

    /*private boolean collision() {
        if (check_wall(playerX + 10, playerY)) {
            return true;
        }
        if (check_wall(playerX + playerWidth, playerY)) {
            return true;
        }
        if (check_wall(playerX + playerWidth, playerY + (playerHeight/2))){
            return true;
        }
        return false;
    }

    private boolean door() {
        doorInfront = false;

        if (check_door(playerX + 10, playerY)) {
            doorInfront = true;
            return true;
        }
        if (check_door(playerX + playerWidth, playerY)) {
            doorInfront = true;
            return true;
        }
        if (check_door(playerX + playerWidth, playerY + (playerHeight/2))) {
            doorInfront = true;
            return true;
        }
        return false;

    }

    private boolean check_wall(float x, float y) {
        int tileX = (int)(x / 32);
        int tileY = (int)(y / 32);
        return wallLayer.getCell(tileX, tileY) != null;
    }

    private boolean check_door(float x, float y) {
        int tileX = (int)(x / 32);
        int tileY = (int)(y / 32);
        if (cell == null) {
            return false;
        } else {
            if (open) {
                doorLayer.setCell(tileX, tileY, null);
                return false;
            }
            return true;
        }
    }*/

    public void render(SpriteBatch batch) {
        TextureRegion frame = walkCycle.getKeyFrame(stateTime, true);
        batch.draw(frame, playerX, playerY);
    }
}
