package com.badlogic.debugthugs.headless;

import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;

import com.badlogic.gdx.Gdx;

public class AssetTests extends AbstractHeadlessTest {
    @Test
    void keyTextureExists() {
    boolean exists = Gdx.files.internal("Key.png").exists() 
                  || Gdx.files.internal("assets/Key.png").exists()
                  || Gdx.files.internal("../assets/Key.png").exists();
    
    assertTrue(exists, "Key.png not found in root, assets/, or ../assets/");
    }
}
