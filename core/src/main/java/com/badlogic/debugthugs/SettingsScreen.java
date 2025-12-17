package com.badlogic.debugthugs;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Slider;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.ScreenUtils;

public class SettingsScreen implements Screen {
    float initialVolume = 0.5f;
    static float volume;
    Main game;
    Stage stage;
    Skin skin;

    /**
     * Creates a SettingsScreen instance.
     * @param game Reference to the main game class to allow screen switching.
     */
    public SettingsScreen(Main game) {
        this.game = game;
    }

    /**
     * the volume level is set to the default value of 0.5f if it has not been changed
     * @return the volume level
     */
    public static float getNoise() {
        if (volume == 0) {
            return 0.5f;
        }
        else {
            return volume;
        }
    }

    public void setNoise(float volume) {
        this.volume = volume;
    }

    /**
     * Initializes the settings screen
     * Creates UI elements including volume slider and menu button
     */
    @Override
    public void show() {
        stage = new Stage(game.uiViewport);
        Gdx.input.setInputProcessor(stage);
        skin = new Skin(Gdx.files.internal("uiskin.json"));

        Label volumeLabel = new Label("Volume:", skin);
        final Slider volumeSlider = new Slider(0.01f, 1f, 0.01f, false, skin);
        if (volume == 0) {
            volumeSlider.setValue(initialVolume);
        }
        else {
            volumeSlider.setValue(volume);
        }

        volumeSlider.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                setNoise(volumeSlider.getValue());
            }
        });

        skin = new Skin(Gdx.files.internal("uiskin.json"));

        TextButton menuButton = new TextButton("Back to Menu", skin);
        menuButton.setPosition(20,20);
        menuButton.setSize(150, 40);

        menuButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                game.setScreen(new MenuScreen(game));
            }
        });

        stage.addActor(menuButton);

        Table table = new Table();
        table.setFillParent(true);
        table.center();
        table.add(volumeLabel).padBottom(10).row();
        table.add(volumeSlider).width(200);
        stage.addActor(table);
    }

    /**
     * Renders the screen every frame - clears screen and draws UI elements
     * @param delta The time in seconds since the last render.
     */
    @Override
    public void render(float delta) {
        ScreenUtils.clear(0, 0, 0, 1);
        game.batch.setProjectionMatrix(game.uiCamera.combined);
        stage.act(delta);
        stage.draw();
    }

    @Override
    public void resize(int i, int i1) {

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
        stage.dispose();
        skin.dispose();
    }
}
