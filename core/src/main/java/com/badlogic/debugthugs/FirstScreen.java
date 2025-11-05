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
    TiledMapTileLayer collisionLayer;
    TiledMapTileLayer doorLayer;
    TiledMapTileLayer bookLayer;
    boolean isMoving = false;
    boolean open = false;
    boolean doorInfront = true;


    float playerX = 710;
    float playerY = 1730;
    float playerWidth = 24;
    float playerHeight = 24;

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
            if (collision() || door()) {
                playerX -= moveAmount;
            }
        }
        if (Gdx.input.isKeyPressed(Input.Keys.LEFT)) {
            isMoving = true;
            playerX -= moveAmount;
            if (collision() || door()) {
                playerX += moveAmount;
            }
        }
        if (Gdx.input.isKeyPressed(Input.Keys.UP)) {
            isMoving = true;
            playerY += moveAmount;
            if (collision() || door()) {
                playerY -= moveAmount;
            }
        }
        if (Gdx.input.isKeyPressed(Input.Keys.DOWN)) {
            isMoving = true;
            playerY -= moveAmount;
            if (collision() || door()) {
                playerY += moveAmount;
            }
        }

    }

    private boolean door() {
        doorInfront = false;
        if (check_door(playerX + 10, playerY)) {
            doorInfront = true;
            return true;
        }
        if (check_door(playerX + playerWidth, playerY)) {
            doorInfront = true;
            return true;
        }
        if (check_door(playerX + playerWidth, playerY + (playerHeight/2))) {
            doorInfront = true;
            return true;
        }
        return false;
    }

    private boolean collision() {

        //checks 3 corners of the sprite to see if it's colliding with a wall.
        //I originally checked 4 corners but it made movement around corners slightly smoother if I checked 3
        //The last check allows for the player to be walking in front of the wall we're facing slightly
        if (check_wall(playerX + 10, playerY)) {
            return true;
        }
        if (check_wall(playerX + playerWidth, playerY)) {
            return true;
        }
        if (check_wall(playerX + playerWidth, playerY + (playerHeight/2))) {
            return true;
        }
        return false;
    }
    private boolean check_door(float x, float y) {
        int tileX = (int) (x / 32);
        int tileY = (int) (y / 32);
        TiledMapTileLayer.Cell cell = doorLayer.getCell(tileX, tileY);
        if (cell == null) {
            return false;
        } else {
            if (open) {
                doorLayer.setCell(tileX, tileY, null);
                return false;
            }
            return true;
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
    private boolean check_wall(float x, float y) {
        //Figures out the x and y coordinate of the tile the sprite is on
        int tileX = (int) (x / 32);
        int tileY = (int) (y / 32);

        //checks the tile at position (x,y)
        //if there is no tile there, return null
        //its checking the wall layer of the map, meaning if there isnt a tile in that location theres nothing to collide with
        TiledMapTileLayer.Cell cell = collisionLayer.getCell(tileX, tileY);
        if (cell == null) {
            return false;
        } else {
            return true;
        }
    }

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
