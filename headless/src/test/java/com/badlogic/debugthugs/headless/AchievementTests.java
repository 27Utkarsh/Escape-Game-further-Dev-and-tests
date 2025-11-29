package com.badlogic.debugthugs.headless;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.badlogic.debugthugs.AchievementManager;

public class AchievementTests extends AbstractHeadlessTest {
    private AchievementManager mgr;
    
    @BeforeEach
    void reset()
    {
        mgr = AchievementManager.get();
        mgr.resetAll();
    }

    @Test
    void achievementsStartLocked()
    {
        assertFalse(mgr.isUnlocked("ESCAPED"));
        assertFalse(mgr.isUnlocked("FOUND_KEY"));        
    }

    @Test
    void unlockShouldSetStateTrue() {
        assertFalse(mgr.isUnlocked("FOUND_KEY"));
        mgr.unlock("FOUND_KEY");
        assertTrue(mgr.isUnlocked("FOUND_KEY"));
    }

    @Test
    void unlockingInvalidKeyDoesNotCrash()
    {
        mgr.unlock("INVALID_ACHIEVEMENT_KEY");
        assertFalse(mgr.isUnlocked("INVALID_ACHIEVEMENT_KEY"));
    }

    @Test
    void resetAllClearsAllAchievements() {
        mgr.unlock("ESCAPED");
        mgr.unlock("FOUND_KEY");

        mgr.resetAll();

        assertFalse(mgr.isUnlocked("ESCAPED"));
        assertFalse(mgr.isUnlocked("FOUND_KEY"));
    }

    @Test
    void popupIsQueuedWhenUnlocked() {
        AchievementManager mgr = AchievementManager.get();
        assertEquals(0, mgr.getPopupQueue().size());
        mgr.unlock("ENERGISED");
        assertEquals(1, mgr.getPopupQueue().size());
        mgr.update(0.01f);
        assertTrue(isDisplayingPopup(mgr));
    }

    @Test
    void multiplePopupsQueueAndDisplaySequentially() {
        mgr.unlock("FOUND_KEY");
        mgr.unlock("ESCAPED");
        
        assertEquals(2, mgr.getPopupQueue().size());
        mgr.update(0.1f);
        assertEquals(1, mgr.getPopupQueue().size());
        assertEquals("Achievement Unlocked: FOUND KEY", mgr.getCurrentPopup());
        
        // Wait 2 seconds
        for (int i = 0; i < 20; i++)
        {
            mgr.update(0.1f);
        }
        assertEquals(0, mgr.getPopupQueue().size());
        assertEquals("Achievement Unlocked: ESCAPED", mgr.getCurrentPopup());
        
        mgr.update(3f);
        assertFalse(isDisplayingPopup(mgr));
    }

    private boolean isDisplayingPopup(AchievementManager mgr)
    {
        mgr.update(0.0001f);
        return mgr.getCurrentPopup() != null;
    }
}
