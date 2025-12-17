package com.badlogic.debugthugs;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.ScreenUtils;

public class LoseScreen implements Screen {
    Texture backgroundTexture;
    Main game;
    Stage stage;
    Skin skin;
    Music music;
    /**
     * Creates a new LoseScreen.
     * @param game reference to the main game instance for screen switching
     */
    public LoseScreen(Main game) {
        this.game = game;
    }

    /**
     * Called when the lose screen becomes the current screen
     * initialises the game over image, music and waits for the player to press the try again button
     */
    @Override
    public void show() {
        stage = new Stage(game.uiViewport);
        Gdx.input.setInputProcessor(stage);

        skin = new Skin(Gdx.files.internal("uiskin.json"));

        backgroundTexture = new Texture("Game_Over_Image.png");
        
        Table root = new Table();
        root.setFillParent(true);

        TextButton againButton = new TextButton("Try Again", skin);

        againButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                music.stop();
                game.setScreen(new MenuScreen(game));
            }
        });

        root.add(againButton).width(300).height(80).padBottom(80);
        root.align(Align.bottom);

        root.setBackground(new TextureRegionDrawable(backgroundTexture));
        stage.addActor(root);

        music = Gdx.audio.newMusic(Gdx.files.internal("Lose.ogg"));
        music.setLooping(true);
        music.setVolume(SettingsScreen.getNoise());
        music.play();
    }

    /**
     * Renders the screen every frame.
     * @param delta The time in seconds since the last render.
     */
    @Override
    public void render(float delta) {
        ScreenUtils.clear(Color.BLACK);

        game.batch.setProjectionMatrix(game.uiCamera.combined);
        game.batch.begin();
        game.batch.draw(backgroundTexture,0,0, game.uiViewport.getWorldWidth(), game.uiViewport.getWorldHeight());
        game.batch.end();

        stage.act(delta);
        stage.draw();
    }

    @Override
    public void resize(int width, int height) {
        stage.getViewport().update(width, height, true);
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
        backgroundTexture.dispose();
        stage.dispose();
        skin.dispose();
        music.dispose();
    }
}
