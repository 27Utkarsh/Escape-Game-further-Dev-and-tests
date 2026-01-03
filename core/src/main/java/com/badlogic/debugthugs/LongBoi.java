package com.badlogic.debugthugs;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;

public class LongBoi {
    public Sprite longBoiSprite;
    public Rectangle bounds;
    public boolean triggered;
    public boolean active = true;
    public float eventTime = 0f;
    public float longX, bushX, bushY;

    private boolean testMode = false;

    Animation<TextureRegion> longBush;
    // triggered is used to track if the player has triggered the event
    // active is used to track if the longBoiSprite is still being rendered
    // eventTime is use to track the timing of animations and movement for this event
    // longX/y and bushX/Y are used to store the coordinate positions of the longBoiSprite and longBushAnimation

    /**
    @param texture is used for initalizing the longBoiSprite to a texture
    @param bush is used to initalize the longBush animation to a textureRegion
    @param x is used to define the x coordinate of the longBoiSprite
    @param y is used to define the y coordinate of the longBoiSprite
    */
    public LongBoi(Texture texture, Animation<TextureRegion> bush, float x, float y) {

        longX = x;
        bushX = x - 120f;
        bushY = y;
        longBush = bush;
        longBush.setPlayMode(Animation.PlayMode.NORMAL);
        longBoiSprite = new Sprite(texture);
        longBoiSprite.setPosition(x, y);
        longBoiSprite.setSize(32, 32);
        bounds = new Rectangle(x, y, 128, 128);
        /**
        *bushX is placed 120 units to the left of longBoiSprite
        *bounds defines the size and position of the object that is used for checking player collision
        */
    }

    public void render(SpriteBatch sb) {
        TextureRegion bushFrames;
        if (active) {  
           
            longBoiSprite.draw(sb);
            bushFrames = longBush.getKeyFrame(0f);
        }
        else if(!active && eventTime >= 0.7f){
            bushFrames = longBush.getKeyFrame(0.7f);

        }
        else{
            bushFrames = longBush.getKeyFrame(eventTime,false);
        }
        sb.draw(bushFrames,bushX,bushY);

        /*
         *bushFrames is used to store the frame of longBushAnimation that should be rendered in eventTime
         *longBoiSprite is drawn only when active is true
         *when !active longBoiSprite is not rendered and eventTime begins incrementing
         *once eventTime >= 0.7 the bushAnimation is locked to its final frame 
         */
    }

    public void checkTriggered(Player player) {
        if (testMode) return;

        if (!triggered && bounds.overlaps(new Rectangle(player.playerX, player.playerY, player.playerWidth, player.playerHeight))) {
            player.hiddenEvent += 1;
            triggered = true;
            eventTime = 0f;
            AchievementManager.get().unlock("Quack");
            /**
             * checks if the player has entered the bounds and changes triggered to true,
             * this prevents the event from incrementing hiddenEvent multiple times.
             * it resets eventTime to track time from the time of triggering.
             */
        }
    }

    public void update(float delta) {
        if (testMode) return;

        if (triggered) {
            longBoiSprite.translateX(-40 * delta);
            eventTime += delta;
            if (eventTime >= 3f) {
                active = false;
                eventTime = 0f;
            }
        }
        else if(!active){
            eventTime += delta;
        }
    }
    /**
     * @param delta is passed from the firstScreen and tracks the passing of time.
     * when triggered == true longBoiSprite is moved -120 units to the left over 3 seconds,
     * this effectively moves longBoiSprite on-top of the bushFrames texture being rendered.
     * Once 3 secconds have passed active is set to false to stop rendering of longBoiSprite,
     * and event time is reset to be usable for tracking animation frames for longBush.
     */

    /**
     * Create a test instance for LongBoi.
     */
    public LongBoi()
    {
        testMode = true;
    }
}    
