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
    Texture helperTexture;
    WinScreen winScreen;
    Coin coin;
    float coinBonusPoints = 0f; // accumulate coin points

    static TiledMapTileLayer collisionLayer;
    static TiledMapTileLayer doorLayer;
    TiledMapTileLayer bookLayer;

    Rectangle exitArea = new Rectangle(1665, 1825, 800, 800);
    Rectangle player;

    AchievementManager achievements;

    public FirstScreen(Game game) {
        this.game = game;
    }

    @Override
    public void show() {
        viewport = new FitViewport(800, 600);
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

        energyTexture = new Texture("energyDrink.png");
        energyDrink = new EnergyDrink(energyTexture, 1380, 1160);

        Pathfinding pathfinder = new Pathfinding(collisionLayer);
        enemyTexture = new Texture("Enemy.png");
        enemy = new Enemy(enemyTexture, 1340, 1860, pathfinder);

        helperTexture = new Texture("helper.png"); // make sure the image exists in assets
        helper = new HelperCharacter(helperTexture, 1500, 1200); // choose a position in your map


        coin = new Coin(new Texture("coin.jpg"), 1100, 1800);
        coin.bonusPoints = 50f; // 50 points for collection

        music = Gdx.audio.newMusic(Gdx.files.internal("music.mp3"));
        music.setLooping(true);
        music.setVolume(SettingsScreen.getNoise());
        music.play();

        timeBatch = new SpriteBatch();
        font = new BitmapFont();
        font.setColor(Color.WHITE);

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

        achievements = AchievementManager.get();
        achievements.resetAll();
    }

    @Override
    public void render(float delta) {
        float animSpeed = 0.5f;
        if (playerChar.isMoving) {
            stateTime += Gdx.graphics.getDeltaTime() * animSpeed;
        } else {
            stateTime = 0;
        }
        if (!paused) {
            playerChar.playerInput(key, energyDrink);
            logic();
            enemy.update(playerChar);
        }
        input();

        camera.position.set(
            playerChar.playerX + playerChar.playerWidth / 2f,
            playerChar.playerY + playerChar.playerHeight / 2f,
            0
        );
        camera.update();

        TextureRegion currentFrame = playerChar.walkCycle.getKeyFrame(stateTime, true);
        draw();
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

        helper.checkCollected(playerChar, this);
        helper.render(spriteBatch);


        coin.checkCollected(playerChar, this);
        coin.render(spriteBatch);

        font.draw(spriteBatch, "EVENTS ~ GOOD: " + playerChar.goodEvent + " BAD: " + playerChar.badEvent + " HIDDEN: " + playerChar.hiddenEvent,
            playerChar.playerX - 100, playerChar.playerY + 180);

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

        if (!paused) {
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

        player.setX(playerChar.playerX);
        player.setY(playerChar.playerY);

        achievements.update(delta);
        achievements.render(spriteBatch, playerChar.playerX, playerChar.playerY - 150);

        if (player.overlaps(exitArea)) {
            music.stop();
            float timeSpent = 300f - timePassed;
            game.setScreen(new WinScreen(game, timeSpent, coinBonusPoints)); // pass collected coin points
            AchievementManager.get().unlock("ESCAPED");
            if (playerChar.badEvent == 0) AchievementManager.get().unlock("FLAWLESS_RUN");
        }
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

    private void logic() {
        float delta = Gdx.graphics.getDeltaTime();
    }

    private void draw() {
        ScreenUtils.clear(220 / 255f, 157 / 255f, 126 / 255f, 1);
        viewport.apply();
    }

    @Override
    public void resize(int width, int height) {
        viewport.update(width, height, true);
    }

    @Override public void pause() {}
    @Override public void resume() {}
    @Override public void hide() {}
    @Override public void dispose() {}
}
