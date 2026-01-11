package com.badlogic.debugthugs;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;

public class Duck {
    public Sprite duckSprite;
    public Rectangle bounds;

    private float duckX;
    private float duckY;

    // Patrol data
    private float patrolStartX;
    private float patrolEndX;
    private float speed = 60f;      
    private int direction = 1;      

    private boolean testMode = false;


    public Duck(Texture texture, float startX, float startY, float patrolDistance) {

        this.duckX = startX;
        this.duckY = startY;

        if (com.badlogic.gdx.Gdx.gl != null) {
            duckSprite = new Sprite(texture);
        } else {
            duckSprite = new Sprite();
            duckSprite.setSize(50f, 55f);
        }
        duckSprite.setPosition(duckX, duckY);
        duckSprite.setSize(50, 55);

        bounds = new Rectangle(680, 520, 50, 55);


        patrolStartX = 670;
        patrolEndX = 670 + patrolDistance;
    }

    /** Call this every frame*/
    public void update(float delta) {
        if (testMode) {
            return;
        }

        // Move horizontally according to current direction
        duckX += speed * direction * delta;

        if (duckX < patrolStartX) {
            duckX = patrolStartX;
            direction = 1;
        } else if (duckX > patrolEndX) {
            duckX = patrolEndX;
            direction = -1;
        }

        // Apply position to sprite & bounds
        duckSprite.setPosition(duckX, duckY);
        bounds.setPosition(duckX, duckY);
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
    public Duck() {
        testMode = true;
        this.bounds = new Rectangle(680, 520, 50, 55);
    }
}
