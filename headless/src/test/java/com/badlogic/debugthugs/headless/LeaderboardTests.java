package com.badlogic.debugthugs.headless;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import com.badlogic.debugthugs.WinScreen;
import com.badlogic.debugthugs.LeaderBoardScreen;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;

import java.util.Arrays;
import java.util.HashMap;

/**
 * This headless test class is used to test the core functionality of
 * the leaderboard, this is done through using various tests, testing both
 * the data input from the WinScreen class and the data retrieval and
 * manipulation used in the LeaderboardScreen. Each test simulates one or many
 * WinScreen scenarios where a score is passed to the WinScreen, the final score
 * is calculated and then the score is added to the preference file using the
 * addScore() function. Some tests that test the data handling in the
 * LeaderBoardScreen screen create a LeaderBoardScreen instance and then uses
 * the function getScores() and sortScores() to simulate data handling.
 */
public class LeaderboardTests extends AbstractHeadlessTest {
    Preferences prefs;

    /**
     * Before each test a clean slated preference file is set, this is to ensure
     * that there is no crossover between each test ensuring compartmentalisation
     * and modularity.
     */
    @BeforeEach
    public void prefsInit() {
        prefs = Gdx.app.getPreferences("GameScores");
        prefs.clear();
        prefs.flush();
    }

    /**
     * Test to check that a score at the end of the game is saved to
     * the preferences file successfully.
     */
    @Test
    void scoreSaved() {
        //Score saved
        float winTime = 500f;
        WinScreen testWin = new WinScreen(null, winTime);
        float testScore = testWin.calcScore();
        testWin.addScore("TestName",  testScore);

        //Do the preferences include the added data?
        assertEquals(testScore, prefs.getFloat("TestName"));
    }

    /**
     * Test to check that a better score replaces a lower score of
     * the same name.
     */
    @Test
    void betterScoreReplaces() {
        //Lower score saved
        float lowerWinTime = 500f;
        WinScreen testLowerWin = new WinScreen(null, lowerWinTime);
        float lowTestScore = testLowerWin.calcScore();
        testLowerWin.addScore("TestName", lowTestScore);

        //Higher score saved
        float higherWinTime = 1000f;
        WinScreen testHigherWin = new WinScreen(null, higherWinTime);
        float highTestScore = testHigherWin.calcScore();
        testHigherWin.addScore("TestName", highTestScore);

        //Is the higher score retrieved?
        assertEquals(highTestScore, prefs.getFloat("TestName"));
    }

    /**
     * Test to check that a lower score doesn't replace a higher score
     * under the same name.
     */
    @Test
    void worseScoreNotReplace() {
        //Higher score saved
        float higherWinTime = 1000f;
        WinScreen testHigherWin = new WinScreen(null, higherWinTime);
        float highTestScore = testHigherWin.calcScore();
        testHigherWin.addScore("TestName", highTestScore);

        //Lower score saved
        float lowerWinTime = 500f;
        WinScreen testLowerWin = new WinScreen(null, lowerWinTime);
        float lowTestScore = testLowerWin.calcScore();
        testLowerWin.addScore("TestName", lowTestScore);

        //Is the higher score retrieved?
        assertEquals(highTestScore, prefs.getFloat("TestName"));
    }

    /**
     * Test to see if multiple scores saved by WinScreen are retrieved by
     * LeaderBoardScreen using getScores.
     */
    @Test
    void multipleScoresRead() {
        //Score1 saved
        float winTime1 = 500f;
        WinScreen testWin1 = new WinScreen(null, winTime1);
        float testScore1 = testWin1.calcScore();
        testWin1.addScore("TestName1",  testScore1);

        //Score2 saved
        float winTime2 = 600f;
        WinScreen testWin2 = new WinScreen(null, winTime2);
        float testScore2 = testWin2.calcScore();
        testWin2.addScore("TestName2",  testScore2);

        //Score3 saved
        float winTime3 = 700f;
        WinScreen testWin3 = new WinScreen(null, winTime3);
        float testScore3 = testWin3.calcScore();
        testWin3.addScore("TestName3",  testScore3);

        LeaderBoardScreen leaderboard = new LeaderBoardScreen(null);
        leaderboard.prefs = prefs;

        HashMap<String, Float> scores = leaderboard.getScores();

        //Do the scores saved to LeaderBoardScreen correctly match the original?
        assertEquals(testScore1, scores.get("TestName1"));
        assertEquals(testScore2, scores.get("TestName2"));
        assertEquals(testScore3, scores.get("TestName3"));
    }

