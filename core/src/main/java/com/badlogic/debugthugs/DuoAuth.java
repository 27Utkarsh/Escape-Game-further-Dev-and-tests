package com.badlogic.debugthugs;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;

public class DuoAuth {
    public Sprite duoSprite;
    public Rectangle bounds;
    public boolean triggered = false;
    public boolean active = false;
    public float timer = 0f;

    private boolean testMode = false;

    public DuoAuth(Texture texture, float x, float y) {
        duoSprite = new Sprite(texture);
        duoSprite.setPosition(x, y);
        duoSprite.setSize(32, 32);
        bounds = new Rectangle(x, y, 32, 32);
    }

    public void render(SpriteBatch sb) {
        if (!triggered) {
            duoSprite.draw(sb);
        }
    }

    public void checkTriggered(Player player) {
        if (testMode) return;

        if (!triggered && bounds
                .overlaps(new Rectangle(player.playerX, player.playerY, player.playerWidth, player.playerHeight))) {
            player.badEvent += 1;
            triggered = true;
            active = true;
            timer = 10f;
        }
    }

    public void update(float delta) {
        if (active) {
            timer -= delta;
            if (timer <= 0) {
                active = false;
                AchievementManager.get().unlock("DUO_AUTHENTICATED");
            }
        }
    }

    /**
     * Create a DuoAuth instance for testing.
     * 
     * Doesn't initialise the sprite so that doesn't interfere with tests.
     * 
     * @param triggered Initial value for triggered.
     * @param active Initial value for active.
     */
    public DuoAuth(boolean triggered, boolean active)
    {
        testMode = true;
        this.triggered = triggered;
        this.active = active;
    }
}
