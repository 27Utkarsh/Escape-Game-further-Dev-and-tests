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

public class LoseScreen implements Screen {
    Texture backgroundTexture;
    SpriteBatch batch;
    Game game;
    Stage stage;
    Skin skin;
    Music music;

    public LoseScreen(Game game) {
        this.game = game;
    }
    @Override
    public void show() {
        batch = new SpriteBatch();
        backgroundTexture = new Texture("Game_Over_image.png");
        stage = new Stage(new ScreenViewport());
        Gdx.input.setInputProcessor(stage);

        music = Gdx.audio.newMusic(Gdx.files.internal("Lose.ogg"));
        music.setLooping(true);
        music.setVolume(0.5f);
        music.play();

        skin = new Skin(Gdx.files.internal("uiskin.json"));

        TextButton againButton = new TextButton("Try Again", skin);
        againButton.setPosition(20,20);
        againButton.setSize(150, 40);

        againButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                music.stop();
                game.setScreen(new MenuScreen(game));
            }
        });

        stage.addActor(againButton);
    }

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

    @Override
    public void dispose() {
        batch.dispose();
        backgroundTexture.dispose();
        stage.dispose();
        skin.dispose();
    }
}
