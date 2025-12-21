package com.badlogic.debugthugs;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;

public class Coin {
    public float x, y;
    public float width = 32, height = 32;
    public float bonusPoints;
    public boolean collected = false;
    public Rectangle bounds;
    public Texture texture;

    public Coin(Texture texture, float x, float y) {
        this.texture = texture;
        this.x = x;
        this.y = y;
        this.bounds = new Rectangle(x, y, width, height);
    }

    public void render(SpriteBatch batch) {
        if (!collected) {
            batch.draw(texture, x, y, width, height);
        }
    }

    public void checkCollected(Player player, FirstScreen firstScreen) {
        Rectangle playerRect = new Rectangle(player.playerX, player.playerY, player.playerWidth, player.playerHeight);
        if (!collected && bounds.overlaps(playerRect)) {
            collected = true;

            // Add to coin points
            firstScreen.coinBonusPoints += bonusPoints;

            // Increment positive events
            player.goodEvent += 1;

            // Announce the coin collection
            AchievementManager.get().showPopup("Coin Collected! +" + (int)bonusPoints + " points");
        }
    }
}
