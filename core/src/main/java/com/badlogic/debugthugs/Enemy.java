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
    float speed = 180f;

    public Enemy(Texture texture, float x, float y) {
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

        x += nx * speed * delta;
        y += ny * speed * delta;
    }

    public void render(SpriteBatch sb) {
        sb.draw(enemyTexture, x, y);
    }

    public void checkCollected(Player player) {
        if (caughtPlayer == false && bounds.overlaps(new Rectangle(player.playerX, player.playerY, player.playerWidth, player.playerHeight))) {
            caughtPlayer = true;
        }
    }
}
