package com.badlogic.debugthugs.headless;

import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;

import com.badlogic.gdx.Gdx;

public class AssetTests extends AbstractHeadlessTest {
    @Test
    void keyTextureExists() {
        assertTrue(Gdx.files.internal("Key.png").exists());
    }
}
