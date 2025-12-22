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
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.ScreenUtils;

public class FirstScreen implements Screen {
    private static final int FRAME_COLS = 8, FRAME_ROWS = 4;

    Texture walkSheet;
    float stateTime;
    Main game;

    Texture keyTexture, energyTexture, portalTexture, pauseTexture;
    Texture longBoiTexture, enemyTexture, duoTexture, wetFloorTexture;
    Texture examTexture, pressureTexture, duckTexture, coinTexture, helperTexture;
    Music music;
    Stage pauseStage;
    Skin skin;
    TextButton menuButton;

    float timePassed = 300f;
    int mins, seconds;
    boolean paused = false;

    OrthogonalTiledMapRenderer renderer;
    Player playerChar;
    Key key;
    EnergyDrink energyDrink;
    Portal portal;
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

    Rectangle exitArea = new Rectangle(1665, 1825, 800, 800);
    Rectangle player;

    AchievementManager achievements;

    // --- SCORE SYSTEM ---
    public float maxScore = 500f;
    public float playerScore = maxScore; // decreases over time

    public FirstScreen(Main game) {
        this.game = game;
    }

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

        portalTexture = new Texture("portal.png");
        portal = new Portal(portalTexture, 608, 512);

        Pathfinding pathfinder = new Pathfinding(collisionLayer);
        enemyTexture = new Texture("Enemy.png");
        enemy = new Enemy(enemyTexture, 1340, 1860, pathfinder, game);

        longBoiTexture = new Texture("LongBoi.png");
        longBoi = new LongBoi(longBoiTexture, 448, 680);

        duoTexture = new Texture("DuoAuth.png");
        duoAuth = new DuoAuth(duoTexture, 1536f, 704f);

        wetFloorTexture = new Texture("WetFloor.png");
        wetFloor = new WetFloor(wetFloorTexture, 544, 1152);

        coinTexture = new Texture("coin.jpg");
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

        // --- Player animation setup ---
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
            public void clicked(com.badlogic.gdx.scenes.scene2d.InputEvent event, float x, float y) {
                music.stop();
                game.setScreen(new MenuScreen(game));
            }
        });
        pauseStage.addActor(menuButton);

        achievements = AchievementManager.get();
        achievements.resetAll();
    }

    @Override
    public void render(float delta) {
        logic(delta);
        renderWorld();
        renderUI();
    }

    private void logic(float delta) {
        if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)) {
            paused = !paused;
            if (paused) Gdx.input.setInputProcessor(pauseStage);
            else Gdx.input.setInputProcessor(null);
        }

        float animSpeed = 0.5f;
        if (playerChar.isMoving) stateTime += delta * animSpeed;
        else stateTime = 0f;

        if (!paused) {
            playerChar.playerInput(key, energyDrink, portal, duoAuth, wetFloor, longBoi);
            enemy.update(playerChar);
            duoAuth.checkTriggered(playerChar);
            duoAuth.update(delta);
            wetFloor.checkTriggered(playerChar);
            wetFloor.update(delta);
            exam.update(delta);
            exam.checkCollided(playerChar);
            duck.update(delta);
            duck.checkCollided(playerChar);
            longBoi.checkTriggered(playerChar);

            // --- TIME-BASED SCORE ---
            float decayRate = maxScore / 300f;
            playerScore -= decayRate * delta;
            if (playerScore < 0) playerScore = 0;

            // --- Collect coin & helper ---
            if (coin != null) coin.checkCollected(playerChar, this);
            if (helper != null) helper.checkCollected(playerChar, this);
        }

        key.checkCollected(playerChar);
        energyDrink.checkDrank(playerChar);

        if (enemy.checkCollided(playerChar)) timePassed -= 30;

        menuButton.setVisible(paused);
        if (paused) pauseStage.act(delta);

        if (!paused) timePassed -= delta;
        if (timePassed <= 0) {
            music.stop();
            game.setScreen(new LoseScreen(game));
        }

        player.setX(playerChar.playerX);
        player.setY(playerChar.playerY);

        achievements.update(delta);

        if (player.overlaps(exitArea) || Gdx.input.isKeyJustPressed(Input.Keys.C)) {
            music.stop();
            game.setScreen(new WinScreen(game, playerScore));
            AchievementManager.get().unlock("ESCAPED");
            if (playerChar.badEvent == 0)
                AchievementManager.get().unlock("FLAWLESS_RUN");
        }
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
        portal.render(game.batch);
        playerChar.render(game.batch, stateTime);
        enemy.render(game.batch);
        duoAuth.render(game.batch);
        wetFloor.render(game.batch);
        exam.render(game.batch);
        duck.render(game.batch);
        longBoi.render(game.batch);

        // --- RENDER COIN & HELPER ---
        if (coin != null) coin.render(game.batch);
        if (helper != null) helper.render(game.batch);

        game.batch.end();
    }

    private void renderUI() {
        game.batch.setProjectionMatrix(game.uiCamera.combined);
        game.batch.begin();

        game.font.draw(game.batch, "EVENTS ~ GOOD: " + playerChar.goodEvent + " BAD: " + playerChar.badEvent + " HIDDEN: "
            + playerChar.hiddenEvent, 540, 650);

        game.font.draw(game.batch, "Score: " + (int) playerScore, 20, 550);

        if (playerChar.needsKeyMessage) game.font.draw(game.batch, "You need to find the key first", 540, 300);
        if (playerChar.needsInteractMessage) game.font.draw(game.batch, "Press 'E' to open the door", 540, 300);
        if (duoAuth.active) game.font.draw(game.batch, "Authenticating duo, Paused for 10s", 540, 300);

        exam.renderOverlay(game.batch);

        if (paused) {
            game.batch.setColor(0, 0, 0, 0.5f);
            game.batch.draw(pauseTexture, 0, 0, game.uiViewport.getWorldWidth(), game.uiViewport.getWorldHeight());
            game.batch.setColor(Color.WHITE);
            game.font.draw(game.batch, "Paused", 600, playerChar.playerY + 500);
        }

        mins = (int) timePassed / 60;
        seconds = (int) timePassed - mins * 60;
        String time = String.format("%d.%02d", mins, seconds);
        game.font.draw(game.batch, time, 20, 580);

        achievements.render(game.batch, 640, 200);
        game.batch.end();

        if (paused) pauseStage.draw();
    }

    @Override public void pause() {}
    @Override public void resume() {}
    @Override public void hide() {}
    @Override public void resize(int width, int height) {}
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
        if (duckTexture != null) duckTexture.dispose();
        if (examTexture != null) examTexture.dispose();
        if (pressureTexture != null) pressureTexture.dispose();
        if (pauseTexture != null) pauseTexture.dispose();

        if (coin != null && coin.sprite != null && coin.sprite.getTexture() != null)
            coin.sprite.getTexture().dispose();
        if (helper != null && helper.sprite != null && helper.sprite.getTexture() != null)
            helper.sprite.getTexture().dispose();

        if (music != null) music.dispose();
        if (skin != null) skin.dispose();
        if (pauseStage != null) pauseStage.dispose();
        if (achievements != null) achievements.dispose();
    }
}
