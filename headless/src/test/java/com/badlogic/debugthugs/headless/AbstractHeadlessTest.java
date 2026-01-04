package com.badlogic.debugthugs.headless;

import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.backends.headless.HeadlessApplication;
import com.badlogic.gdx.backends.headless.HeadlessApplicationConfiguration;
import com.badlogic.gdx.graphics.GL20;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.mockito.Mockito;

import static org.mockito.ArgumentMatchers.anyInt;

public abstract class AbstractHeadlessTest {
    private HeadlessApplication application;

    @BeforeEach
    public void setup() {
        // Mock OpenGL interface to simulate graphics context
        Gdx.gl = Gdx.gl20 = Mockito.mock(GL20.class);

        // Configure shader compilation mocks to return success status
        Mockito.when(Gdx.gl.glCreateShader(anyInt())).thenReturn(1);
        Mockito.when(Gdx.gl.glCompileShader(anyInt())).thenAnswer(i -> {}); 
        Mockito.when(Gdx.gl.glGetShaderiv(anyInt(), anyInt(), Mockito.any())).thenAnswer(invocation -> {
            java.nio.IntBuffer buffer = invocation.getArgument(2);
            buffer.put(0, 1); 
            return null;
        });

        HeadlessApplicationConfiguration config = new HeadlessApplicationConfiguration();
        config.updatesPerSecond = -1; // Disable update loop for unit tests

        // Initialize headless backend with empty listener
        application = new HeadlessApplication(new ApplicationListener() {
            @Override public void create() {}
            @Override public void resize(int width, int height) {}
            @Override public void render() {}
            @Override public void pause() {}
            @Override public void resume() {}
            @Override public void dispose() {}
        }, config);
    }

    @AfterEach
    public void tearDown() {
        if (application != null) {
            application.exit();
            application = null;
        }
    }
}
