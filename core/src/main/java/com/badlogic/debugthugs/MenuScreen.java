package com.badlogic.debugthugs;

import com.badlogic.gdx.*;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.ScreenViewport;

public class MenuScreen extends ScreenAdapter implements Screen {
    FitViewport viewport = new FitViewport(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
    Stage stage;
    Game game;
    SpriteBatch batch;
    BitmapFont font;
    Skin buttonSkin;
    Texture background;
    Music music;

    public MenuScreen(Game game) {
        this.game = game;
    }

    @Override
    public void show() {
        stage = new Stage(new ScreenViewport());
        Gdx.input.setInputProcessor(stage);
        background = new Texture("Menu.png");

        music = Gdx.audio.newMusic(Gdx.files.internal("Menu_music.ogg"));
        music.setLooping(true);
        music.setVolume(SettingsScreen.getNoise());
        music.play();

        batch = new SpriteBatch();
        font = new BitmapFont();
        font.getData().setScale(2f);
        font.setColor(Color.WHITE);

        buttonSkin = new Skin(Gdx.files.internal("uiskin.json"));
        TextButton startButton = new TextButton("Start", buttonSkin);
        TextButton settingsButton = new TextButton("Settings", buttonSkin);
        startButton.setPosition(20,300);
        startButton.setSize(200,60);
        settingsButton.setPosition(20,235);
        settingsButton.setSize(200,60);

        startButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                music.stop();
                game.setScreen(new FirstScreen(game));
            }
        });

        settingsButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                music.stop();
                game.setScreen(new SettingsScreen(game));
            }
        });

        stage.addActor(startButton);
        stage.addActor(settingsButton);
    }
    @Override
    public void render(float delta) {
        ScreenUtils.clear(Color.DARK_GRAY);
        viewport.apply();
        batch.setProjectionMatrix(viewport.getCamera().combined);

        batch.begin();
        batch.draw(background, 0, 0, viewport.getWorldWidth(), viewport.getWorldHeight());
        font.draw(batch, "University Escape [WIP Title]", 20, 400);
        batch.end();

        stage.act(delta);
        stage.draw();

        if (Gdx.input.isKeyJustPressed(Input.Keys.SPACE)) {
            game.setScreen(new FirstScreen(game));
        }
    }

    @Override
    public void resize(int width, int height) {
        viewport.update(width, height, true);
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
        font.dispose();
        stage.dispose();
        buttonSkin.dispose();
        music.dispose();
    }
}
