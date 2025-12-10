package com.badlogic.debugthugs;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;

public class WetFloor {
    public Sprite wetFloorSprite;
    public Rectangle bounds;
    public boolean triggered = false;
    public boolean active = false;
    public float timer = 0f;

    public WetFloor(Texture texture, float x, float y) {
        wetFloorSprite = new Sprite(texture);
        wetFloorSprite.setPosition(x, y);
        wetFloorSprite.setSize(32, 32);
        bounds = new Rectangle(x, y, 16, 16);
    }

    public void render(SpriteBatch sb) {
        if (!triggered) {
            wetFloorSprite.draw(sb);
        }
    }

    public void checkTriggered(Player player) {
        if (!triggered && bounds
                .overlaps(new Rectangle(player.playerX, player.playerY, player.playerWidth, player.playerHeight))) {
            player.badEvent += 1;
            triggered = true;
            active = true;
            timer = 5f;
        }
    }

    public void update(float delta) {
        if (active) {
            timer -= delta;
            if (timer <= 0) {
                active = false;
                AchievementManager.get().unlock("Watch Your Step");
            }
        }
    }
}