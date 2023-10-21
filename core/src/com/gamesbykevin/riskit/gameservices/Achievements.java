package com.gamesbykevin.riskit.gameservices;

import com.gamesbykevin.riskit.MyGdxGame;
import com.gamesbykevin.riskit.board.Board;
import com.gamesbykevin.riskit.player.Players;
import com.gamesbykevin.riskit.preferences.MyPreferences;
import com.gamesbykevin.riskit.screen.GameScreen;
import com.gamesbykevin.riskit.screen.ParentScreen;
import com.gamesbykevin.riskit.screen.Settings;
import com.gamesbykevin.riskit.util.GameScreenHelper;

import de.golfgl.gdxgamesvcs.IGameServiceClient;

public class Achievements {

    public static final String WIN_EASY_2_PLAYERS = "CgkI7sGf_pANEAIQAQ";
    public static final String WIN_EASY_3_PLAYERS = "CgkI7sGf_pANEAIQAg";
    public static final String WIN_EASY_4_PLAYERS = "CgkI7sGf_pANEAIQAw";
    public static final String WIN_EASY_5_PLAYERS = "CgkI7sGf_pANEAIQBA";
    public static final String WIN_EASY_6_PLAYERS = "CgkI7sGf_pANEAIQBQ";
    public static final String WIN_EASY_7_PLAYERS = "CgkI7sGf_pANEAIQBg";
    public static final String WIN_EASY_8_PLAYERS = "CgkI7sGf_pANEAIQBw";

    public static final String WIN_MEDIUM_2_PLAYERS = "CgkI7sGf_pANEAIQCA";
    public static final String WIN_MEDIUM_3_PLAYERS = "CgkI7sGf_pANEAIQCQ";
    public static final String WIN_MEDIUM_4_PLAYERS = "CgkI7sGf_pANEAIQCg";
    public static final String WIN_MEDIUM_5_PLAYERS = "CgkI7sGf_pANEAIQCw";
    public static final String WIN_MEDIUM_6_PLAYERS = "CgkI7sGf_pANEAIQDA";
    public static final String WIN_MEDIUM_7_PLAYERS = "CgkI7sGf_pANEAIQDQ";
    public static final String WIN_MEDIUM_8_PLAYERS = "CgkI7sGf_pANEAIQDg";

    public static final String WIN_HARD_2_PLAYERS = "CgkI7sGf_pANEAIQDw";
    public static final String WIN_HARD_3_PLAYERS = "CgkI7sGf_pANEAIQEA";
    public static final String WIN_HARD_4_PLAYERS = "CgkI7sGf_pANEAIQEQ";
    public static final String WIN_HARD_5_PLAYERS = "CgkI7sGf_pANEAIQEg";
    public static final String WIN_HARD_6_PLAYERS = "CgkI7sGf_pANEAIQEw";
    public static final String WIN_HARD_7_PLAYERS = "CgkI7sGf_pANEAIQFA";
    public static final String WIN_HARD_8_PLAYERS = "CgkI7sGf_pANEAIQFQ";

    public static final String WIN_LARGE_8_PLAYERS = "CgkI7sGf_pANEAIQFg";
    public static final String LOSE_SPECTATE = "CgkI7sGf_pANEAIQFw";

    public static final String LOSE_1ST_GAME = "CgkI7sGf_pANEAIQGA";
    public static final String TERRITORY_UNDERDOG = "CgkI7sGf_pANEAIQGQ";
    public static final String CONTROL_ALL = "CgkI7sGf_pANEAIQGg";
    public static final String FULL_HOUSE = "CgkI7sGf_pANEAIQGw";
    public static final String GOLIATH_LOST = "CgkI7sGf_pANEAIQHA";
    public static final String NICE_GUY = "CgkI7sGf_pANEAIQHQ";

