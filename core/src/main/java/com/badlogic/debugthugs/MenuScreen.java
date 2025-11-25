package com.badlogic.debugthugs;

import com.badlogic.gdx.*;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.FitViewport;
import org.w3c.dom.Text;

public class MenuScreen extends ScreenAdapter {
    private static final int VIEWPORT_WIDTH = 1280;
    private static final int VIEWPORT_HEIGHT = 720;

    Game game;
    FitViewport viewport;
    SpriteBatch batch;
    Stage stage;

    BitmapFont font;
    Skin skin;
    Texture background;
    Music music;

    /**
     * Creates the main menu screen.
     * @param game reference to the main game instance for screen switching
     */
    public MenuScreen(Game game) {
        this.game = game;
    }
    /**
     * Called when the main menu screen becomes the current screen (ie when the player first starts playing)
     * handles buttons, background image, music, and input processing (player needs to interact with buttons)
     */
    @Override
    public void show() {
        viewport = new FitViewport(VIEWPORT_WIDTH, VIEWPORT_HEIGHT);
        stage = new Stage(viewport);
        Gdx.input.setInputProcessor(stage);

        background = new Texture("Menu.png");

        music = Gdx.audio.newMusic(Gdx.files.internal("Menu_music.ogg"));
        music.setLooping(true);
        music.setVolume(SettingsScreen.getNoise());
        music.play();

        batch = new SpriteBatch();
        font = new BitmapFont();
        font.getData().setScale(2f);

        skin = new Skin(Gdx.files.internal("uiskin.json"));

        Table rootTable = new Table();
        rootTable.setFillParent(true);
        stage.addActor(rootTable);

        Label.LabelStyle titleStyle = new Label.LabelStyle(font, Color.WHITE);
        Label titleLabel = new Label("University Escape [WIP Title]", titleStyle);

        TextButton startButton = new TextButton("Start", skin);
        TextButton settingsButton = new TextButton("Settings", skin);
        TextButton leaderBoardButton = new TextButton("Leaderboard", skin);

        rootTable.add(titleLabel).padBottom(80f);
        rootTable.row();
        rootTable.add(startButton).width(300).height(80).padBottom(40);
        rootTable.row();
        rootTable.add(settingsButton).width(300).height(80).padBottom(40);
        rootTable.row();
        rootTable.add(leaderBoardButton).width(300).height(80);


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

        leaderBoardButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                music.stop();
                game.setScreen(new LeaderBoardScreen(game));
            }
        });
    }

    /**
     * Renders the menu screen each frame
     * @param delta The time in seconds since the last render.
     */
    @Override
    public void render(float delta) {
        ScreenUtils.clear(Color.DARK_GRAY);
        viewport.apply();
        batch.setProjectionMatrix(viewport.getCamera().combined);

        batch.begin();
        batch.draw(background, 0, 0, viewport.getWorldWidth(), viewport.getWorldHeight());
        batch.end();

        stage.act(delta);
        stage.draw();

        if (Gdx.input.isKeyJustPressed(Input.Keys.SPACE)) {
            game.setScreen(new FirstScreen(game));
        }
    }
    /**
     * Updates the viewport and stage sizes when the screen changes size.
     * @param width  new width of the screen
     * @param height new height of the screen
     */
    @Override
    public void resize(int width, int height) {
        viewport.update(width, height, true);
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
        batch.dispose();
        font.dispose();
        stage.dispose();
        background.dispose();
        skin.dispose();
        music.dispose();
    }
}
