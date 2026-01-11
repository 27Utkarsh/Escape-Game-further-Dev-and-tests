package com.badlogic.debugthugs;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;

public class Exam {
    public Sprite examSprite;
    public Texture overlayTexture;
    public Rectangle bounds;
    private float examX;
    private float examY;
    private final Main game;

    // Patrol data
    private float patrolStartY;
    private float patrolEndY;
    private float speed = 60f;
    private int direction = 1;
    private float overlayTimer = 0f;
    private boolean examed = false;
    private boolean testMode = false;

    public Exam(Texture texture1, Texture texture2, float startX, float startY, float patrolDistance, Main game) {
        this.examX = startX;
        this.examY = startY;
        this.game = game;

        if (com.badlogic.gdx.Gdx.gl != null) {
            examSprite = new Sprite(texture1);
        } else {
            examSprite = new Sprite();
        }

        examSprite.setSize(50f, 50f);
        examSprite.setPosition(examX, examY);
        examSprite.setSize(50, 50);

        overlayTexture = texture2;
        bounds = new Rectangle(1184, 1500, 50, 50);

        // Patrol from y to y + patrolDistance
        patrolStartY = 1000;
        patrolEndY = 1000 + patrolDistance;
    }

    // Call this every frame
    public void update(float delta) {
        if (testMode) {
            return;
        }

        // Move vertically according to current direction
        examY += speed * direction * delta;

        // Reverse direction at patrol bounds
        if (examY < patrolStartY) {
            examY = patrolStartY;
            direction = 1;
        } else if (examY > patrolEndY) {
            examY = patrolEndY;
            direction = -1;
        }

        // Apply position to sprite & bounds
        if (examSprite != null) {
            examSprite.setPosition(examX, examY);
        }
        bounds.setPosition(examX, examY);

        overlayTimer -= delta;
    }

    public void render(SpriteBatch sb) {
        if (!examed) {
            examSprite.draw(sb);
        }
    }

    public void renderOverlay(SpriteBatch sb) {
        if (overlayTimer > 0f) {
            sb.draw(overlayTexture, 0, 0, game.uiViewport.getWorldWidth(), game.uiViewport.getWorldHeight());
        }
    }

    public void checkCollided(Player player) {
        Rectangle playerBounds = new Rectangle(
                player.playerX,
                player.playerY,
                player.playerWidth,
                player.playerHeight);

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
    public Exam(Main game) {
        testMode = true;
        this.game = game;
        examed = false;
        bounds = new Rectangle(1184, 1500, 50, 50);
        overlayTimer = 0f;
    }

    public Exam(float startX, float startY, float patrolDistance) {
        this.examX = startX;
        this.examY = startY;
        this.game = null;
        this.examSprite = null;
        this.overlayTexture = null;
        this.bounds = new Rectangle(startX, startY, 50, 50);
        this.patrolStartY = startY;
        this.patrolEndY = startY + patrolDistance;
        this.speed = 60f;
        this.direction = 1;
        this.examed = false;
        this.overlayTimer = 0f;
    }
}
