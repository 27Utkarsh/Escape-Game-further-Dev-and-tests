package com.badlogic.debugthugs;

import java.util.ArrayList;
import java.util.List;

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
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.ScreenUtils;

/**
 * First screen of the application. Displayed after the application is created.
 */
public class FirstScreen implements Screen {

    float stateTime;
    Main game;

    Texture keyTexture;
    Texture energyTexture;
    Texture busStopTexture;
    Texture busTexture;
    Texture pauseTexture;
    Texture longBoiTexture;
    Texture enemyTexture;
    Texture duoTexture;
    Texture wetFloorTexture;
    Texture examTexture;
    Texture pressureTexture;
    Texture duckTexture;
    Texture coinTexture;
    Texture helperTexture;

    Music music;
    Stage pauseStage;
    Skin skin;
    TextButton menuButton;
    TextButton resumeButton;

    public float timePassed = 300f;
    int mins;
    int seconds;
    public boolean paused = false;

    OrthogonalTiledMapRenderer renderer;
    public Player playerChar;
    Key key;
    EnergyDrink energyDrink;
    Bus bus;
    List<BusStop> busStops;
    Enemy enemy;
    HelperCharacter helper;
    Coin coin;
    LongBoi longBoi;
    Exam exam;
    Duck duck;
    DuoAuth duoAuth;
    WetFloor wetFloor;
    TiledMap map;

    static TiledMapTileLayer collisionLayer;
    static TiledMapTileLayer doorLayer;

    public Rectangle exitArea = new Rectangle(1665, 1825, 800, 800);
    public Rectangle player;

    AchievementManager achievements;

    // initalizing all animation sheets
    Texture walkSheet = new Texture(Gdx.files.internal("WalkDown.png"));
    Texture walkLeftSheet = new Texture(Gdx.files.internal("WalkLeft.png"));
    Texture walkRightSheet = new Texture(Gdx.files.internal("WalkRight.png"));
    Texture walkUpSheet = new Texture(Gdx.files.internal("WalkUp.png"));
    Texture fallSheet = new Texture(Gdx.files.internal("FallAnimation.png"));
    Texture bushSheet = new Texture(Gdx.files.internal("LongBush.png"));

    /**
     * function that takes a 2d TextureRegion and removes any empty frames and
     * returns an Animation
     */
    public static Animation<TextureRegion> frameTrimmer(TextureRegion[] array, int empty) {
        TextureRegion[] arrayOut = new TextureRegion[array.length - empty];
        for (int i = 0; i < (array.length - empty); i++) {
            arrayOut[i] = array[i];
        }
        return new Animation<>(0.05f, arrayOut);
    }

    /**
     * function that converts an 2d TextureRegion array to a linear TextureRegion
     * array
     */
    public static TextureRegion[] texture2Array(Texture sheet, int row, int col) {
        TextureRegion[][] tmp = TextureRegion.split(sheet, 32, 32);
        TextureRegion[] arrayOut = new TextureRegion[row * col];
        int index = 0;

        for (int i = 0; i < row; i++) {
            for (int j = 0; j < col; j++) {
                arrayOut[index++] = tmp[i][j];
            }
        }
        return arrayOut;
    }

    Animation<TextureRegion> walkAnimation = frameTrimmer(texture2Array(walkSheet, 3, 3), 1);
    Animation<TextureRegion> walkLeftAnimation = frameTrimmer(texture2Array(walkLeftSheet, 3, 3), 1);
    Animation<TextureRegion> walkRightAnimation = frameTrimmer(texture2Array(walkRightSheet, 3, 3), 1);
    Animation<TextureRegion> walkUpAnimation = frameTrimmer(texture2Array(walkUpSheet, 3, 3), 1);
    Animation<TextureRegion> fallAnimation = frameTrimmer(texture2Array(fallSheet, 4, 3), 0);
    Animation<TextureRegion> bushAnimation = frameTrimmer(texture2Array(bushSheet, 3, 3), 1);

    public float maxScore = 500f;
    public float playerScore = maxScore;

    /**
     * Creates a new FirstScreen instance for the game
     *
     * @param game the main LibGDX Game object used to manage screens and shared resources
     */
    public FirstScreen(Main game) {
        this.game = game;
    }

