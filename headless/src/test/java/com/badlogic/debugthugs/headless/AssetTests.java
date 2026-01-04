package com.badlogic.debugthugs.headless;

import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;
import java.io.File;

public class AssetTests {

    @Test
    void keyTextureExists() {
        // Define potential asset roots to check
        String[] paths = { ".", "assets", "../assets", "core/assets", "../core/assets" };
        
        boolean found = false;
        
        System.out.println("DEBUG: Starting search for Key.png...");
        
        for (String path : paths) {
            File dir = new File(path);
            if (dir.exists() && dir.isDirectory()) {
                System.out.println("DEBUG: Scanning directory: " + dir.getAbsolutePath());
                File[] files = dir.listFiles();
                if (files != null) {
                    for (File f : files) {
                        // Case-insensitive check to handle Linux/Windows Git renaming issues
                        if (f.getName().equalsIgnoreCase("Key.png")) {
                            System.out.println("DEBUG: FOUND IT! " + f.getAbsolutePath());
                            found = true;
                            break;
                        }
                    }
                }
            }
            if (found) break;
        }

        assertTrue(found, "Key.png was not found in any standard asset folder (checked . , assets, ../assets)");
    }
}
