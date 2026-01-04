package com.badlogic.debugthugs.headless;

import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;

import com.badlogic.gdx.Gdx;
import java.io.File;
public class AssetTests extends AbstractHeadlessTest {
    @Test
    void keyTextureExists() {
        // Log current working directory to help debugging
        System.out.println("Working Directory: " + new File(".").getAbsolutePath());

        // Check for the file in the most likely locations
        boolean exists = Gdx.files.internal("Key.png").exists() 
                      || Gdx.files.internal("assets/Key.png").exists()
                      || Gdx.files.internal("../assets/Key.png").exists();

        assertTrue(exists, "Key.png should exist (checked root, assets/, and ../assets/)");
    }
}
