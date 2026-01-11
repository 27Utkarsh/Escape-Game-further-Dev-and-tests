package com.badlogic.debugthugs;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.ScreenUtils;

public class MenuScreen extends ScreenAdapter {
    // private static final int VIEWPORT_WIDTH = 1280;
    // private static final int VIEWPORT_HEIGHT = 720;

    Main game;
    // FitViewport viewport;
    // SpriteBatch batch;
    Stage stage;

    // BitmapFont font;
    Skin skin;
    Texture background;
    Music music;

    /**
     * Creates the main menu screen.
     * 
     * @param game reference to the main game instance for screen switching
     */
    public MenuScreen(Main game) {
        this.game = game;
    }

    /**
     * Called when the main menu screen becomes the current screen (ie when the player first starts playing)
     * handles buttons, background image, music, and input processing (player needs to interact with buttons)
     */
    @Override
    public void show() {
        stage = new Stage(game.uiViewport);
        Gdx.input.setInputProcessor(stage);

        background = new Texture("Menu.png");

        music = Gdx.audio.newMusic(Gdx.files.internal("Menu_music.ogg"));
        music.setLooping(true);
        music.setVolume(SettingsScreen.getNoise());
        music.play();

        game.font.getData().setScale(2f);
        game.font.setColor(Color.WHITE);

        skin = new Skin(Gdx.files.internal("uiskin.json"));

        Table rootTable = new Table();
        rootTable.setFillParent(true);
        stage.addActor(rootTable);

        Label.LabelStyle titleStyle = new Label.LabelStyle(game.font, Color.WHITE);
        Label titleLabel = new Label("University Escape", titleStyle);
        rootTable.add(titleLabel).padBottom(80f);
        rootTable.row();

        TextButton startButton = new TextButton("Start", skin);
        rootTable.add(startButton).width(300).height(80).padBottom(40);
        rootTable.row();

        TextButton tutorialButton = new TextButton("Tutorial", skin);
        rootTable.add(tutorialButton).width(300).height(80).padBottom(40);
        rootTable.row();

        TextButton settingsButton = new TextButton("Settings", skin);
        rootTable.add(settingsButton).width(300).height(80).padBottom(40);
        rootTable.row();

        TextButton leaderBoardButton = new TextButton("Leaderboard", skin);
        rootTable.add(leaderBoardButton).width(300).height(80);

        startButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                music.stop();
                game.setScreen(new FirstScreen(game));
            }
        });

        tutorialButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                music.stop();
                game.setScreen(new Tutorial(game));
            }
        });

        settingsButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                music.stop();
                game.setScreen(new SettingsScreen(game));
            }
        });

        leaderBoardButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                music.stop();
                game.setScreen(new LeaderBoardScreen(game));
            }
        });
    }

    /**
     * Navigates to the tutorial screen.
     * Public for testing purposes.
     */
    public void goToTutorial() {
        music.stop();
        game.setScreen(new Tutorial(game));
    }

    /**
     * Renders the menu screen each frame
     * 
     * @param delta The time in seconds since the last render.
     */
    @Override
    public void render(float delta) {
        ScreenUtils.clear(Color.DARK_GRAY);
        // game.uiViewport.apply();
        game.batch.setProjectionMatrix(game.uiCamera.combined);

        game.batch.begin();
        game.batch.draw(background, 0, 0, game.uiViewport.getWorldWidth(), game.uiViewport.getWorldHeight());
        game.batch.end();

        stage.act(delta);
        stage.draw();

        if (Gdx.input.isKeyJustPressed(Input.Keys.SPACE)) {
            game.setScreen(new FirstScreen(game));
        }
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
     * Releases assets and resources used by this screen helps free memory
     */
    @Override
    public void dispose() {
        stage.dispose();
        background.dispose();
        skin.dispose();
        music.dispose();
    }
}
