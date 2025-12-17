package com.badlogic.debugthugs;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.maps.MapLayer;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.ScreenUtils;

/**
 * First screen of the application. Displayed after the application is created.
 */
public class FirstScreen implements Screen {
    private static final int FRAME_COLS = 8, FRAME_ROWS = 4;
    Texture walkSheet;
    float stateTime;
    Main game;
    Texture keyTexture;
    Texture energyTexture;
    Texture portalTexture;
    Texture pauseTexture;
    Texture longBoiTexture;
    Texture enemyTexture;
    Texture duoTexture;
    Texture wetFloorTexture;
    Music music;
    Stage pauseStage;
    Skin skin;
    TextButton menuButton;
    float timePassed = 300f;
    int mins;
    int seconds;
    boolean paused = false;
    OrthogonalTiledMapRenderer renderer;
    Player playerChar;
    Key key;
    EnergyDrink energyDrink;
    Portal portal;
    Enemy enemy;
    LongBoi longBoi;
    DuoAuth duoAuth;
    WetFloor wetFloor;
    TiledMap map;
    //

    static TiledMapTileLayer collisionLayer;
    static TiledMapTileLayer doorLayer;
    //TiledMapTileLayer bookLayer;

    Rectangle exitArea = new Rectangle(1665, 1825, 800, 800);
    Rectangle player;

    AchievementManager achievements;

    /**
     * Creates a new FirstScreen instance for the game
     * 
     * @param game the main LibGDX Game object used to manage screens and shared
     *             resources
     */
    public FirstScreen(Main game) {
        this.game = game;
    }

