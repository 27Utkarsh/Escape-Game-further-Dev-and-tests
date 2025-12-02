package com.badlogic.debugthugs;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.math.Rectangle;

public class Player {

    public float playerX;
    public float playerY;
    public float playerWidth;
    public float playerHeight;

    public int goodEvent = 0;
    public int badEvent = 0;
    public int hiddenEvent = 0;

    boolean isMoving = false;
    boolean needsKeyMessage = false;
    boolean needsInteractMessage = false;

    Animation<TextureRegion> walkCycle;

    TiledMapTileLayer wallLayer;
    static TiledMapTileLayer doorLayer;

    public static boolean doorInfront = false;
    public static boolean open = false; // door opened

    /**
     * Constructs the player entity in the game
     * @param startX X-coordinate for where the player starts
     * @param startY Y-coordinate for where the player starts
     * @param walkCycle Animation object holding frames to create a walk cycle
     * @param wallLayer Layer on the tilemap where collide-able walls are
     * @param doorLayer Layer on the tilemap where doors are
     */
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

    /**
     * Renders the player's current animation frame
     * Retrieves the correct frame from the walking animation using state time
     * and draws it at the player's current X and Y position using the SpriteBatch
     * @param batch the SpriteBatch used to draw the frame to the screen
     */
    public void render(SpriteBatch batch, float stateTime) {
        TextureRegion frame = walkCycle.getKeyFrame(stateTime, true);
        batch.draw(frame, playerX, playerY);
    }

    /**
     * Handles user input
     * Specifically movement and the player interacting with doors
     */
    public void playerInput(Key key, EnergyDrink energyDrink, Portal portal) {
        float speed = 128f;
        if (energyDrink.drank) {
            speed = 160f;
        }
        float delta = Gdx.graphics.getDeltaTime();
        float distance = speed * delta;
        isMoving = false;

        if (key.collected) {
            needsInteractMessage = true;
            needsKeyMessage = false;
            if (doorInfront && Gdx.input.isKeyPressed(Input.Keys.E)) {
                open = true;
                AchievementManager.get().unlock("UNLOCKED_DOOR");
            }
        } else if (key.collected == false) {
            if (doorInfront) {
                needsKeyMessage = true;
                needsInteractMessage = false;
            }
        }
        if (!doorInfront) {
            needsKeyMessage = false;
            needsInteractMessage = false;
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

    /**
     * Teleports the player to a random position on the map
     * 
     * @param randomX X-coordinate for where the player teleports to
     * @param randomY Y-coordinate for where the player teleports to
     */
    private void teleport() {
        boolean validPosition = false;
        while (!validPosition) {
            // random teleport on the map
            float randomX = com.badlogic.gdx.math.MathUtils.random(0, 2000);
            float randomY = com.badlogic.gdx.math.MathUtils.random(0, 2000);

            randomX = (int) (randomX / 32) * 32;
            randomY = (int) (randomY / 32) * 32;

            float oldX = playerX;
            float oldY = playerY;

            playerX = randomX;
            playerY = randomY;

            if (!Collision.collisionCheck(this) && !Collision.door(this)) {
                validPosition = true;
            } else {
                playerX = oldX;
                playerY = oldY;
            }
        }
    }
}
