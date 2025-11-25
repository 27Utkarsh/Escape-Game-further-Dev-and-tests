package com.badlogic.debugthugs;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.ScreenViewport;

/**
 * The WinScreen class is displayed when the player gets to the end of the maze
 * shows a victory image, plays music, and provides a button to return to the main menu to try again
 * Also now updated to save user data for the leaderboard rankings
 */
public class WinScreen implements Screen {
    FitViewport viewport;
    Texture backgroundTexture;
    SpriteBatch batch;
    Game game;
    Stage stage;
    Skin skin;
    Music music;
    BitmapFont font;
    Preferences prefs;
    float finalTime;
    float score;
    AchievementManager achievementManager;

    /**
     * Creates a WinScreen instance.
     * @param game Reference to the main game class to allow screen switching.
     */
    public WinScreen(Game game, float finalTime) {
        this.game = game;
        this.finalTime = finalTime;
    }

    /**
     * Calculates the overall player score
     * @param time passed as final time to factor into score
     * @return final score as a float
     */
    public float calcScore(float time) {
        score = time * 4f;
        return score;
    }

    /**
     * Called when the screen becomes visible
     * Initializes buttons, loads textures (background image) and music
     */
    @Override
    public void show() {
        batch = new SpriteBatch();
        backgroundTexture = new Texture("Win.png");
        font = new BitmapFont();
        font.getData().setScale(4f);
        font.setColor(Color.WHITE);
        score = calcScore(finalTime);
        stage = new Stage(new ScreenViewport());
        Gdx.input.setInputProcessor(stage);

        music = Gdx.audio.newMusic(Gdx.files.internal("Lose.ogg"));
        music.setLooping(true);
        music.setVolume(SettingsScreen.getNoise());
        music.play();

        skin = new Skin(Gdx.files.internal("uiskin.json"));
        viewport = new FitViewport(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());

        TextField userInput = new TextField("", skin);
        userInput.setMessageText("Enter your name (1-8 characters):");
        userInput.setMaxLength(8);
        userInput.setPosition(20, 400);
        userInput.setSize(400, 100);

        TextButton againButton = new TextButton("Try Again", skin);
        againButton.setPosition(20,20);
        againButton.setSize(150, 40);
        againButton.setDisabled(true);

        userInput.addListener(new ChangeListener() {
            @Override
            /**
             * Sets the status of whether the exit button is enabled or not
             * depending if the text in the bar is empty or not
             */
            public void changed(ChangeEvent event, Actor actor) {
                String userName =  userInput.getText();
                againButton.setDisabled(userName.isEmpty());
            }
        });

        againButton.addListener(new ClickListener() {
            @Override
            /**
             * Handles when the player clicks the "Play Again" button.
             * If user has entered text into the bar then the username and score
             * is loaded into a preference file
             * Then stops music and returns to the menu screen if button is clicked
             */
            public void clicked(InputEvent event, float x, float y) {
                String userName =  userInput.getText();
                if (!userName.isEmpty()) {
                    prefs = Gdx.app.getPreferences("GameScores");
                    float oldScore = prefs.getFloat(userName);
                    //Makes sure previous highscore isn't overwritten
                    if (oldScore < score) {
                        prefs.putFloat(userName, score);
                        prefs.flush();
                    }
                    music.stop();
                    game.setScreen(new MenuScreen(game));
                }
            }
        });

        stage.addActor(againButton);
        stage.addActor(userInput);

        achievementManager = AchievementManager.get();
    }
    /**
     * Called every frame to render the screen
     * @param delta The time in seconds since the last render.
     */
    @Override
    public void render(float delta) {
        ScreenUtils.clear(Color.RED);
        viewport.apply();

        batch.begin();
        batch.draw(backgroundTexture,0,0, Gdx.graphics.getWidth(),Gdx.graphics.getHeight());
        font.draw(batch, "Score: " + score, 20, 600);
        batch.end();

        stage.act(delta);
        stage.draw();
        
        achievementManager.update(delta);
        achievementManager.render(batch, viewport.getScreenWidth() / 2, viewport.getScreenHeight() * 0.3f);
    }

    @Override
    public void resize(int width, int height) {
        viewport.update(width, height);
        stage.getViewport().update(width, height, true);
        batch.getProjectionMatrix().setToOrtho2D(0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
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
        font.dispose();
    }
}
