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

    public void render(SpriteBatch batch) {
        TextureRegion frame = walkCycle.getKeyFrame(stateTime, true);
        batch.draw(frame, playerX, playerY);
    }

    public void playerInput(Key key) {
        float speed = 128f;
        float delta = Gdx.graphics.getDeltaTime();
        float distance = speed * delta;
        isMoving = false;

        if (key.collected == true) {
            if (doorInfront && Gdx.input.isKeyPressed(Input.Keys.E)) {
                open = true;
            }
        }

        if (Gdx.input.isKeyPressed(Input.Keys.RIGHT)) {
            isMoving = true;
            playerX += distance;
            if (Collision.collisionCheck(this) || Collision.door(this)) {
                playerX -= distance;
            }
        }

        if (Gdx.input.isKeyPressed(Input.Keys.LEFT)) {
            isMoving = true;
            playerX -= distance;
            if (Collision.collisionCheck(this) || Collision.door(this)) {
                playerX += distance;
            }
        }

        if (Gdx.input.isKeyPressed(Input.Keys.UP)) {
            isMoving = true;
            playerY += distance;
            if (Collision.collisionCheck(this) || Collision.door(this)) {
                playerY -= distance;
            }
        }

        if (Gdx.input.isKeyPressed(Input.Keys.DOWN)) {
            isMoving = true;
            playerY -= distance;
            if (Collision.collisionCheck(this) || Collision.door(this)) {
                playerY += distance;
            }
        }
    }
}
