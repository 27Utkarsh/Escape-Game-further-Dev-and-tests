package com.badlogic.debugthugs;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.maps.MapLayer;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.FitViewport;

/** First screen of the application. Displayed after the application is created. */
public class FirstScreen implements Screen {
    private static final int FRAME_COLS = 8, FRAME_ROWS = 4;
    Texture walkSheet;
    SpriteBatch spriteBatch;
    float stateTime;
    Game game;
    Texture keyTexture;
    Texture energyTexture;
    Texture pauseTexture;
    Texture enemyTexture;
    Music music;
    FitViewport viewport;
    Stage pauseStage;
    Skin skin;
    TextButton menuButton;
    SpriteBatch timeBatch;
    BitmapFont font;
    float timePassed = 300f;
    int mins;
    int seconds;
    boolean paused;
    OrthographicCamera camera;
    OrthogonalTiledMapRenderer renderer;
    Player playerChar;
    Key key;
    EnergyDrink energyDrink;
    Enemy enemy;
    HelperCharacter helper;



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

        keyTexture = new Texture("key.png");
        key = new Key(keyTexture, 1180, 1700);

        Texture helperTexture = new Texture("Enemy.png");
        helper = new HelperCharacter(helperTexture, 715, 1734);





        energyTexture = new Texture("energyDrink.png");
        energyDrink = new EnergyDrink(energyTexture, 1380, 1160);

        Pathfinding pathfinder = new Pathfinding(collisionLayer);
        enemyTexture = new Texture("Enemy.png");
        enemy = new Enemy(enemyTexture, 1340,1860, pathfinder);
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

        playerChar = new Player(710, 1730, new Animation<TextureRegion>(0.025f, walkFrames), collisionLayer, doorLayer);
        player = new Rectangle(playerChar.playerX, playerChar.playerY, playerChar.playerWidth, playerChar.playerHeight);
        spriteBatch = new SpriteBatch();
        stateTime = 0f;

        pauseStage = new Stage(viewport);
        Gdx.input.setInputProcessor(pauseStage);
        skin = new Skin(Gdx.files.internal("ui/uiskin.json"));
        paused = false;
        pauseTexture = new Texture("white3.png");

        menuButton = new TextButton("Main Menu", skin);
        menuButton.setPosition(playerChar.playerX, playerChar.playerY);
        menuButton.setSize(100, 40);
        menuButton.setVisible(false);
        menuButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                music.stop();
                game.setScreen(new MenuScreen(game));
            }
        });
        pauseStage.addActor(menuButton);
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
        if (paused == false) {
            playerChar.playerInput(key, energyDrink);
            logic();
            enemy.update(playerChar);
        }
        input();
        //sets the camera to position the sprite in the middle of the screen
        camera.position.set(
            playerChar.playerX + playerChar.playerWidth / 2f,
            playerChar.playerY + playerChar.playerHeight / 2f,
            0
        );
        camera.update();

        TextureRegion currentFrame = playerChar.walkCycle.getKeyFrame(stateTime, true);

        draw();;

        renderer.setView(camera);
        renderer.render();

        spriteBatch.setProjectionMatrix(camera.combined);
        spriteBatch.begin();
        spriteBatch.draw(currentFrame, playerChar.playerX, playerChar.playerY);
        key.checkCollected(playerChar);
        key.render(spriteBatch);
        energyDrink.checkDrank(playerChar);
        energyDrink.render(spriteBatch);
        playerChar.render(spriteBatch, stateTime);
        enemy.render(spriteBatch);
        helper.render(spriteBatch);

        font.draw(spriteBatch, "EVENTS ~ GOOD: " + playerChar.goodEvent + " BAD: " + playerChar.badEvent + " HIDDEN: " + playerChar.hiddenEvent, playerChar.playerX - 100, playerChar.playerY + 180);
        spriteBatch.end();

        if (enemy.checkCollided(playerChar)) {
            timePassed -= 30;
        }

        if (playerChar.needsKeyMessage) {
            spriteBatch.begin();
            font.draw(spriteBatch, "You need to find the key first", playerChar.playerX - 20, playerChar.playerY + 50);
            spriteBatch.end();
        }
        if (playerChar.needsInteractMessage) {
            spriteBatch.begin();
            font.draw(spriteBatch, "Press 'E' to open the door", playerChar.playerX - 20, playerChar.playerY + 50);
            spriteBatch.end();
        }

        if (paused) {
            spriteBatch.begin();
            spriteBatch.setColor(0, 0, 0, 0.5f);
            spriteBatch.draw(pauseTexture, playerChar.playerX - 400, playerChar.playerY - 300, 800, 800);
            spriteBatch.setColor(Color.WHITE);
            font.draw(spriteBatch, "Paused", playerChar.playerX, playerChar.playerY + 150);
            spriteBatch.end();
            pauseStage.act(delta);
            pauseStage.draw();
        }

        Vector3 screenPos = camera.project(new Vector3(playerChar.playerX, playerChar.playerY, 0));
        menuButton.setPosition(screenPos.x - 280, screenPos.y - 200);
        menuButton.setVisible(paused);

        //timer stuff
        if (paused == false) {
            timePassed -= delta;
        }
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
        Vector3 mousePos = new Vector3(Gdx.input.getX(), Gdx.input.getY(), 0);
        camera.unproject(mousePos);
        //System.out.println(mousePos.x + ", " +  mousePos.y);

        player.setX(playerChar.playerX);
        player.setY(playerChar.playerY);

        if (player.overlaps(exitArea)) {
            music.stop();
            game.setScreen(new WinScreen(game, timePassed));
        }

    }

    private void input() {
        if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)) {
            paused = !paused;
            if (paused) {
                Gdx.input.setInputProcessor(pauseStage);
            }
            else {
                Gdx.input.setInputProcessor(null);
            }
        }
    }

    private boolean check_book(float x, float y) {
        int tileX = (int) (x / 32);
        int tileY = (int) (y / 32);
        TiledMapTileLayer.Cell cell = bookLayer.getCell(tileX, tileY);
        if (cell == null) {
            return false;
        } else {
            return true;
        }
    }

    private void logic() {
        float delta = Gdx.graphics.getDeltaTime();
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
