package com.badlogic.debugthugs;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;

public class HelperCharacter {
    public Sprite sprite;
    public Rectangle bounds;
    public boolean collected = false;

    public HelperCharacter(Texture texture, float x, float y) {
        sprite = new Sprite(texture);
        sprite.setPosition(x, y);
        sprite.setSize(32, 32);          // same size as other events
        bounds = new Rectangle(x, y, 32, 32);
    }

    public void render(SpriteBatch batch) {
        if (!collected) sprite.draw(batch);
    }

    public void checkCollected(Player player, FirstScreen screen) {
        Rectangle playerRect = new Rectangle(player.playerX, player.playerY, player.playerWidth, player.playerHeight);

        if (!collected && bounds.overlaps(playerRect)) {
            collected = true;

            // Increment good events
            player.goodEvent += 1;

            // Add 20 seconds to timer
            screen.timePassed += 20f;

            // Unlock achievement
            AchievementManager.get().unlock("HELPER_FOUND");
        }
    }
}