    /**
     * Sets up the camera and viewport. loads the tiled map and its collision
     * layers,
     * prepares the player's animation and starting position, initializes rendering
     * tools (SpriteBatch, map renderer), starts background music, prepares font and
     * timer rendering for the HUD.
     */
    @Override
    public void show() {
        // made a separate camera rather than using the viewport so that the timer stays
        // in the top corner
        //game.worldCamera.setToOrtho(false, 800, 600);
        game.worldCamera.zoom = 0.6f;

        map = new TmxMapLoader().load("maps/maze_map.tmx");
        MapLayer wallsLayer = map.getLayers().get("Walls");
        MapLayer doorsLayer = map.getLayers().get("Doors");
        //MapLayer booksLayer = map.getLayers().get("Books");
        collisionLayer = (TiledMapTileLayer) wallsLayer;
        doorLayer = (TiledMapTileLayer) doorsLayer;
        //bookLayer = (TiledMapTileLayer) booksLayer;
        renderer = new OrthogonalTiledMapRenderer(map);

        keyTexture = new Texture("Key.png");
        key = new Key(keyTexture, 1180, 1700);

        energyTexture = new Texture("energyDrink.png");
        energyDrink = new EnergyDrink(energyTexture, 1380, 1160);

        portalTexture = new Texture("portal.png");
        portal = new Portal(portalTexture, 608, 512);

        Pathfinding pathfinder = new Pathfinding(collisionLayer);
        enemyTexture = new Texture("Enemy.png");
        enemy = new Enemy(enemyTexture, 1340, 1860, pathfinder);

        longBoiTexture = new Texture("LongBoi.png");
        longBoi = new LongBoi(longBoiTexture, 448, 680);

        duoTexture = new Texture("DuoAuth.png");
        duoAuth = new DuoAuth(duoTexture, 1536f, 704f);

        wetFloorTexture = new Texture("WetFloor.png");
        wetFloor = new WetFloor(wetFloorTexture,  544, 1152);

        // music stuff
        music = Gdx.audio.newMusic(Gdx.files.internal("music.mp3"));
        music.setLooping(true);
        music.setVolume(SettingsScreen.getNoise());
        music.play();

        game.font.getData().setScale(1f);
        game.font.setColor(Color.WHITE);

        // animation set up
        walkSheet = new Texture("walkfixed.png");
        TextureRegion[][] tmp = TextureRegion.split(walkSheet, 32, 32);

        TextureRegion[] walkFrames = new TextureRegion[FRAME_COLS];
        for (int col = 0; col < FRAME_COLS; col++) {
            walkFrames[col] = tmp[1][col];
        }

        playerChar = new Player(710, 1730, new Animation<TextureRegion>(0.025f, walkFrames), collisionLayer, doorLayer);
        player = new Rectangle(playerChar.playerX, playerChar.playerY, playerChar.playerWidth, playerChar.playerHeight);
        stateTime = 0f;

        pauseStage = new Stage(game.uiViewport, game.batch);
        Gdx.input.setInputProcessor(pauseStage);
        skin = new Skin(Gdx.files.internal("ui/uiskin.json"));
        paused = false;
        pauseTexture = new Texture("white3.png");

        menuButton = new TextButton("Main Menu", skin);
        menuButton.setPosition(540, 300);
        menuButton.setSize(200, 60);
        menuButton.setVisible(false);
        menuButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                music.stop();
                game.setScreen(new MenuScreen(game));
            }
        });
        pauseStage.addActor(menuButton);

        achievements = AchievementManager.get();
        achievements.resetAll(); // TODO: This is only for testing purposes - delete before submitting project
    }

    /**
     * Updates and renders the game state for the current frame.
     * Handles player movement and animation timing, updates the camera to follow
     * the player,
     * renders the tile map and player sprite, updates and displays the countdown
     * timer, checks if the player has won or lost.
     * Player loses if the time runs out and wins if they overlap the exit area
     *
     * @param delta time passed since the last frame (used for animation timing when
     *              it comes to frames and also the timer)
     */
    @Override
    public void render(float delta) {
        logic(delta);

        renderWorld();
        renderUI();

        //draw();

        //Vector3 screenPos = camera.project(new Vector3(playerChar.playerX, playerChar.playerY, 0));
        

        // This code will display your mouse x,y coordinates
        // Vector3 mousePos = new Vector3(Gdx.input.getX(), Gdx.input.getY(), 0);
        // camera.unproject(mousePos);
        // System.out.println(mousePos.x + ", " + mousePos.y);
    }

    private void input() {
        if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)) {
            paused = !paused;
            if (paused) {
                Gdx.input.setInputProcessor(pauseStage);
            } else {
                Gdx.input.setInputProcessor(null);
            }
        }
    }

    private void logic(float delta) {
        input();

        float animSpeed = 0.5f;
        if (playerChar.isMoving) {
            stateTime += delta * animSpeed;
        } else {
            stateTime = 0;
        }

        if (!paused) {
            playerChar.playerInput(key, energyDrink, portal, duoAuth, wetFloor, longBoi);
            
            enemy.update(playerChar);
            duoAuth.checkTriggered(playerChar);
            duoAuth.update(delta);
            wetFloor.checkTriggered(playerChar);
            wetFloor.update(delta);
            longBoi.checkTriggered(playerChar);
        }

        key.checkCollected(playerChar);
        energyDrink.checkDrank(playerChar);

        if (enemy.checkCollided(playerChar)) {
            timePassed -= 30;
        }

        menuButton.setVisible(paused);
        if (paused) pauseStage.act(delta);
        
        if (paused == false) {
            timePassed -= delta;
        }
        if (timePassed <= 0) {
            music.stop();
            game.setScreen(new LoseScreen(game));
        }

        player.setX(playerChar.playerX);
        player.setY(playerChar.playerY);

        achievements.update(delta);

        if (player.overlaps(exitArea)) {
            music.stop();
            game.setScreen(new WinScreen(game, timePassed));
            AchievementManager.get().unlock("ESCAPED");
            if (playerChar.badEvent == 0)
                AchievementManager.get().unlock("FLAWLESS_RUN");
        }
    }

    private void renderWorld() {
        ScreenUtils.clear(220 / 255f, 157 / 255f, 126 / 255f, 1);
        game.worldViewport.apply();

        // sets the camera to position the sprite in the middle of the screen
        game.worldCamera.position.set(
                playerChar.playerX + playerChar.playerWidth / 2f,
                playerChar.playerY + playerChar.playerHeight / 2f,
                0);
        game.worldCamera.update();

        renderer.setView(game.worldCamera);
        renderer.render();

        game.batch.setProjectionMatrix(game.worldCamera.combined);

        game.batch.begin();

        TextureRegion currentFrame = playerChar.walkCycle.getKeyFrame(stateTime, true);
        game.batch.draw(currentFrame, playerChar.playerX, playerChar.playerY);

        key.render(game.batch);
        energyDrink.render(game.batch);
        portal.render(game.batch);
        playerChar.render(game.batch, stateTime);
        enemy.render(game.batch);
        duoAuth.render(game.batch);
        wetFloor.render(game.batch);
	    longBoi.render(game.batch);

        game.batch.end();
    }

    private void renderUI(){
        game.batch.setProjectionMatrix(game.uiCamera.combined);
        game.batch.begin();

        game.font.draw(game.batch, "EVENTS ~ GOOD: " + playerChar.goodEvent + " BAD: " + playerChar.badEvent + " HIDDEN: "
                + playerChar.hiddenEvent, 540, 650);

        if (playerChar.needsKeyMessage) {
            game.font.draw(game.batch, "You need to find the key first", 540, 300);
        }
        if (playerChar.needsInteractMessage) {
            game.font.draw(game.batch, "Press 'E' to open the door", 540, 300);
        }
        if (duoAuth.active) {
            game.font.draw(game.batch, "Authenticating duo, Paused for 10s", 540, 300);
        }

        if (paused) {
            game.batch.setColor(0, 0, 0, 0.5f);
            game.batch.draw(pauseTexture, 0, 0, game.uiViewport.getWorldWidth(), game.uiViewport.getWorldHeight());
            game.batch.setColor(Color.WHITE);
            game.font.draw(game.batch, "Paused", 600, playerChar.playerY + 500);
            
        }

        // timer stuff
        mins = (int) timePassed / 60;
        seconds = (int) timePassed - mins * 60;
        String time = String.format("%d.%02d", mins, seconds);
        game.font.draw(game.batch, time, 20, 580);

        achievements.render(game.batch, 640, 200);

        game.batch.end();
                
        if (paused) pauseStage.draw();
    }

    /**
     * Clears the screen.
     * Uses a specific colour that blends in with the tilemap rather than making
     * empty spaces completely black
     */
    private void draw() {

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
        if (renderer != null) renderer.dispose();
        if (map != null) map.dispose();

        if (walkSheet != null) walkSheet.dispose();
        if (keyTexture != null) keyTexture.dispose();
        if (energyTexture != null) energyTexture.dispose();
        if (portalTexture != null) portalTexture.dispose();
        if (enemyTexture != null) enemyTexture.dispose();
        if (longBoiTexture != null) longBoiTexture.dispose();
        if (duoTexture != null) duoTexture.dispose();
        if (wetFloorTexture != null) wetFloorTexture.dispose();
        if (pauseTexture != null) pauseTexture.dispose();
        
        if (music != null) music.dispose();

        if (skin != null) skin.dispose();
        if (pauseStage != null) pauseStage.dispose();

        if (achievements != null) achievements.dispose();
    }

    @Override
    public void resize(int width, int height) {
    }

        // private boolean check_book(float x, float y) {
    //     int tileX = (int) (x / 32);
    //     int tileY = (int) (y / 32);
    //     TiledMapTileLayer.Cell cell = bookLayer.getCell(tileX, tileY);
    //     if (cell == null) {
    //         return false;
    //     } else {
    //         return true;
    //     }
    // }
}
