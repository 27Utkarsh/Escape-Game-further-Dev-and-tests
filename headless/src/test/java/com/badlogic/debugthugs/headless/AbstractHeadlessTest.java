package com.badlogic.debugthugs.headless;

import org.junit.jupiter.api.BeforeEach;
import static org.mockito.Mockito.mock;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;

/**
 * Extend from this class when you want to use the LibGDX HeadlessApplication (no rendering) for testing.
 */
public abstract class AbstractHeadlessTest {
    @BeforeEach
    public void setup() {
        Gdx.gl = Gdx.gl20 = mock(GL20.class);
        HeadlessLauncher.main(new String[0]);
    }
}
