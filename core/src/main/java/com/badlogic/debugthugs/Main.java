package com.badlogic.debugthugs;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.viewport.FitViewport;

/**
 * {@link com.badlogic.gdx.ApplicationListener} implementation shared by all platforms.
 */
public class Main extends Game {
    public SpriteBatch batch;
    public BitmapFont font;
    public OrthographicCamera worldCamera;
    public FitViewport worldViewport;
    public OrthographicCamera uiCamera;
    public FitViewport uiViewport;

    @Override
    public void create() {
        batch = new SpriteBatch();

        worldCamera = new OrthographicCamera();
        worldViewport = new FitViewport(800, 600, worldCamera);

        uiCamera = new OrthographicCamera();
        uiViewport = new FitViewport(1280, 720, uiCamera);

        font = new BitmapFont();
        setScreen(new MenuScreen(this));
    }

    /**
     * Updates the viewport and stage sizes when the screen changes size.
     *
     * @param width  new width of the screen
     * @param height new height of the screen
     */
    @Override
    public void resize(int width, int height) {
        worldViewport.update(width, height);
        uiViewport.update(width, height);
    }

    @Override
    public void dispose() {
        batch.dispose();
        font.dispose();
    }
}
