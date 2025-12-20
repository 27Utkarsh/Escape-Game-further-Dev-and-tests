package com.badlogic.debugthugs.headless;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import com.badlogic.debugthugs.WinScreen;
import com.badlogic.debugthugs.LeaderBoardScreen;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;

import java.util.HashMap;

public class LeaderboardTests extends AbstractHeadlessTest {
    Preferences prefs;

    @BeforeEach
    public void prefsInit() {
        prefs = Gdx.app.getPreferences("GameScores");
        prefs.clear();
        prefs.flush();
    }

    @Test
    void scoreSaved() {
        float winTime = 500f;
        WinScreen testWin = new WinScreen(null, winTime);
        float testScore = testWin.calcScore();

        testWin.addScore("TestName",  testScore);
        assertEquals(testScore, prefs.getFloat("TestName"));
    }

    @Test
    void betterScoreReplaces() {
        float lowerWinTime = 500f;
        WinScreen testLowerWin = new WinScreen(null, lowerWinTime);
        float lowTestScore = testLowerWin.calcScore();
        testLowerWin.addScore("TestName", lowTestScore);

        float higherWinTime = 1000f;
        WinScreen testHigherWin = new WinScreen(null, higherWinTime);
        float highTestScore = testHigherWin.calcScore();
        testHigherWin.addScore("TestName", highTestScore);

        assertEquals(highTestScore, prefs.getFloat("TestName"));
    }

    @Test
    void worseScoreNotReplace() {
        float higherWinTime = 1000f;
        WinScreen testHigherWin = new WinScreen(null, higherWinTime);
        float highTestScore = testHigherWin.calcScore();
        testHigherWin.addScore("TestName", highTestScore);

        float lowerWinTime = 500f;
        WinScreen testLowerWin = new WinScreen(null, lowerWinTime);
        float lowTestScore = testLowerWin.calcScore();
        testLowerWin.addScore("TestName", lowTestScore);

        assertEquals(highTestScore, prefs.getFloat("TestName"));
    }

    @Test
    void multipleScoresRead() {
        float winTime1 = 500f;
        WinScreen testWin1 = new WinScreen(null, winTime1);
        float testScore1 = testWin1.calcScore();
        testWin1.addScore("TestName1",  testScore1);

        float winTime2 = 600f;
        WinScreen testWin2 = new WinScreen(null, winTime2);
        float testScore2 = testWin2.calcScore();
        testWin2.addScore("TestName2",  testScore2);

        float winTime3 = 700f;
        WinScreen testWin3 = new WinScreen(null, winTime3);
        float testScore3 = testWin3.calcScore();
        testWin3.addScore("TestName3",  testScore3);

        LeaderBoardScreen leaderboard = new LeaderBoardScreen(null);
        leaderboard.prefs = prefs;

        HashMap<String, Float> scores = leaderboard.getScores();

        assertEquals(testScore1, scores.get("TestName1"));
        assertEquals(testScore2, scores.get("TestName2"));
        assertEquals(testScore3, scores.get("TestName3"));
    }

    @Test
    void sortScoresHighestToLowest() {
        float winTime1 = 500f;
        WinScreen testWin1 = new WinScreen(null, winTime1);
        float testScore1 = testWin1.calcScore();
        testWin1.addScore("TestName1",  testScore1);

        float winTime2 = 600f;
        WinScreen testWin2 = new WinScreen(null, winTime2);
        float testScore2 = testWin2.calcScore();
        testWin2.addScore("TestName2",  testScore2);

        float winTime3 = 700f;
        WinScreen testWin3 = new WinScreen(null, winTime3);
        float testScore3 = testWin3.calcScore();
        testWin3.addScore("TestName3",  testScore3);

        LeaderBoardScreen leaderboard = new LeaderBoardScreen(null);
        leaderboard.prefs = prefs;

        HashMap<String, Float> scores = leaderboard.getScores();
        leaderboard.sortScores(scores);

        assertEquals("TestName3", leaderboard.topNames[0]);
        assertEquals(testScore3, leaderboard.topScores[0]);

        assertEquals("TestName2", leaderboard.topNames[1]);
        assertEquals(testScore2, leaderboard.topScores[1]);

        assertEquals("TestName1", leaderboard.topNames[2]);
        assertEquals(testScore1, leaderboard.topScores[2]);
    }

    @Test
    void emptyLeaderBoard() {
        LeaderBoardScreen leaderboard = new LeaderBoardScreen(null);
        leaderboard.prefs = prefs;

        HashMap<String, Float> scores = leaderboard.getScores();
        leaderboard.sortScores(scores);

        assertEquals("No Score Yet", leaderboard.topNames[0]);
        assertEquals(0f, leaderboard.topScores[0]);
    }

    @AfterEach
    public void prefsClose() {
        prefs.clear();
        prefs.flush();
    }

}
