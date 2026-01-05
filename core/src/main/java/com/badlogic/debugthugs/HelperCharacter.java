package com.badlogic.debugthugs;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;

public class HelperCharacter {
    public Sprite sprite;
    public Rectangle bounds;
    public boolean collected = false;

    // Normal game constructor
    public HelperCharacter(Texture texture, float x, float y) {
        sprite = new Sprite(texture);
        sprite.setPosition(x, y);
        sprite.setSize(32, 32);
        bounds = new Rectangle(x, y, 32, 32);
    }

    // ✅ Headless constructor for testing
    public HelperCharacter(float x, float y) {
        this.sprite = null;
        this.bounds = new Rectangle(x, y, 32, 32);
        this.collected = false;
    }

    public void render(SpriteBatch batch) {
        if (!collected && sprite != null) {
            sprite.draw(batch);
        }
    }

    // Game logic
    public void checkCollected(Player player, FirstScreen screen) {
        Rectangle playerRect = new Rectangle(
            player.playerX, player.playerY,
            player.playerWidth, player.playerHeight
        );

        if (!collected && bounds.overlaps(playerRect)) {
            collected = true;
            player.goodEvent += 1;
            screen.timePassed += 20f;
            AchievementManager.get().unlock("HELPER_FOUND");
        }
    }

    // ✅ Test-friendly method
    public void collect() {
        collected = true;
    }

    public boolean isCollected() {
        return collected;
    }
}
