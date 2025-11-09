package com.badlogic.debugthugs;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;

public class Enemy {
    public Sprite enemySprite;
    public Rectangle bounds;
    public boolean caughtPlayer = false;
    Texture enemyTexture;
    float x, y;
    float speed = 90f;
    float cooldown = 0f;
    float interval = 2f;

    public Enemy(Texture texture, float x, float y) {
        this.enemyTexture  = texture;
        enemySprite = new Sprite(texture);
        this.x = x;
        this.y = y;
        enemySprite.setSize(32,32);
        bounds = new Rectangle(x,y,32,32);
    }
    public void update(Player player) {
        float delta = Gdx.graphics.getDeltaTime();
        float distX = player.playerX - x;
        float distY = player.playerY - y;
        float distance = (float) Math.sqrt(distX * distX + distY * distY);

        float nx = distX / distance;
        float ny = distY / distance;

        float nextX = x + nx * speed * delta;
        float nextY = y + ny * speed * delta;

        Player tempPlayer = new Player(nextX, nextY, player.walkCycle, player.wallLayer, player.doorLayer);

        if(Collision.collisionCheck(tempPlayer) == false) {
            x = nextX;
            y = nextY;
            enemySprite.setPosition(x, y);
            bounds.setPosition(x, y);
        }

        if (cooldown > 0) {
            cooldown -= Gdx.graphics.getDeltaTime();
        }
    }

    public void render(SpriteBatch sb) {
        enemySprite.draw(sb);
    }

    public boolean checkCollided(Player player) {
        if (bounds.overlaps(new Rectangle(player.playerX, player.playerY, player.playerWidth, player.playerHeight))) {
            if (cooldown <= 0f) {
                cooldown = interval;
                return true;
            }
        }
        return false;
    }
}
