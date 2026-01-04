package com.badlogic.debugthugs.headless;

import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.stream.Stream;

public class AssetTests {

    @Test
    void keyTextureExists() throws Exception {
        System.out.println("DEBUG: Current Working Directory: " + new File(".").getAbsolutePath());
        
        boolean found = false;
        try (Stream<Path> paths = Files.walk(Paths.get("."))) {
            found = paths
                .filter(Files::isRegularFile)
                .filter(p -> p.getFileName().toString().equalsIgnoreCase("Key.png"))
                .peek(p -> System.out.println("DEBUG: FOUND IT AT: " + p.toAbsolutePath()))
                .findFirst()
                .isPresent();
        }

        if (!found) {
            System.out.println("DEBUG: Not found in current dir, searching parent...");
            File parent = new File("..").getCanonicalFile();
            if (parent.exists()) {
                 try (Stream<Path> paths = Files.walk(parent.toPath())) {
                    found = paths
                        .filter(Files::isRegularFile)
                        .filter(p -> p.getFileName().toString().equalsIgnoreCase("Key.png"))
                        .peek(p -> System.out.println("DEBUG: FOUND IT IN PARENT AT: " + p.toAbsolutePath()))
                        .findFirst()
                        .isPresent();
                }
            }
        }

        assertTrue(found, "Key.png was not found anywhere in the project recursively!");
    }
}
