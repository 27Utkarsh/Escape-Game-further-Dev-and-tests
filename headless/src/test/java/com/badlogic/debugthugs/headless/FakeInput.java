package com.badlogic.debugthugs.headless;

import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.input.NativeInputConfiguration;


/** Used as a replacement for the default LibGDX input system to allow for simulated button 
 * presses in testing. */
public class FakeInput implements Input {
    private final boolean[] pressedKeys = new boolean[256];
    private InputProcessor processor;

    /**
     * Simulate a key being pressed down.
     * @param key the keycode of the key to press.
     */
    public void press(int key) {
        pressedKeys[key] = true;
    }

    /**
     * Simulate a key being released.
     * @param key the keycode of the key to release.
     */
    public void release(int key){
        pressedKeys[key] = false;
    }

    /**
     * Reset simulated keyboard so that no keys are pressed.
     */
    public void clear() {
        for (int i = 0; i < pressedKeys.length; i++) {
            pressedKeys[i] = false;
        }
    }

    @Override
    public boolean isKeyPressed(int keycode) 
    {
        if (keycode < 0 || keycode >= pressedKeys.length)
        {
            return false;
        }
        return pressedKeys[keycode];
    }

    @Override
    public boolean isKeyJustPressed(int keycode) {
        // Effectively does the same as isKeyPressed.
        return isKeyPressed(keycode);
    }

    // Stub all methods not needed for input simulation in tests (e.g. mouse/sensors).
    
    @Override
    public void setInputProcessor(InputProcessor processor) {
        this.processor = processor;
    }
    @Override
    public InputProcessor getInputProcessor() {
        return processor;
    }
    
    @Override public int getX() { return 0; }
    @Override public int getX(int pointer) { return 0; }
    @Override public int getY() { return 0; }
    @Override public int getY(int pointer) { return 0; }
    @Override public int getDeltaX() { return 0; }
    @Override public int getDeltaX(int pointer) { return 0; }
    @Override public int getDeltaY() { return 0; }
    @Override public int getDeltaY(int pointer) { return 0; }
    @Override public boolean isTouched() { return false; }
    @Override public boolean isTouched(int pointer) { return false; }
    @Override public boolean justTouched() { return false; }
    @Override public float getPressure() { return 0f; }
    @Override public float getPressure(int pointer) { return 0f; }
    @Override public boolean isButtonPressed(int button) { return false; }
    @Override public boolean isButtonJustPressed(int button) { return false; }
    @Override public float getAccelerometerX() { return 0f; }
    @Override public float getAccelerometerY() { return 0f; }
    @Override public float getAccelerometerZ() { return 0f; }
    @Override public float getGyroscopeX() { return 0f; }
    @Override public float getGyroscopeY() { return 0f; }
    @Override public float getGyroscopeZ() { return 0f; }
    @Override public float getAzimuth() { return 0f; }
    @Override public float getPitch() { return 0f; }
    @Override public float getRoll() { return 0f; }
    @Override public void getRotationMatrix(float[] matrix) {}
    @Override public void getTextInput(TextInputListener listener, String title, String text, String hint) {}
    @Override public void getTextInput(TextInputListener listener, String title, String text, String hint, OnscreenKeyboardType type) {}
    @Override public void setOnscreenKeyboardVisible(boolean visible) {}
    @Override public void setOnscreenKeyboardVisible(boolean visible, OnscreenKeyboardType type) {}
    @Override public void openTextInputField(NativeInputConfiguration configuration) {}
    @Override public void closeTextInputField(boolean sendReturn) {}
    @Override public void setKeyboardHeightObserver(KeyboardHeightObserver observer) {}
    @Override public int getMaxPointers() { return 1; }
    @Override public long getCurrentEventTime() { return System.currentTimeMillis(); }
    @Override public void vibrate(int milliseconds) {}
    @Override public void vibrate(int milliseconds, boolean fallback) {}
    @Override public void vibrate(int milliseconds, int amplitude, boolean fallback) {}
    @Override public void vibrate(VibrationType vibrationType) {}
    @Override public void setCatchKey(int keycode, boolean catchKey) {}
    @Override public boolean isCatchKey(int keycode) { return false; }
    @Override public boolean isPeripheralAvailable(Peripheral peripheral) { return false; }
    @Override public int getRotation() { return 0; }
    @Override public Orientation getNativeOrientation() { return Orientation.Landscape; }
    @Override public void setCursorCatched(boolean catched) {}
    @Override public boolean isCursorCatched() { return false; }
    @Override public void setCursorPosition(int x, int y) {}
}
