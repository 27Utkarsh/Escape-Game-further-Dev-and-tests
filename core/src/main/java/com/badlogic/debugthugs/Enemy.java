package com.badlogic.debugthugs;

import java.util.List;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
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
    float repathInterval = 3f;

    public Enemy(Texture texture, float x, float y, Pathfinding pathfinder) {
        enemySprite = new Sprite(texture);
        this.x = x;
        this.y = y;
        this.pathfinder = pathfinder;
        enemySprite.setSize(32,32);
        bounds = new Rectangle(x,y,32,32);
    }
    public void update(Player player) {
        float delta = Gdx.graphics.getDeltaTime();

        repathTimer -= delta;
        if (repathTimer <= 0)
        {
            repathTimer = repathInterval;

            path = pathfinder.findPath(x, y, player.playerX, player.playerY);
            pathIndex = 0;
        }

        if (path != null && pathIndex < path.size())
        {
            movePathfinding(player, delta);
        }
        else if (path == null)
        {
            //System.out.println("Path is null");
            moveDirect(player, delta);
        }
        else if (pathIndex >= path.size())
        {
            //System.out.println("Path index >= path.size()");
            moveDirect(player, delta);
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
        System.out.println(target.toString());
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

            //Player tempPlayer = new Player(nextX, nextY, player.walkCycle, player.wallLayer, player.doorLayer);

            //if(!Collision.collisionCheck(tempPlayer)) {
            //    x = nextX;
            //    y = nextY;
            //    enemySprite.setPosition(x, y);
            //    bounds.setPosition(x, y);
            //}
            //else
            //{
            //    System.out.println("collision");
            //}
        }
    }

    /**
     * Enemy moves in a straight line directly towards the player.
     */
    private void moveDirect(Player player, float delta)
    {
        float distX = player.playerX - x;
        float distY = player.playerY - y;
        float distance = (float) Math.sqrt(distX * distX + distY * distY);

        float nx = distX / distance;
        float ny = distY / distance;

        float nextX = x + nx * speed * delta;
        float nextY = y + ny * speed * delta;

        x = nextX;
        y = nextY;
        enemySprite.setPosition(x, y);
        bounds.setPosition(x, y);
    }

    public void render(SpriteBatch sb) {
        enemySprite.draw(sb);
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
