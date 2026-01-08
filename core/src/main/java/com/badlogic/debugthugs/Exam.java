package com.badlogic.debugthugs;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;

public class Exam {
    public Sprite examSprite;
    public Texture overlayTexture;
    public Rectangle bounds;

    private float x, y;

    private final Main game;

    // Patrol data
    private float patrolStartY;
    private float patrolEndY;
    private float speed = 60f;     
    private int direction = 1;      

    private float overlayTimer = 0f;     
    private boolean examed = false;

    private boolean testMode = false;
    

    public Exam(Texture texture1, Texture texture2, float x, float y, float patrolDistance, Main game) {

        this.x = x;
        this.y = y;
        this.game = game;

        examSprite = new Sprite(texture1);
        examSprite.setPosition(x, y);
        examSprite.setSize(50, 50);

        overlayTexture = texture2;

        bounds = new Rectangle(1184, 1500, 50, 50);
        
        // Patrol from y to y + patrolDistance
        patrolStartY = 1000;
        patrolEndY = 1000 + patrolDistance;
    }

    /** Call this every frame*/
    public void update(float delta) {
        if (testMode) return;
        // Move horizontally according to current direction
        y += speed * direction * delta;

        // Reverse direction at patrol bounds
        if (y < patrolStartY) {
            y = patrolStartY;
            direction = 1;
        } else if (y > patrolEndY) {
            y = patrolEndY;
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
    }

    public void renderOverlay(SpriteBatch sb)
    {
        if (overlayTimer >= 0f) 
        {   
            sb.draw(overlayTexture, 0, 0, game.uiViewport.getWorldWidth(), game.uiViewport.getWorldHeight());
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
            overlayTimer = 15f;
        }
    }

    /**
     * Create a test instance of exam.
     * 
     * @param game The main game instance.
     */
    public Exam(Main game)
    {

        testMode = true;
        this.game = game;
        examed = false;
        bounds = new Rectangle(1184, 1500, 50, 50);
        overlayTimer = 0f;
    }
    
    public Exam(float x, float y, float patrolDistance) 
    {
        testMode = true;
        this.x = x;
        this.y = y;
        this.game = null;
        this.examSprite = null;
        this.overlayTexture = null;

        this.bounds = new Rectangle(x, y, 50, 50);
        this.patrolStartY = y;
        this.patrolEndY = y + patrolDistance;

        this.speed = 60f;
        this.direction = 1;
        this.examed = false;
        this.overlayTimer = 0f;
        this.testMode = false;
    }
}



