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
    public boolean caughtPlayer = false;
    float x, y;
    float speed = 90f;
    float cooldown = 0f;
    float interval = 2f;

    Pathfinding pathfinder;
    List<Vector2> path = null;
    int pathIndex = 0;
    float repathTimer = 0f;
    float repathInterval = 0.5f;
    private ShapeRenderer shapeRenderer;
    Main game;

    public Enemy(Texture texture, float x, float y, Pathfinding pathfinder, Main game) {
        enemySprite = new Sprite(texture);
        this.x = x;
        this.y = y;
        this.pathfinder = pathfinder;
        enemySprite.setSize(32,32);
        bounds = new Rectangle(x,y,32,32);

        shapeRenderer = new ShapeRenderer();
        this.game = game;
    }
    public void update(Player player) {
        float delta = Gdx.graphics.getDeltaTime();

        repathTimer -= delta;
        if (repathTimer <= 0)
        {
            repathTimer = repathInterval;

            path = pathfinder.findPath(x, y, player.playerX, player.playerY);
            if (path.size() > 1){
                pathIndex = 1;
            } else
            {
                pathIndex = 0;
            }
        }

        if (path != null && pathIndex < path.size())
        {
            movePathfinding(player, delta);
        }

        if (cooldown > 0) {
            cooldown -= delta;
        }
    }

    /**
     * Enemy uses A* pathfinding to follow the shortest path towards the player.
     */
    private void movePathfinding(Player player, float delta)
    {
        Vector2 target = path.get(pathIndex);
        float distX = target.x - x;
        float distY = target.y - y;
        float distance = (float)Math.sqrt(distX * distX + distY * distY);

        if (distance < 2f)
        {
            pathIndex++;
        }
        else
        {
            float nx = distX / distance;
            float ny = distY / distance;

            float nextX = x + nx * speed * delta;
            float nextY = y + ny * speed * delta;

            x = nextX;
            y = nextY;
            enemySprite.setPosition(x, y);
            bounds.setPosition(x, y);
        }
    }

    public void render(SpriteBatch sb) {
        enemySprite.draw(sb);
    }

    /**
     * Draws a visualisation of the path the enemy plans to take, useful for debugging but should not be enabled in the final game.
     * @param player
     */
    public void renderDebugPath(Player player)
    {
        if (path == null) return;

        shapeRenderer.setProjectionMatrix(game.worldCamera.combined);

        shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
        shapeRenderer.setColor(Color.RED);

        shapeRenderer.rect(player.playerX, player.playerY, player.playerX + 32, player.playerY + 32);

        for (int i = 0; i < path.size(); i++)
        {
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
                cooldown = interval;
                player.badEvent += 1;
                AchievementManager.get().unlock("ENCOUTERED_DEAN");
                return true;
            }
        }
        return false;
    }
}
