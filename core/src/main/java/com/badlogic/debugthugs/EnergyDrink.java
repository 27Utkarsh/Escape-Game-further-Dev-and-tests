package com.badlogic.debugthugs;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;
public class EnergyDrink{
    public Sprite energySprite;
    public Rectangle bounds;
    public boolean drank = false;

    public EnergyDrink(Texture texture, float x, float y) {
        energySprite = new Sprite(texture);
        energySprite.setPosition(x, y);
        energySprite.setSize(32,32);
        bounds = new Rectangle(x,y,32,32);
    }

    public void render(SpriteBatch sb) {
        if (!drank) {
            energySprite.draw(sb);
        }
    }

    public Boolean playerTouched(Player player){
        return bounds.overlaps(new Rectangle(player.playerX, player.playerY, player.playerWidth, player.playerHeight));
    }

    public void checkDrank(Player player) {
        if (!drank && playerTouched(player)) {
            player.goodEvent += 1;
            drank = true;
            AchievementManager.get().unlock("ENERGISED");
        }
    }

    /**
     * Create a EnergyDrink instance for testing.
     *
     * Doesn't initialise the sprite so that doesn't interfere with tests.
     *
     * @param x
     * @param y
     * Both these parameters take the theoretical test positioning
     * @param drank Initial value for whether the energy drink has been drank.
     */
    public EnergyDrink(float x, float y, boolean drank)
    {
        this.drank = drank;
        this.bounds = new Rectangle(x,y,32,32);
    }
}
