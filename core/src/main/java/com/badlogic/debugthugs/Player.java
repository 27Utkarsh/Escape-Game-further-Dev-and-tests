package com.badlogic.debugthugs;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.math.Rectangle;

public class Player {

    public enum State {

        WALK,
        WALK_L,
        WALK_R,
        WALK_UP,
        FALL
    } // used to store different possible animation states of the player

    public String lastBusMessage = "";
    public boolean needsBusMessage = false;
    public boolean canRideBus = false;
    private float busMessageTimer = 0f;

    // initalizes the playerAnimation as the variable which stores player's State
    public State playerAnimation = State.WALK;

    public float playerX;
    public float playerY;
    public float playerWidth = 16;
    public float playerHeight = 16;

    public int goodEvent = 0;
    public int badEvent = 0;
    public int hiddenEvent = 0;

    boolean isMoving = false;
    boolean needsKeyMessage = false;
    boolean needsInteractMessage = false;

    public float speed = 128f;

    // defines the different walking animations
    // and a placeholder activeAnimation which stores the the animation in use
    Animation<TextureRegion> walk;
    Animation<TextureRegion> walkL;
    Animation<TextureRegion> walkR;
    Animation<TextureRegion> walkUp;
    Animation<TextureRegion> fall;
    Animation<TextureRegion> activeAnimation;

    TiledMapTileLayer wallLayer;
    TiledMapTileLayer doorLayer;

    public static boolean doorInfront = false;
    public static boolean open = false; // door opened

    /**
     * Constructs the player entity in the game
     *
     * @param startX    X-coordinate for where the player starts
     * @param startY    Y-coordinate for where the player starts
     * @param wallLayer Layer on the tilemap where collide-able walls are
     * @param doorLayer Layer on the tilemap where doors are
     */
    public Player(float startX, float startY,
            TiledMapTileLayer wallLayer,
            TiledMapTileLayer doorLayer) {

        this.playerX = startX;
        this.playerY = startY;
        this.playerAnimation = State.WALK;
        this.wallLayer = wallLayer;
        this.doorLayer = doorLayer;
    }

    /**
     * Renders the player's current animation frame
     * Retrieves the correct frame from the walking animation using state time
     * and draws it at the player's current X and Y position using the SpriteBatch
     *
     * @param batch the SpriteBatch used to draw the frame to the screen
     */
    public void render(SpriteBatch batch, float stateTime) {

        if (!isMoving) {
            stateTime = 0f;
        }

        switch (playerAnimation) {
            case WALK:
                activeAnimation = walk;
                break;

            case WALK_L:
                activeAnimation = walkL;
                break;

            case WALK_R:
                activeAnimation = walkR;
                break;

            case WALK_UP:
                activeAnimation = walkUp;
                break;

            case FALL:
                activeAnimation = fall;
                break;

            default:
                activeAnimation = walk;
                break;

        }

        TextureRegion frame = activeAnimation.getKeyFrame(stateTime, true);
        batch.draw(frame, playerX - 8, playerY - 10, 32, 48);

    }

    // used to change playerAnimation to = State.FALL by events
    public void playerFall() {
        playerAnimation = State.FALL;
    }

    /**
     * Handles user input
     * Specifically movement and the player interacting with doors
     */
    public void playerInput(Key key, EnergyDrink energyDrink, Bus bus, java.util.List<BusStop> busStops,
            DuoAuth duoAuth,
            WetFloor wetFloor, float delta) {

        if (energyDrink.drank) {
            speed = 160f;
        }

        if (duoAuth.active) {
            return;
        }

        if (wetFloor.active) {
            return;
        }
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

        // Bus Interaction
        boolean overlappingBus = false;
        canRideBus = false;

        if (needsBusMessage) {
            busMessageTimer += delta;
            if (busMessageTimer > 3f) {
                needsBusMessage = false;
                busMessageTimer = 0f;
            }
        }

        if (bus.bounds.overlaps(new Rectangle(playerX, playerY, playerWidth, playerHeight))) {
            overlappingBus = true;
            canRideBus = true;
            if (Gdx.input.isKeyJustPressed(Input.Keys.E)) {
                teleportToRandomStop(busStops);
            }
        }

        if (!doorInfront && !overlappingBus) {
            needsKeyMessage = false;
            needsInteractMessage = false;
        }

        if (Gdx.input.isKeyPressed(Input.Keys.RIGHT)) {
            isMoving = true;
            playerAnimation = State.WALK_R;
            playerX += distance;
            if (Collision.collisionCheck(this) || Collision.door(this)) {
                playerX -= distance;
            }
        }

        if (Gdx.input.isKeyPressed(Input.Keys.LEFT)) {
            isMoving = true;
            playerAnimation = State.WALK_L;
            playerX -= distance;
            if (Collision.collisionCheck(this) || Collision.door(this)) {
                playerX += distance;
            }
        }

        if (Gdx.input.isKeyPressed(Input.Keys.UP)) {
            isMoving = true;
            playerAnimation = State.WALK_UP;
            playerY += distance;
            if (Collision.collisionCheck(this) || Collision.door(this)) {
                playerY -= distance;
            }
        }

        if (Gdx.input.isKeyPressed(Input.Keys.DOWN)) {
            isMoving = true;
            playerAnimation = State.WALK;
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
    private void teleportToRandomStop(java.util.List<BusStop> busStops) {
        if (busStops.isEmpty())
            return;

        int index = com.badlogic.gdx.math.MathUtils.random(0, busStops.size() - 1);
        BusStop targetStop = busStops.get(index);

        playerX = targetStop.bounds.x;
        playerY = targetStop.bounds.y;

        lastBusMessage = "You took a bus to " + targetStop.name;
        needsBusMessage = true;
        busMessageTimer = 0f;

        hiddenEvent += 1;
        AchievementManager.get().unlock("TELEPORTED");
    }
}