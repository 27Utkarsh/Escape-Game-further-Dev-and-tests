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

    public Coin(Texture texture, float x, float y) {
        sprite = new Sprite(texture);
        sprite.setPosition(x, y);
        sprite.setSize(32, 32);
        bounds = new Rectangle(x, y, 32, 32);
    }

    public void render(SpriteBatch batch) {
        if (!collected) sprite.draw(batch);
    }

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
}
