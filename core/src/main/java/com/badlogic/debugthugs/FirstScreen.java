package com.badlogic.debugthugs;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.*;
import com.badlogic.gdx.maps.MapLayer;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;

/** First screen of the application. Displayed after the application is created. */
public class FirstScreen implements Screen {
    private static final int FRAME_COLS = 8, FRAME_ROWS = 4;
    Texture walkSheet;
    SpriteBatch spriteBatch;
    float stateTime;
    Game game;
    Music music;
    FitViewport viewport;
    SpriteBatch timeBatch;
    BitmapFont font;
    float timePassed = 300f;
    int mins;
    int seconds;
    OrthographicCamera camera;
    OrthogonalTiledMapRenderer renderer;
    Player playerChar;

    static TiledMapTileLayer collisionLayer;
    static TiledMapTileLayer doorLayer;
    TiledMapTileLayer bookLayer;

    Rectangle exitArea = new Rectangle(1665, 1825, 800, 800);
    Rectangle player;

    /**
     * Creates a new FirstScreen instance for the game
     * @param game the main LibGDX Game object used to manage screens and shared resources
     */
    public FirstScreen(Game game) {
        this.game = game;
    }

    /**
     * Sets up the camera and viewport. loads the tiled map and its collision layers,
     * prepares the player's animation and starting position, initializes rendering
     * tools (SpriteBatch, map renderer), starts background music, prepares font and timer rendering for the HUD.
     */
    @Override
    public void show() {
        viewport = new FitViewport(800, 600);
        //made a separate camera rather than using the viewport so that the timer stays in the top corner
        camera = new OrthographicCamera();
        camera.setToOrtho(false, 800, 600);
        camera.zoom = 0.6f;

        TiledMap map = new TmxMapLoader().load("maps/maze_map.tmx");
        MapLayer wallsLayer = map.getLayers().get("Walls");
        MapLayer doorsLayer = map.getLayers().get("Doors");
        MapLayer booksLayer = map.getLayers().get("Books");
        collisionLayer = (TiledMapTileLayer) wallsLayer;
        doorLayer = (TiledMapTileLayer) doorsLayer;
        bookLayer = (TiledMapTileLayer) booksLayer;
        renderer = new OrthogonalTiledMapRenderer(map);

        //music stuff
        music = Gdx.audio.newMusic(Gdx.files.internal("music.mp3"));
        music.setLooping(true);
        music.setVolume(SettingsScreen.getNoise());
        music.play();

        //timer stuff
        timeBatch = new SpriteBatch();
        font = new BitmapFont();
        font.setColor(Color.WHITE);

        //animation set up
        walkSheet = new Texture("walkfixed.png");
        TextureRegion[][] tmp = TextureRegion.split(walkSheet, 32, 32);

        TextureRegion[] walkFrames = new TextureRegion[FRAME_COLS];
        for (int col = 0; col < FRAME_COLS; col++) {
            walkFrames[col] = tmp[1][col];
        }
        //walkCycle = new Animation<TextureRegion>(0.025f, walkFrames);
        playerChar = new Player(710, 1730, new Animation<TextureRegion>(0.025f, walkFrames), collisionLayer, doorLayer);
        player = new Rectangle(playerChar.playerX, playerChar.playerY, playerChar.playerWidth, playerChar.playerHeight);
        spriteBatch = new SpriteBatch();
        stateTime = 0f;
    }

    /**
     * Updates and renders the game state for the current frame.
     * Handles player movement and animation timing, updates the camera to follow the player,
     * renders the tile map and player sprite, updates and displays the countdown timer, checks if the player has won or lost.
     * Player loses if the time runs out and wins if they overlap the exit area
     *
     * @param delta time passed since the last frame (used for animation timing when it comes to frames and also the timer)
     */
    @Override
    public void render(float delta) {
        float animSpeed = 0.5f;
        if (playerChar.isMoving) {
            stateTime += Gdx.graphics.getDeltaTime() * animSpeed;
        } else {
            stateTime = 0;
        }
        playerChar.input();
        //sets the camera to position the sprite in the middle of the screen
        camera.position.set(
            playerChar.playerX + playerChar.playerWidth / 2f,
            playerChar.playerY + playerChar.playerHeight / 2f,
            0
        );
        camera.update();

        draw();

        renderer.setView(camera);
        renderer.render();

        spriteBatch.setProjectionMatrix(camera.combined);
        spriteBatch.begin();
        playerChar.render(spriteBatch, stateTime);
        spriteBatch.end();

        //timer stuff
        timePassed -= delta;
        if (timePassed <= 0) {
            music.stop();
            game.setScreen(new LoseScreen(game));
        }
        mins = (int) timePassed / 60;
        seconds = (int) timePassed - mins * 60;
        timeBatch.setProjectionMatrix(viewport.getCamera().combined);
        timeBatch.begin();
        String time = String.format("%d.%02d", mins, seconds);
        font.draw(timeBatch, time, 20, 580);
        timeBatch.end();

        //This code will display your mouse x,y coordinates
        //Vector3 mousePos = new Vector3(Gdx.input.getX(), Gdx.input.getY(), 0);
        //camera.unproject(mousePos);
        //System.out.println(mousePos.x + ", " +  mousePos.y);

        player.setX(playerChar.playerX);
        player.setY(playerChar.playerY);

        if (player.overlaps(exitArea)) {
            music.stop();
            game.setScreen(new WinScreen(game));
        }

    }

    /**
     * Clears the screen.
     * Uses a specific colour that blends in with the tilemap rather than making empty spaces completely black
     */
    private void draw() {
        ScreenUtils.clear(220 / 255f, 157 / 255f, 126 / 255f, 1);
        viewport.apply();
    }

    /**
     * Handles resizing of the game window or viewport
     * Makes the game more compatible over different devices
     *
     * @param width  the new window width in pixels
     * @param height the new window height in pixels
     */
    @Override
    public void resize(int width, int height) {
        viewport.update(width, height, true);
        if (width <= 0 || height <= 0) return;
    }

    @Override
    public void pause() {
    }

    @Override
    public void resume() {
    }

    @Override
    public void hide() {
    }
    /**
     * Releases assets and resources used by this screen
     * helps free memory
     */
    @Override
    public void dispose() {
    }
}
