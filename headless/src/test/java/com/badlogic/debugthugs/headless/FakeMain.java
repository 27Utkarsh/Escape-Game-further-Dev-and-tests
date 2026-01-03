package com.badlogic.debugthugs.headless;

import com.badlogic.debugthugs.Main;
import com.badlogic.gdx.Screen;

/**
 * FakeMain extends from Main but overrides setScreen to store the last screen which has been set for testing.
 */
public class FakeMain extends Main {
    
    public Screen lastScreen;

    @Override
    public void setScreen(Screen screen) {
        this.lastScreen = screen;
    }
}
