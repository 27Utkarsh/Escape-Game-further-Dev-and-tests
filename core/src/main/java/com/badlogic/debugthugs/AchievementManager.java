package com.badlogic.debugthugs;

import java.util.ArrayDeque;
import java.util.Queue;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

/**
 * Manages the persistent saving and accessing of achievements, as well as displaying achievement pop-up notifications.
 * 
 * Implemented as a singleton, and accessed using {@code AchievementManager.get()}, ensuring a single shared instance across the application.
 * Achievements are stored using LibGDX {@link Preferences}.
 */
public class AchievementManager {
    private static AchievementManager instance;
    private final Preferences prefs;

    /** All valid achievement preference keys */
    private static final String[] ACHIEVEMENT_KEYS = {
        "ESCAPED",
        "ENCOUTERED_DEAN",
        "FOUND_KEY",
        "UNLOCKED_DOOR",
        "ENERGISED",
        "FLAWLESS_RUN",
        "TELEPORTED",
        "DUO_AUTHENTICATED",
        "WATCH_YOUR_STEP",
	"Quack"
    };

    private Queue<String> popupQueue = new ArrayDeque<>();
    private float popupTimer = 0f;
    private String currentPopup = null;
    private final BitmapFont popupFont = new BitmapFont();
    private final Color popupColor = new Color(1f, 1f, 1f, 1f);
    private final GlyphLayout layout = new GlyphLayout();
    
    /**
     * Create a new instance of AchievementManager.
     * Initialises the achievements preferences file if necessary.
     */
    private AchievementManager()
    {
        prefs = Gdx.app.getPreferences("Achievements");
        initialiseAchievements();

        popupFont.getData().setScale(1.2f);
    }

    /**
     * Returns the single shared instance of the AchievementManager, using a Singleton pattern.
     * 
     * Creates the instance if it has not yet been initialised.
     */
    public static AchievementManager get() {
        if (instance == null) {
            instance = new AchievementManager();
        }
        return instance;
    }

    /**
     * Ensures all achievement keys exist in persistent storage.
     * 
     * Missing keys are added and initialised to false.
     */
    private void initialiseAchievements()
    {
        boolean changed = false;
        for (String key : ACHIEVEMENT_KEYS)
        {
            if (!prefs.contains(key))
            {
                prefs.putBoolean(key, false);
                changed = true;
            }
        }

        if (changed) prefs.flush();
    }


    /**
     * Returns whether an achievement is unlocked.
     */
    public boolean isUnlocked(String key) {
        if (!isValidKey(key))
        {
            System.err.println("Achievement key not recognised: " + key);
            return false;
        }
        return prefs.getBoolean(key, false);
    }

    /**
     * Unlocks an achievement if it is valid and not already unlocked.
     * 
     * Queues a pop-up achievement notification.
     * @param key the name of the achievement to unlock.
     */
    public void unlock(String key)
    {
        if (!isValidKey(key))
        {
            System.err.println("Achievement key not recognised: " + key);
            return;
        }

        if (prefs.getBoolean(key, false)) return;
        prefs.putBoolean(key, true);
        prefs.flush();
        popupQueue.offer("Achievement Unlocked: " + key.replace("_", " "));
    }

    /**
     * Checks whether the key is a valid achievment identifier i.e. it is in ACHIEVEMENT_KEYS.
     */
    private boolean isValidKey(String key) {
        for (String k : ACHIEVEMENT_KEYS)
        {
            if (k.equals(key)) return true;
        }
        return false;
    }

    /**
     * Reset all achievements to false.
     * 
     * Mainly intended for testing.
     */
    public void resetAll()
    {
        for (String key : ACHIEVEMENT_KEYS) {
            prefs.putBoolean(key, false);
        }
        prefs.flush();

        currentPopup = null;
        popupQueue = new ArrayDeque<>();
    }

    public Queue<String> getPopupQueue() {
        return popupQueue;
    }

    public String getCurrentPopup()
    {
        return currentPopup;
    }


    /**
     * Updates the achievement pop-up system every frame.
     * 
     * Handles showing queued pop-ups and fading out the current pop-up.
     * @param delta the time passed since the last frame (seconds).
     */
    public void update(float delta)
    {
        if (currentPopup == null && !popupQueue.isEmpty())
        {
            currentPopup = popupQueue.poll();
            popupTimer = 2f;
            popupColor.a = 1f;
        }

        if (currentPopup != null)
        {
            popupTimer -= delta;

            if (popupTimer <= 0.5f)
            {
                popupColor.a = popupTimer / 0.5f;
            }

            if (popupTimer <= 0f)
            {
                currentPopup = null;
            }
        }
    }

    /**
     * Renders the current achievement pop-up if it exists.
     * @param batch the SpriteBatch to draw the text with.
     * @param xPos the horizontal centre for the pop-up text.
     * @param yPos the vertical centre for the pop-up text.
     */
    public void render(SpriteBatch batch, float xPos, float yPos)
    {
        if (currentPopup == null) return;
        layout.setText(popupFont, currentPopup);
        float textWidth = layout.width;
        float textHeight = layout.height;

        float drawX = xPos - textWidth / 2f;
        float drawY = yPos + textHeight / 2f;

        popupFont.setColor(popupColor);
        popupFont.draw(batch, currentPopup, drawX, drawY);
    }

    public void dispose()
    {
        popupFont.dispose();
    }
}