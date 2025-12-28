package com.badlogic.debugthugs;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;

public class WetFloor {
    
    public Sprite wetFloorSprite;
    public Rectangle bounds;
    public boolean triggered;
    public boolean active;
    public float timer = 0f;
    /**
     * 
     * @param texture stores the texture that will be used for wetFloorSprite
     * @param x stores the x coordinate that the sprite will be rendered at
     * @param y stores the x coordinate that the sprite will be rendered at
     * bounds is used to store a Rectangle which is used to trigger the event
     */

    public WetFloor(Texture texture, float x, float y) {
        wetFloorSprite = new Sprite(texture);
        wetFloorSprite.setPosition(x, y);
        wetFloorSprite.setSize(32, 32);
        bounds = new Rectangle(x, y, 8, 8);
    }
 
    public void render(SpriteBatch sb) {
        if (!triggered) {
            wetFloorSprite.draw(sb); 
        }
    }

    public void checkTriggered(Player player) {
        if (!triggered && bounds
                .overlaps(new Rectangle(player.playerX, player.playerY, player.playerWidth, player.playerHeight))) {
            player.badEvent += 1;
            player.playerFall();
            triggered = true;
            active = true;
            timer = 1.1f;
        }
        /**
         * when player overlaps the bounds;
         *  badEvent is incremented,
         *  playerFall() is called to set playerAnimation = State.FALL,
         *  triggered = true (to stop event from triggering multiple times),
         *  active = true used for logic in update()
         *  timer = 1.1f is used to start to count down time passing
         * 
         * 
         */
    }

    public void update(float delta) {
        if (active) {
            timer -= delta;
            if (timer <= 0) {
                active = false;
                AchievementManager.get().unlock("Watch Your Step");
            }
        }
    }
    /**
     * when active is set to true the timer counts down from 1.1f,
     * active is then set to false this controls the time the player
     * cannot move for
     */
}