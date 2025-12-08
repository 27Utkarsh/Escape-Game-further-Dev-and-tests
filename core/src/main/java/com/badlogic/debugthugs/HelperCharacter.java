package com.badlogic.debugthugs;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;

/**
 * A helper character that grants +20 seconds when found and displays a pop-up message
 * using the AchievementManager system.
 */
public class HelperCharacter {

    public Sprite sprite;
    public Rectangle bounds;
    public boolean collected = false;
    private final float bonusTime = 20f; // Time bonus in seconds

    /**
     * Create a helper character at position (x, y) with the given texture.
     */
    public HelperCharacter(Texture texture, float x, float y) {
        sprite = new Sprite(texture);
        sprite.setSize(32, 32);
        sprite.setPosition(x, y);

        bounds = new Rectangle(x, y, 32, 32);
    }

    /**
     * Draw the helper character if it has not yet been collected.
     */
    public void render(SpriteBatch batch) {
        if (!collected) sprite.draw(batch);
    }

    /**
     * Check if the player overlaps with the helper character.
     * If collected, adds +20 seconds to the game's timer and shows a pop-up.
     *
     * @param player The player instance
     * @param screen The current FirstScreen instance (needed to modify timePassed)
     */
    public void checkCollected(Player player, FirstScreen screen) {
        if (!collected && bounds.overlaps(new Rectangle(
            player.playerX, player.playerY, player.playerWidth, player.playerHeight))) {

            // Add 20 seconds to the timer
            screen.timePassed += bonusTime;
            collected = true;

            // Unlock achievement to show pop-up
            AchievementManager.get().unlock("HELPER_FOUND");
        }
    }
}
