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
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.graphics.GL20;
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
    TiledMap map;
    OrthogonalTiledMapRenderer renderer;

    public FirstScreen(Game game) {
        this.game = game;
    }

    @Override
    public void show() {
        viewport = new FitViewport(800, 600);
        camera = new OrthographicCamera();
        camera.setToOrtho(false, 800, 600);
        map = new TmxMapLoader().load("maps/maze_map.tmx");
        renderer = new OrthogonalTiledMapRenderer(map);
        bucketTexture = new Texture("bucket.png");
        bucketSprite = new Sprite(bucketTexture);
        bucketSprite.setSize(32,32);
        bucketSprite.setPosition(64, 64);
        camera.position.set(
            bucketSprite.getX() + bucketSprite.getWidth() / 2f,
            bucketSprite.getY() + bucketSprite.getHeight() / 2f,
            0
        );
        camera.update();
        music = Gdx.audio.newMusic(Gdx.files.internal("music.mp3"));
        dropSprites = new Array<>();
        bucketRectangle = new Rectangle();
        dropRectangle = new Rectangle();
        music.setLooping(true);
        music.setVolume(0.5f);
        music.play();
        timeBatch = new SpriteBatch();
        font = new BitmapFont();
        font.setColor(Color.WHITE);
    }

    @Override
    public void render(float delta) {
        input();
        logic();
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

        if (Gdx.input.isKeyPressed(Input.Keys.RIGHT)) {
            bucketSprite.translateX(speed * delta);
        }
        if (Gdx.input.isKeyPressed(Input.Keys.LEFT)) {
            bucketSprite.translateX(-speed * delta);
        }
        if (Gdx.input.isKeyPressed(Input.Keys.UP)) {
            bucketSprite.translateY(speed * delta);
        }
        if (Gdx.input.isKeyPressed(Input.Keys.DOWN)) {
            bucketSprite.translateY(-speed * delta);
        }

    }

    private void logic() {
        float delta = Gdx.graphics.getDeltaTime();
        float bucketWidth = bucketSprite.getWidth();
        float bucketHeight = bucketSprite.getHeight();

        bucketRectangle.set(bucketSprite.getX(), bucketSprite.getY(), bucketWidth, bucketHeight);
    }

    private void draw() {
        ScreenUtils.clear(Color.BLACK);
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
