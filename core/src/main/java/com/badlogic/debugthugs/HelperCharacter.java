package com.badlogic.debugthugs;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;

/**
 * A friendly character that gives the player extra time when touched.
 */
public class HelperCharacter {

    public Sprite sprite;
    public Rectangle bounds;
    public boolean active = true;

    private float x, y;

    /**
     * Create a helper character at a specific position with a texture
     *
     * @param texture the texture to display
     * @param x       x-coordinate in the world
     * @param y       y-coordinate in the world
     */
    public HelperCharacter(Texture texture, float x, float y) {
        this.x = x;
        this.y = y;

        sprite = new Sprite(texture);
        sprite.setSize(32, 32);
        sprite.setPosition(x, y);

        bounds = new Rectangle(x, y, 32, 32);
    }

    /**
     * Update method for future animations or movement.
     * Currently does nothing, helper is static.
     */
    public void update() {
        // Static helper for now
    }

    /**
     * Render the helper sprite if active
     *
     * @param batch the SpriteBatch used for drawing
     */
    public void render(SpriteBatch batch) {
        if (active) sprite.draw(batch);
    }

    /**
     * Checks if the player has collided with the helper
     * If so, the helper disappears and returns true
     *
     * @param player the Player object
     * @return true if collision happened
     */
    public boolean checkCollided(Player player) {
        if (!active) return false;

        Rectangle playerRect = new Rectangle(
            player.playerX,
            player.playerY,
            player.playerWidth,
            player.playerHeight
        );

        if (bounds.overlaps(playerRect)) {
            active = false; // Remove helper after collision
            return true;
        }

        return false;
    }
}
