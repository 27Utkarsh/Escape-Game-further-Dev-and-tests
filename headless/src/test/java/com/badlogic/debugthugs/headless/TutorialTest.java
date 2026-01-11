package com.badlogic.debugthugs.headless;

import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import com.badlogic.debugthugs.MenuScreen;
import com.badlogic.debugthugs.Tutorial;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class TutorialTest extends AbstractHeadlessTest {

    private FakeMain testGame;
    private Tutorial tutorialScreen;

    @BeforeEach
    public void setUp() {
        testGame = new FakeMain();
        tutorialScreen = new Tutorial(testGame);
    }

    @Test
    public void testReturnToMenu() {
        tutorialScreen.returnToMenu();

        assertNotNull(testGame.lastScreen);
        assertInstanceOf(MenuScreen.class, testGame.lastScreen, "Should navigate back to MenuScreen");
    }
}
