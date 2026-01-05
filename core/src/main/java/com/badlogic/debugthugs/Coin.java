package com.badlogic.debugthugs;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;

public class Coin {
    public Sprite sprite;
    public Rectangle bounds;
    public boolean collected = false;
    public float bonusPoints = 0f; // Points added to score

    /**
     * Normal constructor for the game (uses a texture)
     */
    public Coin(Texture texture, float x, float y) {
        sprite = new Sprite(texture);
        sprite.setPosition(x, y);
        sprite.setSize(32, 32);
        bounds = new Rectangle(x, y, 32, 32);
    }

    /**
     * Headless constructor for unit testing (no texture needed)
     */
    public Coin(float x, float y, float bonusPoints) {
        this.sprite = null;                 // no sprite needed
        this.bounds = new Rectangle(x, y, 32, 32);
        this.collected = false;
        this.bonusPoints = bonusPoints;
    }

    /**
     * Draws the coin (game only)
     */
    public void render(SpriteBatch batch) {
        if (!collected && sprite != null) {
            sprite.draw(batch);
        }
    }

    /**
     * Checks if the player collected the coin (game only)
     */
    public void checkCollected(Player player, FirstScreen screen) {
        if (!collected && bounds.overlaps(new Rectangle(
            player.playerX, player.playerY, player.playerWidth, player.playerHeight))) {

            collected = true;

            // Increase score ONCE
            screen.playerScore += bonusPoints;

            // Increment positive events
            player.goodEvent += 1;

            // Unlock achievement
            AchievementManager.get().unlock("COIN_COLLECTED");
        }
    }

    /**
     * Marks coin as collected (can be used for tests)
     */
    public void collect() {
        collected = true;
    }

    /**
     * Returns whether the coin is collected
     */
    public boolean isCollected() {
        return collected;
    }
}
