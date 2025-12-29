package com.badlogic.debugthugs;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.ScreenUtils;

/**
 * This class handles the leaderboard screen and reads from the preference
 * file, written to in WinScreen to obtain usernames and scores to display
 * Extends ScreenAdapter as a convenience implementation of screen
 */
public class LeaderBoardScreen extends ScreenAdapter {
    Main game;
    Stage stage;
    Skin skin;
    public Preferences prefs;
    HashMap<String, Float> returnScores;
    public String[] topNames = new String[5];
    public Float[] topScores =  new Float[5];

    /**
     * Creates a new leaderboard instance for the game
     * @param game the main LibGDX Game object used to manage screens and shared resources
     */
    public LeaderBoardScreen(Main game) { this.game = game; }

    /**
     * Creates the objects to be displayed on the screen once it has been initiated, and
     * controls the data handling functions created further below
     * Uses a table system consisting of text, labels and a button to neatly display objects
     */
    @Override
    public void show() {
        stage = new Stage(game.uiViewport);
        Gdx.input.setInputProcessor(stage);

        game.font.getData().setScale(2f);

        //Calls the preference file and manages the data handlers
        prefs = Gdx.app.getPreferences("GameScores");
        returnScores = getScores();
        sortScores(returnScores);

        skin = new Skin(Gdx.files.internal("uiskin.json"));

        Table table = new Table();
        table.setFillParent(true);
        stage.addActor(table);

        //Sets labels with the array data containing player names and scores
        Label.LabelStyle labelStyle = new Label.LabelStyle(game.font, Color.WHITE);
        Label l0 = new Label("Top 5 players score: ", labelStyle);
        Label l1 = new Label("1: " + topNames[0] + ": " +  topScores[0], labelStyle);
        Label l2 = new Label("2: " + topNames[1] + ": " + topScores[1], labelStyle);
        Label l3 = new Label("3: " + topNames[2] + ": " + topScores[2], labelStyle);
        Label l4 = new Label("4: " + topNames[3] + ": " + topScores[3], labelStyle);
        Label l5 = new Label("5: " + topNames[4] + ": " + topScores[4], labelStyle);
        TextButton exit = new TextButton("Exit", skin);

        table.add(l0).padBottom(40f);
        table.row();
        table.add(l1).padBottom(40f);
        table.row();
        table.add(l2).padBottom(40f);
        table.row();
        table.add(l3).padBottom(40f);
        table.row();
        table.add(l4).padBottom(40f);
        table.row();
        table.add(l5).padBottom(40f);
        table.row();
        table.add(exit).width(150).height(40);
        table.row();

        exit.addListener(new ClickListener() {
            /**
             * Handles the player clicking the "exit" button
             * Loads the main menu
             */
            @Override
            public void clicked(InputEvent event, float x, float y) {
                game.setScreen(new MenuScreen(game));
            }
        });

    }

    /**
     * A function that obtains the data written to the preference file
     * @return a hashmap of String (player) and Float (score), this is done in order to map
     * the names of the current player to their respective score
     */
    public HashMap<String, Float> getScores() {
        HashMap<String, Float> returnScores = new HashMap<>();

        //Loop through the preference file and map each key and value to hashmap
        //Values from preference file are also returned as an object so is converted to a float
        for (Map.Entry<String, ?> entry : prefs.get().entrySet()) {
            String key = entry.getKey();
            Object value = entry.getValue();

            float score;
            if (value instanceof Float) {
                score = (Float) value;
            } else if (value instanceof String) {
                score = Float.parseFloat((String) value);
            } else {
                continue;
            }
            returnScores.put(key, score);
        }

        return returnScores;
    }

    /**
     * Used to take the output from getScores() and store as a 2D ArrayList, then sort the scores
     * from the highest score. These can then be passed into the arrays topNames and topScores to
     * retain the mapping from player name to score.
     * @param scores output from getScores()
     */
    public void sortScores(HashMap<String, Float> scores) {
        List<Map.Entry<String, Float>> scoresList = new ArrayList<>(scores.entrySet());

        //Sorts scores from highest
        scoresList.sort((a, b) -> Float.compare(b.getValue(), a.getValue()));

        for (int i = 0; i < 5; i++) {
            if (i < scoresList.size()) {
                topNames[i] = scoresList.get(i).getKey();
                topScores[i] = scoresList.get(i).getValue();
            //Else to fill array if it is < 5
            } else {
                topNames[i] = "No Score Yet";
                topScores[i] = 0f;
            }
        }
    }

    /**
     * Called every frame to render the screen
     * @param delta The time in seconds since the last render.
     */
    @Override
    public void render(float delta) {
        ScreenUtils.clear(0, 0, 0, 1);
        game.uiViewport.apply();
        game.batch.setProjectionMatrix(game.uiCamera.combined);

        stage.act(delta);
        stage.draw();
    }

    @Override
    public void resize(int width, int height) {
    }

    /**
     * Releases assets and resources used by this screen.
     * helps free memory
     */
    @Override
    public void dispose() {
        stage.dispose();
        skin.dispose();
    }
}