    //called when the game has finished
    public static void update(MyGdxGame game) {

        String achievementId = null;

        GameScreen screen = (GameScreen)game.getScreen(ParentScreen.Screens.GameScreen);
        Board board = screen.getBoard();
        IGameServiceClient client = game.getGameServiceClient();

        if (!client.isSessionActive())
            return;

        boolean difficultyEasy = MyPreferences.getInt(MyPreferences.PREFS_DIFFICULTY) == Settings.DIFFICULTY_EASY;
        boolean difficultyMedium = MyPreferences.getInt(MyPreferences.PREFS_DIFFICULTY) == Settings.DIFFICULTY_MEDIUM;
        boolean difficultyHard = MyPreferences.getInt(MyPreferences.PREFS_DIFFICULTY) == Settings.DIFFICULTY_HARD;

        boolean sizeLarge = board.getCols() == GameScreenHelper.LARGE_COLS && board.getRows() == GameScreenHelper.LARGE_ROWS;
        boolean won = !screen.getPlayers().isHumansDead();

        if (won) {

            //if the computer went first and we still won
            if (!screen.getPlayers().isHumanFirst())
                unlock(client, NICE_GUY);

            switch (screen.getPlayers().getPlayerCount()) {
                case 2:
                    if (difficultyEasy) {
                        achievementId = WIN_EASY_2_PLAYERS;
                    } else if (difficultyMedium) {
                        achievementId = WIN_MEDIUM_2_PLAYERS;
                    } else if (difficultyHard) {
                        achievementId = WIN_HARD_2_PLAYERS;
                    }
                    break;
                case 3:
                    if (difficultyEasy) {
                        achievementId = WIN_EASY_3_PLAYERS;
                    } else if (difficultyMedium) {
                        achievementId = WIN_MEDIUM_3_PLAYERS;
                    } else if (difficultyHard) {
                        achievementId = WIN_HARD_3_PLAYERS;
                    }
                    break;
                case 4:
                    if (difficultyEasy) {
                        achievementId = WIN_EASY_4_PLAYERS;
                    } else if (difficultyMedium) {
                        achievementId = WIN_MEDIUM_4_PLAYERS;
                    } else if (difficultyHard) {
                        achievementId = WIN_HARD_4_PLAYERS;
                    }
                    break;
                case 5:
                    if (difficultyEasy) {
                        achievementId = WIN_EASY_5_PLAYERS;
                    } else if (difficultyMedium) {
                        achievementId = WIN_MEDIUM_5_PLAYERS;
                    } else if (difficultyHard) {
                        achievementId = WIN_HARD_5_PLAYERS;
                    }
                    break;
                case 6:
                    if (difficultyEasy) {
                        achievementId = WIN_EASY_6_PLAYERS;
                    } else if (difficultyMedium) {
                        achievementId = WIN_MEDIUM_6_PLAYERS;
                    } else if (difficultyHard) {
                        achievementId = WIN_HARD_6_PLAYERS;
                    }
                    break;
                case 7:
                    if (difficultyEasy) {
                        achievementId = WIN_EASY_7_PLAYERS;
                    } else if (difficultyMedium) {
                        achievementId = WIN_MEDIUM_7_PLAYERS;
                    } else if (difficultyHard) {
                        achievementId = WIN_HARD_7_PLAYERS;
                    }
                    break;
                case 8:
                    if (difficultyEasy) {
                        achievementId = WIN_EASY_8_PLAYERS;
                    } else if (difficultyMedium) {
                        achievementId = WIN_MEDIUM_8_PLAYERS;
                    } else if (difficultyHard) {
                        achievementId = WIN_HARD_8_PLAYERS;
                    }
                    break;
            }
        }

        unlock(client, achievementId);

        if (won && sizeLarge && screen.getPlayers().getPlayerCount() == 8)
            unlock(client, WIN_LARGE_8_PLAYERS);

        if (!won)
            unlock(client, LOSE_1ST_GAME);

        if (!won && screen.getPlayers().getPlayerCount() > 2)
            unlock(client, LOSE_SPECTATE);
    }

    public static void unlock(IGameServiceClient client, String achievementId) {

        if (achievementId == null)
            return;
        if (!client.isSessionActive())
            return;

        try {
            client.unlockAchievement(achievementId);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
