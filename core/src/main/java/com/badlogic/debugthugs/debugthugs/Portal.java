package com.badlogic.debugthugs;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;

public class Portal {
    public Sprite portalSprite;
    public Rectangle bounds;
    public boolean used = false;

    public Portal(Texture texture, float x, float y) {
        portalSprite = new Sprite(texture);
        portalSprite.setPosition(x, y);
        portalSprite.setSize(32, 32);
        bounds = new Rectangle(x, y, 32, 32);
    }

    public void render(SpriteBatch sb) {
        if (!used) {
            portalSprite.draw(sb);
        }
    }
}
