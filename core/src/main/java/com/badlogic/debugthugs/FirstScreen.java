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
    SpriteBatch spriteBatch;
    FitViewport viewport;
    Rectangle bucketRectangle;
    Rectangle dropRectangle;
    SpriteBatch timeBatch;
    BitmapFont font;
    float timePassed = 3f;
    int mins;
    int seconds;

    public FirstScreen(Game game) {
        this.game = game;
    }

    @Override
    public void show() {
        // Prepare your screen here.
        spriteBatch = new SpriteBatch();
        viewport = new FitViewport(8, 5);
        backgroundTexture = new Texture("background.png");
        backgroundSprite = new Sprite(backgroundTexture);
        backgroundSprite.setSize(20, 20);
        backgroundSprite.setPosition(0, 0);
        bucketTexture = new Texture("bucket.png");
        bucketSprite = new Sprite(bucketTexture);
        bucketSprite.setSize(1,1);
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
        draw();

        timePassed -= delta;
        if(timePassed <= 0){
            music.stop();
            game.setScreen(new LoseScreen(game));
        }
        mins =(int) timePassed / 60;
        seconds = (int) timePassed - mins * 60;
        timeBatch.begin();
        String time = String.format("%d.%02d", mins, seconds);
        font.draw(timeBatch, time, 10, viewport.getWorldHeight() + 460);
        timeBatch.end();
        // Draw your screen here. "delta" is the time since last render in seconds.
    }

    private void input() {
        float speed = 4f;
        float delta = Gdx.graphics.getDeltaTime();

        if (Gdx.input.isKeyPressed(Input.Keys.RIGHT)) {
            backgroundSprite.translateX(-speed * delta);
        } if (Gdx.input.isKeyPressed(Input.Keys.LEFT)) {
            backgroundSprite.translateX(speed * delta);
        } if (Gdx.input.isKeyPressed(Input.Keys.UP)) {
            backgroundSprite.translateY(-speed * delta);
        } if (Gdx.input.isKeyPressed(Input.Keys.DOWN)) {
            backgroundSprite.translateY(speed * delta);
        }

    }

    private void logic() {
        float worldWidth = viewport.getWorldWidth();
        float worldHeight = viewport.getWorldHeight();
        float bucketWidth = bucketSprite.getWidth();
        float bucketHeight = bucketSprite.getHeight();
        float backgroundWidth = backgroundSprite.getWidth();
        float backgroundHeight = backgroundSprite.getHeight();
        float ybound = backgroundHeight - worldHeight;
        float xbound = backgroundWidth - worldWidth;

        bucketSprite.setX(MathUtils.clamp(bucketSprite.getX(), 0, worldWidth - bucketWidth));
        backgroundSprite.setX(MathUtils.clamp(backgroundSprite.getX(), 0 - xbound, 0));
        backgroundSprite.setY(MathUtils.clamp(backgroundSprite.getY(), 0 - ybound, 0));

        float delta = Gdx.graphics.getDeltaTime();
        bucketRectangle.set(bucketSprite.getX(), bucketSprite.getY(), bucketWidth, bucketHeight);
        for (int i = dropSprites.size - 1; i >= 0; i--) {
            Sprite dropSprite = dropSprites.get(i); // Get the sprite from the list
            float dropWidth = dropSprite.getWidth();
            float dropHeight = dropSprite.getHeight();

            dropSprite.translateY(-2f * delta);
            dropRectangle.set(dropSprite.getX(), dropSprite.getY(), dropWidth, dropHeight);
            if (dropSprite.getY() < -dropHeight) dropSprites.removeIndex(i);
            else if (bucketRectangle.overlaps(dropRectangle)) {
                dropSprites.removeIndex(i);
                dropSound.play();
            }
        }
    }

    private void draw() {
        ScreenUtils.clear(Color.BLACK);
        viewport.apply();
        spriteBatch.setProjectionMatrix(viewport.getCamera().combined);
        spriteBatch.begin();
        float worldWidth = viewport.getWorldWidth();
        float worldHeight = viewport.getWorldHeight();
        spriteBatch.draw(backgroundTexture, 0, 0, worldWidth, worldHeight);
        backgroundSprite.draw(spriteBatch);
        bucketSprite.draw(spriteBatch);

        spriteBatch.end();
    }

    @Override
    public void resize(int width, int height) {
        viewport.update(width, height, true);
        // If the window is minimized on a desktop (LWJGL3) platform, width and height are 0, which causes problems.
        // In that case, we don't resize anything, and wait for the window to be a normal size before updating.
        if(width <= 0 || height <= 0) return;

        // Resize your screen here. The parameters represent the new window size.
    }

    @Override
    public void pause() {
        // Invoked when your application is paused.
    }

    @Override
    public void resume() {
        // Invoked when your application is resumed after pause.
    }

    @Override
    public void hide() {
        // This method is called when another screen replaces this one.
    }

    @Override
    public void dispose() {
        // Destroy screen's assets here.
    }
}
