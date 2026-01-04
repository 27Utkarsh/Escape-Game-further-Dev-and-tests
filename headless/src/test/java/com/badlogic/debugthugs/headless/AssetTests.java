package com.badlogic.debugthugs.headless;

import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;
import java.io.File;

public class AssetTests {

    @Test
    void keyTextureExists() {
        
        File fileAtRoot = new File("assets/Key.png");

        System.out.println("DEBUG: Checking path: " + fileAtRoot.getAbsolutePath());

        boolean found = fileAtRoot.exists();

        assertTrue(found, "Key.png not found! Checked 'assets/Key.png'");
    }
}
