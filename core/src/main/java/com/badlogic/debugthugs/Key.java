package com.badlogic.debugthugs;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;

public class Key {
    public Sprite keySprite;
    public Rectangle bounds;
    public boolean collected = false;

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
            player.hiddenEvent += 1;
            collected = true;
            AchievementManager.get().unlock("FOUND_KEY");
        }
    }

    /**
     * Create a Key instance for testing.
     * 
     * Doesn't initialise the sprite so that doesn't interfere with tests.
     * 
     * @param collected Initial value for whether the key has been collected.
     */
    public Key(boolean collected)
    {
        this.collected = collected;
    }
}
