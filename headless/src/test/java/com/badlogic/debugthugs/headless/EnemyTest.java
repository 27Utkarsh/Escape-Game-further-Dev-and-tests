package com.badlogic.debugthugs.headless;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.badlogic.debugthugs.AchievementManager;
import com.badlogic.debugthugs.Enemy;
import com.badlogic.debugthugs.Player;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;

/**
 * This testing class is used to test the primary logic of the enemy class and ensures
 * that the negative events counter is updated correctly and capped at a maximum of 5 and
 * that achievements are correctly handles.
 */
public class EnemyTest extends AbstractHeadlessTest {

    private Player testPlayer;
    private Enemy testEnemy;
    private TiledMapTileLayer testWallLayer;
    private TiledMapTileLayer testDoorLayer;
    private AchievementManager testAchievementManager;
    private boolean playerCaught;

    /**
     * Each test has a fresh player and a fresh enemy created in different locations, each
     * one does not load graphics or assets. The negative events counter is also reset to 0.
     */
    @BeforeEach
    public void setUp() {
        testWallLayer = new TiledMapTileLayer(10, 10, 32, 32);
        testDoorLayer = new TiledMapTileLayer(10, 10, 32, 32);
        testPlayer = new Player(0f, 0f, testWallLayer, testDoorLayer);
        testEnemy = new Enemy(100f, 100f);
        testPlayer.badEvent = 0;
        playerCaught = false;
    }

    /**
     * Checks that when the player is not caught, nothing should happen.
     */
    @Test
    public void checkPlayerNotCaught() {
        playerCaught = testEnemy.checkCollided(testPlayer);

        assertFalse(playerCaught);
        assertEquals(0, testPlayer.badEvent);
    }

    /**
     * Checks that player being caught is registered and updates the negative events counter.
     */
    @Test
    public void checkPlayerCaught() {
        testEnemy.bounds.setPosition(0,0);

        playerCaught = testEnemy.checkCollided(testPlayer);

        assertTrue(playerCaught);
        assertEquals(1, testPlayer.badEvent);
    }

    /**
     * Checks that when the player is immediately caught twice, the second time should not be registered.
     */
    @Test
    public void cooldownWorks() {
        testEnemy.bounds.setPosition(0,0);

        playerCaught = testEnemy.checkCollided(testPlayer);
        assertTrue(playerCaught);

        //Immediately checks the player for being caught again
        playerCaught = testEnemy.checkCollided(testPlayer);
        assertFalse(playerCaught);

        assertEquals(1, testPlayer.badEvent);
    }

    /**
     * Checks that when the player has waited after the cooldown, they will be caught again, and that the negative events counter only reach a maximum of 5.
     */
    @Test
    public void negativeEventsCapped() {
        testEnemy.bounds.setPosition(0,0);

        //Loop tests dean still works after cooldown
        for (int i = 1; i <= 5; i++) {
            playerCaught = testEnemy.checkCollided(testPlayer);
            assertTrue(playerCaught);
            assertEquals(i, testPlayer.badEvent);
            testEnemy.reduceCooldown();
        }

        //Tests negative events limit
        playerCaught = testEnemy.checkCollided(testPlayer);
        assertTrue(playerCaught);
        assertEquals(5, testPlayer.badEvent);

    }

    /**
     * Checks that when the dean catches the player the corresponding achievement is unlocked.
     */
    @Test
    public void achievementUnlocked() {
        testAchievementManager = AchievementManager.get();
        testAchievementManager.resetAll();

        testEnemy.bounds.setPosition(0,0);
        testEnemy.checkCollided(testPlayer);

        assertTrue(testAchievementManager.isUnlocked("ENCOUTERED_DEAN"));
    }

}
