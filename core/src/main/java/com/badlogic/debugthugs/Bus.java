package com.badlogic.debugthugs;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;

public class Bus {
    public Sprite sprite;
    public Rectangle bounds;

    public Bus(Texture texture, float x, float y) {
        sprite = new Sprite(texture);
        sprite.setPosition(x, y);
        sprite.setSize(32, 32);
        bounds = new Rectangle(x, y, 32, 32);
    }

    public void render(SpriteBatch sb) {
        sprite.draw(sb);
    }

    /**
     * Create a Bus instance for testing.
     * 
     * Doesn't initialise the sprite so that doesn't interfere with tests.
     * @param x The x position of the bus (bottom left).
     * @param y The y position of the bus (bottom left).
     */
    public Bus(float x, float y)
    {
        bounds = new Rectangle(x, y, 32, 32);
    }
}
