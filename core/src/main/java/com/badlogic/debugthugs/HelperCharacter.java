package com.badlogic.debugthugs;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;

public class HelperCharacter {
    public Sprite sprite;
    public Rectangle bounds;
    public boolean active= true;
    private float x,y;
    public HelperCharacter(Texture texture, float x, float y) {
        this.x = x;
        this.y= y;
        sprite = new Sprite(texture);
        sprite.setSize(32, 32);
        sprite.setPosition(x, y);
        bounds = new Rectangle(x, y, 32, 32);

    }
    public void update(){

    }
    public void render(SpriteBatch batch){
        if (active) sprite.draw(batch);
    }
    public boolean collision(Player player){
        if(!active) return false;
        Rectangle playerRect= new Rectangle(
            player.playerX,
            player.playerY,
            player.playerWidth,
            player.playerHeight
        );
        if (bounds.overlaps(playerRect)) {
            active= false;
            return true;
        }
        return false;
    }
}
