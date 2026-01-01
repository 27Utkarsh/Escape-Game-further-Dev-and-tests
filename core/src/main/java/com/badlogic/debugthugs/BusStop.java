package com.badlogic.debugthugs;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;

public class BusStop {
    public Sprite busStopSprite;
    public Rectangle bounds;
    public boolean used = false;
    public String name;

    public BusStop(Texture stopTexture, float x, float y, String name) {
        busStopSprite = new Sprite(stopTexture);
        busStopSprite.setPosition(x, y);
        busStopSprite.setSize(32, 32);

        bounds = new Rectangle(x, y, 32, 32);
        this.name = name;
    }

    public void render(SpriteBatch sb) {
        if (!used) {
            busStopSprite.draw(sb);
        }
    }

    /**
     * Create a BusStop instance for testing.
     * 
     * Doesn't initialise the sprite so that doesn't interfere with tests.
     * 
     * @param used Initial value for whether the BusStop has been used.
     */
    public BusStop(boolean used, String name) {
        this.bounds = new Rectangle(608, 512, 32, 32);
        this.used = used;
        this.name = name;
    }
}
