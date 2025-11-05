package com.badlogic.debugthugs;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.*;
import com.badlogic.gdx.maps.MapLayer;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;

import static com.badlogic.gdx.math.Rectangle.tmp;

/** First screen of the application. Displayed after the application is created. */
public class FirstScreen implements Screen {
    private static final int FRAME_COLS = 8, FRAME_ROWS = 4;
    Animation<TextureRegion> walkCycle;
    Texture walkSheet;
    SpriteBatch spriteBatch;
    float stateTime;
    Game game;
    Texture bucketTexture;
    Sprite bucketSprite;
    Music music;
    Array<Sprite> dropSprites;
    FitViewport viewport;
    Rectangle bucketRectangle;
    Rectangle dropRectangle;
    SpriteBatch timeBatch;
    BitmapFont font;
    float timePassed = 300f;
    int mins;
    int seconds;
    OrthographicCamera camera;
    OrthogonalTiledMapRenderer renderer;

    static TiledMapTileLayer collisionLayer;
    static TiledMapTileLayer doorLayer;
    int FRAME_WIDTH = 32;
    int FRAME_HEIGHT = 32;
    TiledMapTileLayer bookLayer;

    boolean isMoving = false;
    static boolean open = false;
    static boolean doorInfront = true;


    static float playerX = 710;
    static float playerY = 1730;
    static float playerWidth = 24;
    static float playerHeight = 24;

    Rectangle exitArea = new Rectangle(1665, 1825, 800, 800);
    Rectangle player = new Rectangle(playerX, playerY, playerWidth, playerHeight);

    public FirstScreen(Game game) {
        this.game = game;
    }

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

        bucketTexture = new Texture("bucket.png");
        bucketSprite = new Sprite(bucketTexture);
        bucketSprite.setSize(32,32);
        bucketSprite.setPosition(710, 1730);

        dropSprites = new Array<>();
        bucketRectangle = new Rectangle();
        dropRectangle = new Rectangle();
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
        walkCycle = new Animation<TextureRegion>(0.025f, walkFrames);
        spriteBatch = new SpriteBatch();
        stateTime = 0f;
    }

    @Override
    public void render(float delta) {
        float animSpeed = 0.5f;
        if (isMoving) {
            stateTime += Gdx.graphics.getDeltaTime() * animSpeed;
        } else {
            stateTime = 0;
        }
        input();
        logic();
        //sets the camera to position the sprite in the middle of the screen
        camera.position.set(
            playerX + playerWidth / 2f,
            playerY+ playerHeight / 2f,
            0
        );
        camera.update();

        draw();

        renderer.setView(camera);
        renderer.render();

        TextureRegion currentFrame = walkCycle.getKeyFrame(stateTime, true);
        spriteBatch.setProjectionMatrix(camera.combined);
        spriteBatch.begin();
        spriteBatch.draw(currentFrame, playerX, playerY);
        spriteBatch.end();

        //timer stuff
        timePassed -= delta;
        if(timePassed <= 0){
            music.stop();
            game.setScreen(new LoseScreen(game));
        }
        mins =(int) timePassed / 60;
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

        player.setX(playerX);
        player.setY(playerY);

        if (player.overlaps(exitArea)) {
            music.stop();
            game.setScreen(new WinScreen(game));
        }

    }

    private void input() {
        float speed = 128f;
        float delta = Gdx.graphics.getDeltaTime();
        float moveAmount = speed * delta;
        isMoving = false;
        if (doorInfront) {
            if (Gdx.input.isKeyJustPressed(Input.Keys.E)) {
                open = true;
            }
        }
        if (Gdx.input.isKeyPressed(Input.Keys.RIGHT)) {
            isMoving = true;
            playerX += moveAmount;
            if (Collision.collisionCheck() || Collision.door()) {
                playerX -= moveAmount;
            }
        }
        if (Gdx.input.isKeyPressed(Input.Keys.LEFT)) {
            isMoving = true;
            playerX -= moveAmount;
            if (Collision.collisionCheck() || Collision.door()) {
                playerX += moveAmount;
            }
        }
        if (Gdx.input.isKeyPressed(Input.Keys.UP)) {
            isMoving = true;
            playerY += moveAmount;
            if (Collision.collisionCheck() || Collision.door()) {
                playerY -= moveAmount;
            }
        }
        if (Gdx.input.isKeyPressed(Input.Keys.DOWN)) {
            isMoving = true;
            playerY -= moveAmount;
            if (Collision.collisionCheck() || Collision.door()) {
                playerY += moveAmount;
            }
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
    } main

    private void logic() {
        float delta = Gdx.graphics.getDeltaTime();
        float bucketWidth = bucketSprite.getWidth();
        float bucketHeight = bucketSprite.getHeight();

        bucketRectangle.set(bucketSprite.getX(), bucketSprite.getY(), bucketWidth, bucketHeight);
    }

    private void draw() {
        ScreenUtils.clear(220/255f, 157/255f, 126/255f, 1);
        viewport.apply();
    }

    @Override
    public void resize(int width, int height) {
        viewport.update(width, height, true);
        if(width <= 0 || height <= 0) return;
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

    @Override
    public void dispose() {
    }
}
