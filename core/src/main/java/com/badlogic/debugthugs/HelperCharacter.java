package com.badlogic.debugthugs;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;

/**
 * A helper character that grants +20 seconds when found, increases positive events,
 * and displays a pop-up message using the AchievementManager system.
 */
public class HelperCharacter {

    public Sprite sprite;
    public Rectangle bounds;
    public boolean collected = false;
    private final float bonusTime = 20f; // Time bonus in seconds

    public HelperCharacter(Texture texture, float x, float y) {
        sprite = new Sprite(texture);
        sprite.setSize(32, 32);
        sprite.setPosition(x, y);

        bounds = new Rectangle(x, y, 32, 32);
    }

    public void render(SpriteBatch batch) {
        if (!collected) sprite.draw(batch);
    }

    public void checkCollected(Player player, FirstScreen screen) {
        if (!collected && bounds.overlaps(new Rectangle(
            player.playerX, player.playerY, player.playerWidth, player.playerHeight))) {

            // Add 20 seconds to the timer
            screen.timePassed += bonusTime;

            // Increment positive events
            player.goodEvent += 1;

            collected = true;

            // Unlock achievement to show pop-up
            AchievementManager.get().unlock("HELPER_FOUND");

            // Optional: show a custom pop-up in addition to the achievement
            AchievementManager.get().showPopup("Helper Found! +20 seconds");
        }
    }
}
