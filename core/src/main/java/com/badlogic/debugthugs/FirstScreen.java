package com.badlogic.debugthugs;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.maps.MapLayer;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;

/** First screen of the application. Displayed after the application is created. */
public class FirstScreen implements Screen {
    Game game;
    Texture backgroundTexture;
    Sprite backgroundSprite;
    Texture bucketTexture;
    Sprite bucketSprite;
    Sound dropSound;
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
    TiledMap map = new TmxMapLoader().load("maps/maze_map.tmx");
    OrthogonalTiledMapRenderer renderer;
    private TiledMapTileLayer collisionLayer;

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
        MapLayer wallsLayer = map.getLayers().get("Walls");
        collisionLayer = (TiledMapTileLayer) wallsLayer;
        renderer = new OrthogonalTiledMapRenderer(map);

        bucketTexture = new Texture("bucket.png");
        bucketSprite = new Sprite(bucketTexture);
        bucketSprite.setSize(30,30);
        bucketSprite.setPosition(710, 1730);

        dropSprites = new Array<>();
        bucketRectangle = new Rectangle();
        dropRectangle = new Rectangle();
        //music stuff
        music = Gdx.audio.newMusic(Gdx.files.internal("music.mp3"));
        music.setLooping(true);
        music.setVolume(0.5f);
        music.play();
        //timer stuff
        timeBatch = new SpriteBatch();
        font = new BitmapFont();
        font.setColor(Color.WHITE);
    }

    @Override
    public void render(float delta) {
        input();
        logic();
        //sets the camera to position the sprite in the middle of the screen
        camera.position.set(
            bucketSprite.getX() + bucketSprite.getWidth() / 2f,
            bucketSprite.getY() + bucketSprite.getHeight() / 2f,
            0
        );
        camera.update();

        draw();

        renderer.setView(camera);
        renderer.render();

        renderer.getBatch().begin();
        bucketSprite.draw(renderer.getBatch());
        renderer.getBatch().end();

        //timer stuff
        timePassed -= delta;
        if(timePassed <= 0){
            game.setScreen(new LoseScreen(game));
        }
        mins =(int) timePassed / 60;
        seconds = (int) timePassed - mins * 60;
        timeBatch.setProjectionMatrix(viewport.getCamera().combined);
        timeBatch.begin();
        String time = String.format("%d.%02d", mins, seconds);
        font.draw(timeBatch, time, 20, 580);
        timeBatch.end();

    }

    private void input() {
        float speed = 128f;
        float delta = Gdx.graphics.getDeltaTime();
        float moveAmount = speed * delta;

        if (Gdx.input.isKeyPressed(Input.Keys.RIGHT)) {
            bucketSprite.translateX(moveAmount);
            if (collision()) {
                bucketSprite.translateX(-moveAmount);
            }
        }
        if (Gdx.input.isKeyPressed(Input.Keys.LEFT)) {
            bucketSprite.translateX(-moveAmount);
            if (collision()) {
                bucketSprite.translateX(moveAmount);
            }
        }
        if (Gdx.input.isKeyPressed(Input.Keys.UP)) {
            bucketSprite.translateY(moveAmount);
            if (collision()) {
                bucketSprite.translateY(-moveAmount);
            }
        }
        if (Gdx.input.isKeyPressed(Input.Keys.DOWN)) {
            bucketSprite.translateY(-moveAmount);
            if (collision()) {
                bucketSprite.translateY(moveAmount);
            }
        }

    }

    private boolean collision() {
        float x = bucketSprite.getX();
        float y = bucketSprite.getY();
        float width = bucketSprite.getWidth();
        float height = bucketSprite.getHeight();

        //checks 3 corners of the sprite to see if it's colliding with a wall.
        //I originally checked 4 corners but it made movement around corners slightly smoother if I checked 3
        //The last check allows for the player to be walking in front of the wall we're facing slightly
        if (check_wall(x, y)) {
            return true;
        }
        if (check_wall(x + (width), y)) {
            return true;
        }
        if (check_wall(x + width, y + (height/2))) {
            return true;
        }
        return false;
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
