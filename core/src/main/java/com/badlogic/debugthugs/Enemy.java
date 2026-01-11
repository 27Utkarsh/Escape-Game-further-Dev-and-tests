package com.badlogic.debugthugs;

import java.util.List;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;

public class Enemy {
    public Sprite enemySprite;
    public Rectangle bounds;
    public int timesCaught;
    public float enemyX;
    public float enemyY;
    public float speed = 90f;
    public float cooldown = 0f;
    public float interval = 2f;
    Pathfinding pathfinder;
    List<Vector2> path = null;
    int pathIndex = 0;
    float repathTimer = 0f;
    float repathInterval = 0.5f;
    private ShapeRenderer shapeRenderer;
    Main game;
    private boolean testMode = false;

    public Enemy(Texture texture, float x, float y, Pathfinding pathfinder, Main game) {
        enemySprite = new Sprite(texture);
        this.enemyX = x;
        this.enemyY = y;
        this.pathfinder = pathfinder;
        enemySprite.setSize(32, 32);
        bounds = new Rectangle(x, y, 32, 32);
        shapeRenderer = new ShapeRenderer();
        this.game = game;
    }

    public void update(Player player) {
        if (testMode) {
            return;
        }

        float delta = Gdx.graphics.getDeltaTime();
        repathTimer -= delta;
        if (repathTimer <= 0) {
            repathTimer = repathInterval;
            path = pathfinder.findPath(enemyX, enemyY, player.playerX, player.playerY);
            if (path != null && path.size() > 1) {
                pathIndex = 1;
            } else {
                pathIndex = 0;
            }
        }

        if (path != null && pathIndex < path.size()) {
            movePathfinding(player, delta);
        }

        if (cooldown > 0) {
            cooldown -= delta;
        }
    }

    // Enemy uses A* pathfinding to follow the shortest path towards the player.
    private void movePathfinding(Player player, float delta) {
        Vector2 target = path.get(pathIndex);
        float distX = target.x - enemyX;
        float distY = target.y - enemyY;
        float distance = (float) Math.sqrt(distX * distX + distY * distY);

        if (distance < 2f) {
            pathIndex++;
        } else {
            float nx = distX / distance;
            float ny = distY / distance;
            float nextX = enemyX + nx * speed * delta;
            float nextY = enemyY + ny * speed * delta;

            enemyX = nextX;
            enemyY = nextY;
            enemySprite.setPosition(enemyX, enemyY);
            bounds.setPosition(enemyX, enemyY);
        }
    }

    public void render(SpriteBatch sb) {
        enemySprite.draw(sb);
    }

    /**
     * Draws a visualisation of the path the enemy plans to take.
     * @param player
     */
    public void renderDebugPath(Player player) {
        if (path == null) {
            return;
        }

        shapeRenderer.setProjectionMatrix(game.worldCamera.combined);
        shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
        shapeRenderer.setColor(Color.RED);
        shapeRenderer.rect(player.playerX, player.playerY, player.playerX + 32, player.playerY + 32);

        for (int i = 0; i < path.size(); i++) {
            Vector2 point = path.get(i);
            shapeRenderer.rect(point.x, point.y, 32, 32);
            if (i < path.size() - 1) {
                Vector2 next = path.get(i + 1);
                shapeRenderer.line(point.x + 16, point.y + 16, next.x + 16, next.y + 16);
            }
        }
        shapeRenderer.end();
    }

    public boolean checkCollided(Player player) {
        if (bounds.overlaps(new Rectangle(player.playerX, player.playerY, player.playerWidth, player.playerHeight))) {
            if (cooldown <= 0f) {
                if (timesCaught < 5) {
                    timesCaught++;
                    player.badEvent += 1;
                }
                
                cooldown = interval;
                AchievementManager.get().unlock("ENCOUTERED_DEAN");
                return true;
            }
        }
        return false;
    }

    public void reduceCooldown() {
        cooldown = 0f;
    }

    public float getCooldown() {
        return cooldown;
    }

    /**
     * Create an Enemy instance for testing. Doesnt initialise the sprite so that doesnt interfere with tests.
     * @param x the initial x position for the enemy.
     * @param y the initial y position for the enemy.
     */
    public Enemy(float x, float y) {
        this.testMode = true;
        this.enemyX = x;
        this.enemyY = y;
        this.speed = 0f;
        this.interval = 2f;
        this.cooldown = 0f;
        this.bounds = new Rectangle(x, y, 32, 32);
        this.path = null;
        this.pathfinder = null;
    }
}
