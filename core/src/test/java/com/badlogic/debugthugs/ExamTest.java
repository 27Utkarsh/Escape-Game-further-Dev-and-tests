package com.badlogic.debugthugs;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.backends.headless.HeadlessApplication;
import com.badlogic.gdx.backends.headless.HeadlessApplicationConfiguration;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class ExamTest {

    @BeforeAll
    public static void init() {
        if (Gdx.files == null) {
            HeadlessApplicationConfiguration config = new HeadlessApplicationConfiguration();
            new HeadlessApplication(new com.badlogic.gdx.ApplicationAdapter() {}, config);
        }
    }

    //Test if exam is initialized with correct position and size
    @Test
    void ExamInitialization() {
        
        Exam exam = new Exam(1184f, 1000f, 200f);

        assertEquals(1184f, exam.bounds.x, 0.0001f);
        assertEquals(1000f, exam.bounds.y, 0.0001f);
        assertEquals(50f, exam.bounds.width, 0.0001f);
        assertEquals(50f, exam.bounds.height, 0.0001f);
    }

    //Test if encountering Exam increments bad event by one
    @Test
    void IncreaseCounter() {

        Exam exam = new Exam(1184f, 1500f, 100f);

        Player player = new Player(
                1184f, 1500f,   
                null, null  
        );

        player.playerWidth = 50f;
        player.playerHeight = 50f;
        player.badEvent = 0;

        exam.checkCollided(player);

        //Test if badEvent counter gets incremented
        assertEquals(1, player.badEvent,
                "badEvent should increment by 1 when player collides with exam");

        //Ensure exam only triggers once
        exam.checkCollided(player);
        assertEquals(1, player.badEvent,
                "badEvent should not increment again once exam is completed");
    }

    //Check if Exam object patrols correctly
    @Test
    void ExamPatrols() {
        Exam exam = new Exam(1184f, 1000f, 100f);

        exam.update(2f);
        assertEquals(1100f, exam.bounds.y, 0.0001f);

        exam.update(1f);
        assertEquals(1040f, exam.bounds.y, 0.0001f);

        exam.update(2f); 
        assertEquals(1000f, exam.bounds.y, 0.0001f);
    }
}
