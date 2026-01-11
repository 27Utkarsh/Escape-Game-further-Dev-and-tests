package com.badlogic.debugthugs;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.ScreenUtils;

/**
 * This class displays the win screen once a player has successfully escaped, it
 * has
 * another functionality, where before the player is able to exit to the menu
 * they must first
 * enter their name. Upon exiting to the menu their name and score will then be
 * saved to a
 * preference file so that it can later be accessed for the leaderboard.
 */
public class WinScreen implements Screen {
    Texture backgroundTexture;
    Main game;
    Stage stage;
    Skin skin;
    Music music;
    Preferences prefs;
    float finalTime;
    float score;

    public WinScreen(Main game, float finalTime) {
        this.game = game;
        this.finalTime = finalTime;
    }

    /**
     * This function adds an entry to the GameScore preferences file in order to be
     * later used in the leaderboard.
     * 
     * @param userName of the player for this run.
     * @param score    of the player obtained in this run.
     */
    public void addScore(String userName, Float score) {
        prefs = Gdx.app.getPreferences("GameScores");
        float oldScore = prefs.getFloat(userName);
        if (oldScore < score) {
            prefs.putFloat(userName, score);
            prefs.flush();
        }
    }

    /**
     * Calculates the overall player score
     *
     * @return final score as a float
     */
    public float calcScore() {
        score = this.finalTime * 4f;
        return score;
    }

    @Override
    public void show() {
        music = Gdx.audio.newMusic(Gdx.files.internal("lose.ogg"));
        music.setLooping(true);
        music.setVolume(SettingsScreen.getNoise());
        music.play();

        score = calcScore();

        stage = new Stage(game.uiViewport);
        Gdx.input.setInputProcessor(stage);

        backgroundTexture = new Texture("Win.png");
        skin = new Skin(Gdx.files.internal("uiskin.json"));

        Table root = new Table();
        root.setFillParent(true);
        root.setBackground(new TextureRegionDrawable(backgroundTexture));
        stage.addActor(root);
        root.align(Align.left | Align.center);
        root.padLeft(40);

        Label scoreLabel = new Label("Score: " + (int) score, skin);
        scoreLabel.setAlignment(Align.center);

        TextField userInput = new TextField("", skin);
        userInput.setMessageText("Enter your name (1-8 characters):");
        userInput.setMaxLength(8);

        TextButton againButton = new TextButton("Save And Exit", skin);
        againButton.setDisabled(true);

        userInput.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                String userName = userInput.getText();
                againButton.setDisabled(userName.isEmpty());
            }
        });

        againButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                String userName = userInput.getText();
                if (!userName.isEmpty()) {
                    addScore(userName, score);
                    music.stop();
                    game.setScreen(new MenuScreen(game));
                }
            }
        });

        root.defaults().pad(30);
        root.add(scoreLabel);
        root.row();
        root.add(userInput).width(400).height(60);
        root.row();
        root.add(againButton).width(300).height(70);
    }

    @Override
    public void render(float delta) {
        ScreenUtils.clear(0, 0, 0, 1);
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

    @Override
    public void dispose() {
        backgroundTexture.dispose();
        stage.dispose();
        skin.dispose();
        music.dispose();
    }
}
