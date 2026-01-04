package com.badlogic.debugthugs.headless;

import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;
import com.badlogic.gdx.Gdx;
import java.io.File;

public class AssetTests extends AbstractHeadlessTest {

    @Test
    void keyTextureExists() {
        //Print current directory so you can see where you are in the logs
        System.out.println("DEBUG: Working Directory: " + new File(".").getAbsolutePath());

        //Check all possible path combinations
        boolean exists = false;
        
        // Check "Key.png" (Capital K)
        if (Gdx.files.internal("Key.png").exists()) exists = true;
        if (Gdx.files.internal("assets/Key.png").exists()) exists = true;
        if (Gdx.files.internal("../assets/Key.png").exists()) exists = true;

        // Check "key.png" (Lowercase k)
        if (Gdx.files.internal("key.png").exists()) exists = true;
        if (Gdx.files.internal("assets/key.png").exists()) exists = true;
        if (Gdx.files.internal("../assets/key.png").exists()) exists = true;

        assertTrue(exists, "Could not find Key.png (or key.png) in root, assets/, or ../assets/");
    }
}
