package com.badlogic.debugthugs;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;

public class Exam {
public Sprite examSprite;
public Texture overlayTexture;
private SpriteBatch batch;
    public Rectangle bounds;

    private float x, y;

    private final Main game;

    // Patrol data
    private float patrolStartX;
    private float patrolEndX;
    private float speed = 60f;     
    private int direction = 1;      

    private float overlayTimer = 0f;     
    private boolean examed = false;
    

    public Exam(Texture texture1, Texture texture2, float x, float y, float patrolDistance, Main game) {

        this.x = x;
        this.y = y;
        this.game = game;

        examSprite = new Sprite(texture1);
        examSprite.setPosition(x, y);
        examSprite.setSize(32, 32);

        batch = new SpriteBatch();

        overlayTexture = texture2;

        bounds = new Rectangle(780, 1800, 32, 32);
        
        // Patrol from x to x + patrolDistance
        patrolStartX = 780;
        patrolEndX = 780 + patrolDistance;
    }

    /** Call this every frame*/
    public void update(float delta) {
        // Move horizontally according to current direction
        x += speed * direction * delta;

        // Reverse direction at patrol bounds
        if (x < patrolStartX) {
            x = patrolStartX;
            direction = 1;
        } else if (x > patrolEndX) {
            x = patrolEndX;
            direction = -1;
        }

        // Apply position to sprite & bounds
        examSprite.setPosition(x, y);
        bounds.setPosition(x, y);
        overlayTimer -= delta;
    }

    public void render(SpriteBatch sb) {
        if (!examed) {
            examSprite.draw(sb);
        }

        if (overlayTimer >= 0f) 
        {   
            game.batch.setProjectionMatrix(game.uiCamera.combined);
            batch.begin();
            batch.draw(overlayTexture, 0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
            batch.end();
        }
    }

    public void checkCollided(Player player) {
        Rectangle playerBounds = new Rectangle(
                player.playerX,
                player.playerY,
                player.playerWidth,
                player.playerHeight
        );

        if (!examed && bounds.overlaps(playerBounds)) {
            // Trigger event
            player.badEvent += 1;
            AchievementManager.get().unlock("EXAM");
            examed = true;
            overlayTimer = 7f;
        }
    }
    public void dispose() {
        overlayTexture.dispose();
        batch.dispose();
    }

}