    /**
     * Test to see if the scores are correctly saved by sortScores from highest
     * to lowest.
     */
    @Test
    void sortScoresHighestToLowest() {
        //Score1 saved
        float winTime1 = 600f;
        WinScreen testWin1 = new WinScreen(null, winTime1);
        float testScore1 = testWin1.calcScore();
        testWin1.addScore("TestName1",  testScore1);

        //Score2 saved
        float winTime2 = 700f;
        WinScreen testWin2 = new WinScreen(null, winTime2);
        float testScore2 = testWin2.calcScore();
        testWin2.addScore("TestName2",  testScore2);

        //Score3 saved
        float winTime3 = 500f;
        WinScreen testWin3 = new WinScreen(null, winTime3);
        float testScore3 = testWin3.calcScore();
        testWin3.addScore("TestName3",  testScore3);

        LeaderBoardScreen leaderboard = new LeaderBoardScreen(null);
        leaderboard.prefs = prefs;

        HashMap<String, Float> scores = leaderboard.getScores();
        leaderboard.sortScores(scores);

        //Is the highest score saved at top?
        assertEquals("TestName2", leaderboard.topNames[0]);
        assertEquals(testScore2, leaderboard.topScores[0]);

        //Is the second-highest score saved second?
        assertEquals("TestName1", leaderboard.topNames[1]);
        assertEquals(testScore1, leaderboard.topScores[1]);

        //Is the lowest score saved at the bottom?
        assertEquals("TestName3", leaderboard.topNames[2]);
        assertEquals(testScore3, leaderboard.topScores[2]);
    }

    /**
     * Test to check that when there are more than five scores only the top five
     * are displayed and any others are not.
     */
    @Test
    void topFiveDisplayed() {
        //Score1 saved
        float winTime1 = 500f;
        WinScreen testWin1 = new WinScreen(null, winTime1);
        float testScore1 = testWin1.calcScore();
        testWin1.addScore("TestName1",  testScore1);

        //Score2 saved
        float winTime2 = 700f;
        WinScreen testWin2 = new WinScreen(null, winTime2);
        float testScore2 = testWin2.calcScore();
        testWin2.addScore("TestName2",  testScore2);

        //Score3 saved
        float winTime3 = 400f;
        WinScreen testWin3 = new WinScreen(null, winTime3);
        float testScore3 = testWin3.calcScore();
        testWin3.addScore("TestName3",  testScore3);

        //Score4 saved
        float winTime4 = 300f;
        WinScreen testWin4 = new WinScreen(null, winTime4);
        float testScore4 = testWin4.calcScore();
        testWin4.addScore("TestName4",  testScore4);

        //Score5 saved
        float winTime5 = 900f;
        WinScreen testWin5 = new WinScreen(null, winTime5);
        float testScore5 = testWin5.calcScore();
        testWin5.addScore("TestName5",  testScore5);

        //Score6 saved
        float winTime6 = 1000f;
        WinScreen testWin6 = new WinScreen(null, winTime6);
        float testScore6 = testWin6.calcScore();
        testWin6.addScore("TestName6",  testScore6);

        LeaderBoardScreen leaderboard = new LeaderBoardScreen(null);
        leaderboard.prefs = prefs;

        HashMap<String, Float> scores = leaderboard.getScores();
        leaderboard.sortScores(scores);

        //Are only the top five highest scores displayed?
        assertTrue(Arrays.asList(leaderboard.topNames).contains("TestName1"));
        assertTrue(Arrays.asList(leaderboard.topNames).contains("TestName2"));
        assertTrue(Arrays.asList(leaderboard.topNames).contains("TestName3"));
        assertFalse(Arrays.asList(leaderboard.topNames).contains("TestName4"));
        assertTrue(Arrays.asList(leaderboard.topNames).contains("TestName5"));
        assertTrue(Arrays.asList(leaderboard.topNames).contains("TestName6"));
    }

    /**
     * Test to check that when no scores are saved to the preference file, that
     * the default placeholder values are shown instead.
     */
    @Test
    void emptyLeaderBoard() {
        LeaderBoardScreen leaderboard = new LeaderBoardScreen(null);
        leaderboard.prefs = prefs;

        HashMap<String, Float> scores = leaderboard.getScores();
        leaderboard.sortScores(scores);

        //Are the default placeholders shown instead?
        assertEquals("No Score Yet", leaderboard.topNames[0]);
        assertEquals(0f, leaderboard.topScores[0]);
    }

    /**
     * Preferences are also cleared after each test to ensure a clean slate
     * at the start of every new test.
     */
    @AfterEach
    public void prefsClose() {
        prefs.clear();
        prefs.flush();
    }

}
