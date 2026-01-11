package com.badlogic.debugthugs;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ScreenAdapter;
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

public class Tutorial extends ScreenAdapter {

    final Main game;
    Stage stage;
    Texture background;
    Skin skin;

    public Tutorial(Main game) {
        this.game = game;
    }

    @Override
    public void show() {
        stage = new Stage(game.uiViewport);
        Gdx.input.setInputProcessor(stage);

        background = new Texture("Menu.png");
        skin = new Skin(Gdx.files.internal("uiskin.json"));

        Table table = new Table();
        table.setFillParent(true);
        stage.addActor(table);

        Table contentTable = new Table();
        contentTable.setBackground(skin.get(TextButton.TextButtonStyle.class).up);
        contentTable.pad(20);

        Label.LabelStyle labelStyle = new Label.LabelStyle(game.font, Color.WHITE);
        Label.LabelStyle headingStyle = new Label.LabelStyle(game.font, Color.WHITE);

        Label headingLabel = new Label("TUTORIAL", headingStyle);

        String[][] instructions = {
                { "Interact", "E" },
                { "Move UP", "UP,W" },
                { "Move DOWN", "DOWN,S" },
                { "Move RIGHT", "RIGHT,D" },
                { "Move LEFT", "LEFT,A" }
        };

        for (String[] instruction : instructions) {
            Label actionLabel = new Label(instruction[0], labelStyle);
            Label keyLabel = new Label(instruction[1], labelStyle);

            contentTable.add(actionLabel).left().padRight(20);
            contentTable.add(keyLabel).left();
            contentTable.row().padTop(10);
        }

        TextButton backButton = new TextButton("Go Back", skin);

        // Layout Main Table
        table.add(headingLabel).padBottom(30);
        table.row();
        table.add(contentTable).padBottom(30);
        table.row();
        table.add(backButton).width(200).height(50);

        backButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                returnToMenu();
            }
        });
    }

    /**
     * Navigates back to the main menu.
     * Public for testing purposes.
     */
    public void returnToMenu() {
        game.setScreen(new MenuScreen(game));
        dispose();
    }

    @Override
    public void render(float delta) {
        ScreenUtils.clear(Color.DARK_GRAY);

        game.batch.setProjectionMatrix(game.uiCamera.combined);
        game.batch.begin();
        game.batch.draw(background, 0, 0, game.uiViewport.getWorldWidth(), game.uiViewport.getWorldHeight());
        game.batch.end();

        stage.act(delta);
        stage.draw();
    }

    @Override
    public void dispose() {
        if (stage != null)
            stage.dispose();
        if (background != null)
            background.dispose();
        if (skin != null)
            skin.dispose();
    }
}
