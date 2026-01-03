package com.badlogic.debugthugs;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;

public class Duck {
    public Sprite duckSprite;
    public Rectangle bounds;

    private float x, y;

    // Patrol data
    private float patrolStartX;
    private float patrolEndX;
    private float speed = 60f;      
    private int direction = 1;      

    private boolean testMode = false;


    public Duck(Texture texture, float x, float y, float patrolDistance) {

        this.x = x;
        this.y = y;

        duckSprite = new Sprite(texture);
        duckSprite.setPosition(x, y);
        duckSprite.setSize(50, 55);

        bounds = new Rectangle(680, 520, 50, 55);
        
        
        patrolStartX = 670;
        patrolEndX = 670 + patrolDistance;
    }

    /** Call this every frame*/
    public void update(float delta) {
        if (testMode) return;

        // Move horizontally according to current direction
        x += speed * direction * delta;

        if (x < patrolStartX) {
            x = patrolStartX;
            direction = 1;
        } else if (x > patrolEndX) {
            x = patrolEndX;
            direction = -1;
        }

        // Apply position to sprite & bounds
        duckSprite.setPosition(x, y);
        bounds.setPosition(x, y);
    }

    public void render(SpriteBatch sb) {
        duckSprite.draw(sb);
    }

    public void checkCollided(Player player) {
        Rectangle playerBounds = new Rectangle(
                player.playerX,
                player.playerY,
                player.playerWidth,
                player.playerHeight
        );

        if (bounds.overlaps(playerBounds)) {
            // Trigger event
            player.badEvent += 1;
            AchievementManager.get().unlock("DUCK_OF_RESETTING");

            player.playerX = 710;
            player.playerY = 1730;
        }
    }

    /**
     * Create an instance of Duck for testing.
     */
    public Duck()
    {
        testMode = true;
        this.bounds = new Rectangle(680, 520, 50, 55);
    }
}