package com.badlogic.debugthugs;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;

public class Key {
    public Sprite keySprite;
    public Rectangle bounds;
    public boolean collected = false;
    Texture keyTexture;

    public Key(Texture texture, float x, float y) {
        keySprite = new Sprite(texture);
        keySprite.setPosition(x, y);
        keySprite.setSize(32,32);
        bounds = new Rectangle(x,y,32,32);
    }

    public void render(SpriteBatch sb) {
        if (collected == false) {
            keySprite.draw(sb);
        }
    }

    public void checkCollected(Player player) {
        if (collected == false && bounds.overlaps(new Rectangle(player.playerX, player.playerY, player.playerWidth, player.playerHeight))) {
            collected = true;
        }
    }
}
