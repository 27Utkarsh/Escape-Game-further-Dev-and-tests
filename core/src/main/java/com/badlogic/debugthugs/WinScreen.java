package com.badlogic.debugthugs;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.ScreenViewport;

/**
 * The WinScreen class is displayed when the player gets to the end of the maze
 * shows a victory image, plays music, and provides a button to return to the main menu to try again
 */
public class WinScreen implements Screen {
    Texture backgroundTexture;
    SpriteBatch batch;
    Game game;
    Stage stage;
    Skin skin;
    Music music;

    /**
     * Creates a WinScreen instance.
     * @param game Reference to the main game class to allow screen switching.
     */
    public WinScreen(Game game) {
        this.game = game;
    }

    /**
     * Called when the screen becomes visible
     * Initializes buttons, loads textures (background image) and music
     */
    @Override
    public void show() {
        batch = new SpriteBatch();
        backgroundTexture = new Texture("Win.png");
        stage = new Stage(new ScreenViewport());
        Gdx.input.setInputProcessor(stage);

        music = Gdx.audio.newMusic(Gdx.files.internal("Lose.ogg"));
        music.setLooping(true);
        music.setVolume(SettingsScreen.getNoise());
        music.play();

        skin = new Skin(Gdx.files.internal("uiskin.json"));

        TextButton againButton = new TextButton("Try Again", skin);
        againButton.setPosition(20,20);
        againButton.setSize(150, 40);

        againButton.addListener(new ClickListener() {
            @Override
            /**
             * Handles when the player clicks the "Play Again" button.
             * Stops music and returns to the menu screen if button is clicked
             */
            public void clicked(InputEvent event, float x, float y) {
                music.stop();
                game.setScreen(new MenuScreen(game));
            }
        });

        stage.addActor(againButton);
    }
    /**
     * Called every frame to render the screen
     * @param delta The time in seconds since the last render.
     */
    @Override
    public void render(float delta) {
        ScreenUtils.clear(Color.RED);

        batch.begin();
        batch.draw(backgroundTexture,0,0, Gdx.graphics.getWidth(),Gdx.graphics.getHeight());
        batch.end();

        stage.act(delta);
        stage.draw();
    }

    @Override
    public void resize(int width, int height) {

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
     * Releases assets and resources used by this screen.
     * helps free memory
     */
    @Override
    public void dispose() {
        batch.dispose();
        backgroundTexture.dispose();
        stage.dispose();
        skin.dispose();
        music.dispose();
    }
}
