package com.badlogic.debugthugs.headless;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.assets.loaders.resolvers.InternalFileHandleResolver;
import com.badlogic.gdx.backends.headless.HeadlessApplication;
import com.badlogic.gdx.backends.headless.HeadlessApplicationConfiguration;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

public class AssetTests {

    private static AssetManager assetManager;

    @BeforeAll
    static void init() {
        // required to make Gdx.files work in a test environment
        HeadlessApplicationConfiguration config = new HeadlessApplicationConfiguration();
        new HeadlessApplication(new ApplicationAdapter() {}, config);

        assetManager = new AssetManager();
        assetManager.setLoader(TiledMap.class, new TmxMapLoader(new InternalFileHandleResolver()));
    }

    @Test
    void testAllAssetsFromTxtFile() {
        FileHandle listFile = Gdx.files.internal("assets/assets.txt");
        
        if (!listFile.exists()) {
            fail("assets.txt missing, cannot verify assets");
        }

        System.out.println("Verifying assets from: " + listFile.path());
        
        List<String> failedAssets = new ArrayList<>();
        int loadedCount = 0;

        try (BufferedReader reader = new BufferedReader(listFile.reader())) {
            String line;
            while ((line = reader.readLine()) != null) {
                line = line.trim();
                // ignore git files or comments
                if (line.isEmpty() || line.startsWith("#") || line.equals(".gitkeep")) continue; 

                // assets.txt paths are relative to the assets folder
                String fullPath = "assets/" + line; 
                
                // verify file exists on disk first
                if (!Gdx.files.internal(fullPath).exists()) {
                    System.err.println("MISSING: " + fullPath);
                    failedAssets.add(fullPath + " (File not found)");
                    continue;
                }

                // queue loadable resources
                if (line.endsWith(".png") || line.endsWith(".jpg")) {
                    assetManager.load(fullPath, Texture.class);
                } else if (line.endsWith(".tmx")) {
                    assetManager.load(fullPath, TiledMap.class);
                } else if (line.endsWith(".json") && line.contains("skin")) {
                    assetManager.load(fullPath, Skin.class);
                } else {
                    // skip audio (crashes headless backend) and config files
                    System.out.println("Skipping load check: " + line);
                    continue; 
                }
                
                loadedCount++;
            }
        } catch (IOException e) {
            fail("Error reading assets.txt: " + e.getMessage());
        }

        // blocking load to catch corrupt files
        try {
            System.out.println("Loading " + loadedCount + " assets...");
            assetManager.finishLoading();
        } catch (Exception e) {
            failedAssets.add("AssetManager error: " + e.getMessage());
        }

        // report failures
        if (!failedAssets.isEmpty()) {
            fail("Asset verification failed:\n" + String.join("\n", failedAssets));
        }
        
        assertTrue(true);
    }
}
