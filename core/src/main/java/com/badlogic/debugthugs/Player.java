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
    TiledMapTileLayer doorLayer;

    public static boolean doorInfront = false;
    public static boolean open = false; // door opened

    /**
     * Constructs the player entity in the game
     *
     * @param startX    X-coordinate for where the player starts
     * @param startY    Y-coordinate for where the player starts
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

        this.playerWidth = 12;
        this.playerHeight = 14;
    }

    /**
     * Renders the player's current animation frame
     * Retrieves the correct frame from the walking animation using state time
     * and draws it at the player's current X and Y position using the SpriteBatch
     *
     * @param batch the SpriteBatch used to draw the frame to the screen
     */
    public void render(SpriteBatch batch, float stateTime) {
        TextureRegion frame = walkCycle.getKeyFrame(stateTime, true);
        batch.draw(frame, playerX - 10, playerY - 10);
    }

    /**
     * Handles user input
     * Specifically movement and the player interacting with doors
     */
    public void playerInput(Key key, EnergyDrink energyDrink, Portal portal, DuoAuth duoAuth, WetFloor wetFloor) {
        float speed = 128f;
        if (energyDrink.drank) {
            speed = 160f;
        }
    }
    public void playerInput(Key key, EnergyDrink energyDrink, Portal portal, DuoAuth duoAuth, WetFloor wetFloor, 	LongBoi longBoi) {
        float speed = 128f;
        if (energyDrink.drank) {
            speed = 160f;
        }


        if (duoAuth.active) {
            return;
        }

        if (wetFloor.active){
            return;
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

        // Portal Interaction
        if (!portal.used && portal.bounds.overlaps(new Rectangle(playerX, playerY, playerWidth, playerHeight))) {
            needsInteractMessage = true;
            if (Gdx.input.isKeyJustPressed(Input.Keys.E)) {
                teleport();
                portal.used = true;
            }
        }

        if (!doorInfront && (!portal.bounds.overlaps(new Rectangle(playerX, playerY, playerWidth, playerHeight))
                || portal.used)) {
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
            float randomX = com.badlogic.gdx.math.MathUtils.random(400, 1500);
            float randomY = com.badlogic.gdx.math.MathUtils.random(400, 1500);

            randomX = (int) (randomX / 32) * 32;
            randomY = (int) (randomY / 32) * 32;

            float oldX = playerX;
            float oldY = playerY;

            playerX = randomX;
            playerY = randomY;

            if (!Collision.collisionCheck(this) && !Collision.door(this)) {
                validPosition = true;
                hiddenEvent += 1;
                AchievementManager.get().unlock("TELEPORTED");
            } else {
                playerX = oldX;
                playerY = oldY;
            }
        }
    }
}
