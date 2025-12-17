package com.badlogic.debugthugs;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;

public class LongBoi {
    public Sprite longBoiSprite;
    public Rectangle bounds;
    public boolean triggered = false;
    public boolean active = false;
    public float timer = 0f;

    public LongBoi(Texture texture, float x, float y) {
        longBoiSprite = new Sprite(texture);
        longBoiSprite.setPosition(x, y);
        longBoiSprite.setSize(32, 32);
        bounds = new Rectangle(x, y, 64, 64);
    }

    public void render(SpriteBatch sb) {
        if (!triggered) {
            longBoiSprite.draw(sb);
        }
    }

    public void checkTriggered(Player player) {
        if (!triggered && bounds.overlaps(new Rectangle(player.playerX, player.playerY, player.playerWidth, player.playerHeight))) {
            player.hiddenEvent += 1;
            triggered = true;
            active = true;
            timer = 10f;
            AchievementManager.get().unlock("Quack");
        }
    }

    public void update(float delta) {
        if (active) {
            timer -= delta;
            if (timer <= 0) {
                active = false;
            
            }
        }
    }
}    

