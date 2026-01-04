package com.badlogic.debugthugs.headless;

import org.junit.jupiter.api.Test;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.stream.Stream;
import static org.junit.jupiter.api.Assertions.fail;

public class AssetTests {

    @Test
    void debugFileSystem() throws Exception {
        System.out.println("================ FILE SYSTEM DEBUG ================");
        
        File currentDir = new File(".").getCanonicalFile();
        System.out.println("TEST RUNNING IN: " + currentDir.getAbsolutePath());

        System.out.println("\n--- Listing Files in Current Directory ---");
        try (Stream<Path> paths = Files.list(currentDir.toPath())) {
            paths.forEach(p -> System.out.println(p.getFileName()));
        }

        System.out.println("\n--- Searching for 'assets' folder ---");
        File assetsDir = new File(currentDir, "assets");
        if (assetsDir.exists()) {
            System.out.println("FOUND ASSETS FOLDER AT: " + assetsDir.getAbsolutePath());
            System.out.println("Contents:");
            try (Stream<Path> paths = Files.list(assetsDir.toPath())) {
                paths.forEach(p -> System.out.println("  " + p.getFileName()));
            }
        } else {
            System.out.println("ERROR: 'assets' folder NOT found in " + currentDir.getAbsolutePath());
            
            // Try parent
            File parentDir = currentDir.getParentFile();
            if (parentDir != null) {
                System.out.println("Checking parent: " + parentDir.getAbsolutePath());
                 File parentAssets = new File(parentDir, "assets");
                 if (parentAssets.exists()) {
                     System.out.println("Ah! Found 'assets' in parent: " + parentAssets.getAbsolutePath());
                 } else {
                     System.out.println("Not in parent either.");
                 }
            }
        }
        
        System.out.println("================ END DEBUG ================");
        fail("Failing test intentionally to see logs.");
    }
}
