package com.badlogic.debugthugs.headless;

import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;
import com.badlogic.gdx.Gdx;
import java.io.BufferedReader;
import java.io.InputStreamReader;

public class AssetTests extends AbstractHeadlessTest {

    @Test
    void testAllAssetsFromTxtFile() throws Exception {
        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(Gdx.files.internal("assets.txt").read()))) {
            
            String line;
            while ((line = reader.readLine()) != null) {
                line = line.trim();
                if (!line.isEmpty() && !line.startsWith("#")) {
                    assertTrue(Gdx.files.internal(line).exists(), 
                        "Asset not found: " + line);
                }
            }
        }
    }
}
