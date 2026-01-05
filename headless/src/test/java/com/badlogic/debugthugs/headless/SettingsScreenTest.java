package com.badlogic.debugthugs.headless;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import com.badlogic.debugthugs.MenuScreen;
import com.badlogic.debugthugs.SettingsScreen;

public class SettingsScreenTest extends AbstractHeadlessTest {

    private FakeMain testGame;
    private SettingsScreen testSettingsScreen;
    private float testVolume;

    @BeforeEach
    public void setUp() {
        testGame = new FakeMain();
        testSettingsScreen = new SettingsScreen(testGame);
        testSettingsScreen.volume = -1f;
        testVolume = -1f;
    }

    @Test
    public void defaultVolume() {
        testVolume = testSettingsScreen.getNoise();

        assertEquals(0.5f, testVolume);
    }

    @Test
    public void changeVolume() {
        testSettingsScreen.setNoise(0.8f);
        testVolume = testSettingsScreen.getNoise();

        assertEquals(0.8f, testVolume);
    }

    @Test
    public void persistentVolume() {
        testSettingsScreen.setNoise(0.8f);
        SettingsScreen changedScreen = new SettingsScreen(testGame);
        testVolume = changedScreen.getNoise();

        assertEquals(0.8f,  testVolume);
    }

    @Test
    public void returnToMainMenu() {
        testSettingsScreen.returnMain(testGame);

        assertNotNull(testGame.lastScreen);
        assertInstanceOf(MenuScreen.class, testGame.lastScreen);
    }

    @Test
    public void volumeInBounds() {
        testSettingsScreen.setNoise(0.009f);
        assertEquals(0.5f, testSettingsScreen.getNoise());

        testSettingsScreen.setNoise(1.1f);
        assertEquals(0.5f, testSettingsScreen.getNoise());
    }
}