    /**
     * Sets up the camera and viewport. loads the tiled map and its collision layers,
     * prepares the player's animation and starting position, initializes rendering tools (SpriteBatch, map renderer), starts background music, prepares font and timer rendering for the HUD.
     * Also sets up the coin and helper character.
     */
    @Override
    public void show() {
        game.worldCamera.zoom = 0.6f;

        map = new TmxMapLoader().load("maps/maze_map.tmx");
        MapLayer wallsLayer = map.getLayers().get("Walls");
        MapLayer doorsLayer = map.getLayers().get("Doors");
        collisionLayer = (TiledMapTileLayer) wallsLayer;
        doorLayer = (TiledMapTileLayer) doorsLayer;
        renderer = new OrthogonalTiledMapRenderer(map);

        keyTexture = new Texture("key.png");
        key = new Key(keyTexture, 1180, 1700);

        energyTexture = new Texture("energyDrink.png");
        energyDrink = new EnergyDrink(energyTexture, 1380, 1160);

        examTexture = new Texture("Exam.png");
        pressureTexture = new Texture("Pressure.png");
        exam = new Exam(examTexture, pressureTexture, 1184, 1500, 400f, game);

        duckTexture = new Texture("MovingDuck.png");
        duck = new Duck(duckTexture, 680, 520, 250f);

        busStopTexture = new Texture("BusStop.png");
        busTexture = new Texture("Bus.png");
        bus = new Bus(busTexture, 608, 512);

        busStops = new ArrayList<>();
        busStops.add(new BusStop(busStopTexture, 992, 1856, "Stop A"));
        busStops.add(new BusStop(busStopTexture, 1184, 512, "Stop B"));
        busStops.add(new BusStop(busStopTexture, 1856, 480, "Stop C"));
        busStops.add(new BusStop(busStopTexture, 780, 1785, "Stop D"));
        busStops.add(new BusStop(busStopTexture, 1504, 1696, "Stop E"));
        busStops.add(new BusStop(busStopTexture, 1024, 1568, "Stop F"));
        busStops.add(new BusStop(busStopTexture, 480, 832, "Stop G"));
        busStops.add(new BusStop(busStopTexture, 1184, 768, "Stop H"));
        busStops.add(new BusStop(busStopTexture, 1536, 864, "Stop I"));
        busStops.add(new BusStop(busStopTexture, 1280, 1280, "Stop J"));

        Pathfinding pathfinder = new Pathfinding(collisionLayer);
        enemyTexture = new Texture("Enemy.png");
        enemy = new Enemy(enemyTexture, 1340, 1860, pathfinder, game);

        longBoiTexture = new Texture("LongBoi.png");
        longBoi = new LongBoi(longBoiTexture, bushAnimation, 448, 680);

        duoTexture = new Texture("DuoAuth.png");
        duoAuth = new DuoAuth(duoTexture, 1536f, 704f);

        wetFloorTexture = new Texture("WetFloor.png");
        wetFloor = new WetFloor(wetFloorTexture, 544, 1152);

        coinTexture = new Texture("coin.png");
        coin = new Coin(coinTexture, 1100, 1800);
        coin.bonusPoints = 50f;

        helperTexture = new Texture("helper.png");
        helper = new HelperCharacter(helperTexture, 1500, 1200);

        music = Gdx.audio.newMusic(Gdx.files.internal("music.mp3"));
        music.setLooping(true);
        music.setVolume(SettingsScreen.getNoise());
        music.play();

        game.font.getData().setScale(1f);
        game.font.setColor(Color.WHITE);

        playerChar = new Player(710, 1730, collisionLayer, doorLayer);
        player = new Rectangle(playerChar.playerX,
                               playerChar.playerY,
                               playerChar.playerWidth,
                               playerChar.playerHeight);

        stateTime = 0f;
        playerChar.walk = walkAnimation;
        playerChar.walkL = walkLeftAnimation;
        playerChar.walkR = walkRightAnimation;
        playerChar.walkUp = walkUpAnimation;
        playerChar.fall = fallAnimation;
        longBoi.longBush = bushAnimation;

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
            public void clicked(com.badlogic.gdx.scenes.scene2d.InputEvent event, float x, float y) {
                music.stop();
                game.setScreen(new MenuScreen(game));
            }
        });
        pauseStage.addActor(menuButton);
        // resume added afer user evaluation
        resumeButton = new TextButton("Resume", skin);
        resumeButton.setPosition(540, 380);
        resumeButton.setSize(200, 60);
        resumeButton.setVisible(false);
        resumeButton.addListener(new ClickListener() {
            @Override
            public void clicked(com.badlogic.gdx.scenes.scene2d.InputEvent event, float x, float y) {
                paused = false;
                Gdx.input.setInputProcessor(null);
            }
        });
        pauseStage.addActor(resumeButton);

        achievements = AchievementManager.get();
        achievements.resetAll();
    }

    /**
     * Allows initialisation of objects and variables required for the game logic to be used in testing.
     * These objects must be passed in as parameters
     * Doesn't initialise graphics or audio.
     */
    public void initLogic(Player player, Key key, EnergyDrink energyDrink, Bus bus,
                          List<BusStop> busStops, DuoAuth duoAuth, WetFloor wetFloor,
                          float enemyX, float enemyY) {
        this.playerChar = player;
        this.key = key;
        this.energyDrink = energyDrink;
        this.bus = bus;
        this.busStops = busStops;
        this.duoAuth = duoAuth;
        this.wetFloor = wetFloor;

        this.player = new Rectangle(player.playerX, player.playerY, player.playerWidth, player.playerHeight);
        this.enemy = new Enemy(enemyX, enemyY);
        this.exam = new Exam(game);
        this.duck = new Duck();
        this.coin = null;
        this.helper = null;
        this.paused = false;
        this.longBoi = new LongBoi(700, 700, false, false);

        this.achievements = AchievementManager.get();
    }

    /**
     * Updates and renders the game state for the current frame.
     * Handles player movement and animation timing, updates the camera to follow the player, renders the tile map and player sprite, updates and displays the countdown timer, checks if the player has won or lost.
     * Also checks coin and helper collection.
     *
     * @param delta time passed since the last frame (used for animation timing and timer)
     */
    @Override
    public void render(float delta) {
        logic(delta);
        renderWorld();
        renderUI(delta);
    }

    /**
     * Handles game logic updates.
     */
    public void logic(float delta) {
        if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)) {
            paused = !paused;
            if (paused) {
                Gdx.input.setInputProcessor(pauseStage);
            } else {
                Gdx.input.setInputProcessor(null);
            }
        }

        float animSpeed = 0.5f;
        if (playerChar.isMoving) {
            stateTime += delta * animSpeed;
        } else {
            stateTime = 0f;
        }

        if (!paused) {
            playerChar.playerInput(key, energyDrink, bus, busStops, duoAuth, wetFloor, delta);
            enemy.update(playerChar);
            duoAuth.checkTriggered(playerChar);
            duoAuth.update(delta);
            wetFloor.checkTriggered(playerChar);
            wetFloor.update(delta);
            exam.update(delta);
            exam.checkCollided(playerChar);
            duck.update(delta);
            duck.checkCollided(playerChar);
            if (longBoi != null) {
                longBoi.checkTriggered(playerChar);
                longBoi.update(delta);
            }

            float decayRate = maxScore / 300f;
            playerScore -= decayRate * delta;
            if (playerScore < 0) {
                playerScore = 0;
            }

            if (coin != null) {
                coin.checkCollected(playerChar, this);
            }
            if (helper != null) {
                helper.checkCollected(playerChar, this);
            }
        }

        key.checkCollected(playerChar);
        energyDrink.checkDrank(playerChar);

        if (enemy.checkCollided(playerChar)) {
            timePassed -= 30;
        }

        if (!paused) {
            timePassed -= delta;
        }
        if (timePassed <= 0) {
            if (music != null) {
                music.stop();
            }
            game.setScreen(new LoseScreen(game));
        }

        player.setX(playerChar.playerX);
        player.setY(playerChar.playerY);

        achievements.update(delta);

        if (player.overlaps(exitArea) || Gdx.input.isKeyJustPressed(Input.Keys.C)) {
            if (music != null) {
                music.stop();
            }
            game.setScreen(new WinScreen(game, playerScore));
            AchievementManager.get().unlock("ESCAPED");
            if (playerChar.badEvent == 0) {
                AchievementManager.get().unlock("FLAWLESS_RUN");
            }
        }
    }

    /**
     * Returns whether the player is overlapping the exit area.
     */
    public boolean isPlayerOverlappingExit() {
        return player.overlaps(exitArea);
    }

    private void renderWorld() {
        ScreenUtils.clear(220 / 255f, 157 / 255f, 126 / 255f, 1);
        game.worldViewport.apply();

        game.worldCamera.position.set(playerChar.playerX + playerChar.playerWidth / 2f,
                playerChar.playerY + playerChar.playerHeight / 2f, 0);
        game.worldCamera.update();

        renderer.setView(game.worldCamera);
        renderer.render();

        game.batch.setProjectionMatrix(game.worldCamera.combined);
        game.batch.begin();

        key.render(game.batch);
        energyDrink.render(game.batch);
        bus.render(game.batch);
        for (BusStop stop : busStops) {
            stop.render(game.batch);
        }
        playerChar.render(game.batch, stateTime);
        enemy.render(game.batch);
        duoAuth.render(game.batch);
        wetFloor.render(game.batch);
        exam.render(game.batch);
        duck.render(game.batch);
        longBoi.render(game.batch);

        if (coin != null) {
            coin.render(game.batch);
        }
        if (helper != null) {
            helper.render(game.batch);
        }

        game.batch.end();
    }

    private void renderUI(float delta) {
        menuButton.setVisible(paused);
        if (paused) {
            pauseStage.act(delta);
        }
        resumeButton.setVisible(paused);
        game.batch.setProjectionMatrix(game.uiCamera.combined);
        game.batch.begin();

        game.font.draw(game.batch,
                "EVENTS ~ GOOD: " + playerChar.goodEvent + " BAD: " + playerChar.badEvent + " HIDDEN: "
                        + playerChar.hiddenEvent,
                540, 650);

        game.font.draw(game.batch, "Score: " + (int) playerScore, 20, 550);

        if (playerChar.needsKeyMessage) {
            game.font.draw(game.batch, "You need to find the key first", 540, 300);
        }
        if (playerChar.needsInteractMessage) {
            game.font.draw(game.batch, "Press 'E' to open the door", 540, 300);
        }
        if (playerChar.canRideBus) {
            game.font.draw(game.batch, "Press 'E' to ride the bus", 540, 300);
        }
        if (playerChar.needsBusMessage) {
            game.font.draw(game.batch, playerChar.lastBusMessage, 540, 300);
        }
        if (duoAuth.active) {
            game.font.draw(game.batch, "Authenticating duo, Paused for 10s", 540, 300);
        }

        exam.renderOverlay(game.batch);

        if (paused) {
            game.batch.setColor(0, 0, 0, 0.5f);
            game.batch.draw(pauseTexture, 0, 0,
                            game.uiViewport.getWorldWidth(),
                            game.uiViewport.getWorldHeight());
            game.batch.setColor(Color.WHITE);
            game.font.draw(game.batch, "Paused", 600, playerChar.playerY + 500);
        }

        mins = (int) timePassed / 60;
        seconds = (int) timePassed - mins * 60;
        String time = String.format("%d.%02d", mins, seconds);
        game.font.draw(game.batch, time, 20, 580);

        achievements.render(game.batch, 640, 200);
        game.batch.end();

        if (paused) {
            pauseStage.draw();
        }
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
    public void resize(int width, int height) {
    }

    @Override
    public void dispose() {
        if (renderer != null) {
            renderer.dispose();
        }
        if (map != null) {
            map.dispose();
        }

        if (keyTexture != null) {
            keyTexture.dispose();
        }
        if (energyTexture != null) {
            energyTexture.dispose();
        }
        if (busStopTexture != null) {
            busStopTexture.dispose();
        }
        if (busTexture != null) {
            busTexture.dispose();
        }
        if (enemyTexture != null) {
            enemyTexture.dispose();
        }
        if (longBoiTexture != null) {
            longBoiTexture.dispose();
        }
        if (duoTexture != null) {
            duoTexture.dispose();
        }
        if (wetFloorTexture != null) {
            wetFloorTexture.dispose();
        }
        if (duckTexture != null) {
            duckTexture.dispose();
        }
        if (examTexture != null) {
            examTexture.dispose();
        }
        if (pressureTexture != null) {
            pressureTexture.dispose();
        }
        if (pauseTexture != null) {
            pauseTexture.dispose();
        }

        if (coin != null && coin.sprite != null && coin.sprite.getTexture() != null) {
            coin.sprite.getTexture().dispose();
        }
        if (helper != null && helper.sprite != null && helper.sprite.getTexture() != null) {
            helper.sprite.getTexture().dispose();
        }

        if (music != null) {
            music.dispose();
        }
        if (skin != null) {
            skin.dispose();
        }
        if (pauseStage != null) {
            pauseStage.dispose();
        }
        if (achievements != null) {
            achievements.dispose();
        }
    }
}
