package com.gamesbykevin.riskit.preferences;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;
import com.gamesbykevin.riskit.board.Board;
import com.gamesbykevin.riskit.player.Computer;
import com.gamesbykevin.riskit.player.Human;
import com.gamesbykevin.riskit.player.Player;
import com.gamesbykevin.riskit.player.Players;
import com.gamesbykevin.riskit.screen.GameScreen;
import com.gamesbykevin.riskit.territory.Territories;
import com.gamesbykevin.riskit.territory.Territory;

public class MyPreferences {

    public static final String PREFS_NAME = "RiskItPreferences";

    public static final String PREFS_LANGUAGE = "language";
    public static final String PREFS_DIFFICULTY = "difficulty";
    public static final String PREFS_PLAYERS = "players";
    public static final String PREFS_SIZE = "size";
    public static final String PREFS_VIBRATE = "vibrate";
    public static final String PREFS_ORDER_HUMAN = "order";

    public static final String PREFS_SAVED_PLAYERS_CURRENT_ID = "playersCurrentId";
    public static final String PREFS_SAVED_PLAYERS_HUMANFIRST = "playersHumanFirst";
    public static final String PREFS_SAVED_PLAYERS_THINKING = "playersIsThinking";
    public static final String PREFS_SAVED_PLAYERS_ARRAY = "playersData";

    public static final String PREFS_SAVED_BOARD_SIZE = "savedBoardSize";
    public static final String PREFS_SAVED_BOARD_COLS = "savedBoardCols";
    public static final String PREFS_SAVED_BOARD_ROWS = "savedBoardRows";
    public static final String PREFS_SAVED_BOARD_SEED = "savedBoardSeed";
    public static final String PREFS_SAVED_BOARD_TERR = "savedBoardTerr";

    public static final String PREFS_AUDIO_ENABLED = "audio";
    private static Preferences PREFERENCES = null;

    //allowed string length
    private static final int PREFS_CHAR_LIMIT = 1000;

    private static final String SPLIT_ARRAY = ";";
    private static final String SPLIT_ELEMENT = ",";

    private static Preferences getPreferences() {
        if (PREFERENCES == null)
            PREFERENCES = Gdx.app.getPreferences(PREFS_NAME);

        return PREFERENCES;
    }

    public static final boolean hasSavedGame() {
        return (hasPreference(PREFS_SAVED_PLAYERS_ARRAY + "-0") || hasPreference(PREFS_SAVED_BOARD_TERR + "-0") ||
                hasPreference(PREFS_SAVED_PLAYERS_ARRAY) || hasPreference(PREFS_SAVED_BOARD_TERR));
    }

    public static final void removeSave() {
        removeString(PREFS_SAVED_PLAYERS_ARRAY);
        remove(PREFS_SAVED_PLAYERS_CURRENT_ID);
        remove(PREFS_SAVED_PLAYERS_HUMANFIRST);
        remove(PREFS_SAVED_PLAYERS_THINKING);

        removeString(PREFS_SAVED_BOARD_TERR);
        remove(PREFS_SAVED_BOARD_SIZE);
        remove(PREFS_SAVED_BOARD_COLS);
        remove(PREFS_SAVED_BOARD_ROWS);
        remove(PREFS_SAVED_BOARD_SEED);
    }

    private static void removeString(String key) {

        int index = 0;
        String uniqueKey = key + "-" + index;

        //continue as long as we have the unique key
        while (hasPreference(uniqueKey)) {
            remove(uniqueKey);
            index++;
            uniqueKey = key + "-" + index;
        }
    }

    private static void remove(String key) {
        Gdx.app.log("MyPreferences", "removing: " + key);
        getPreferences().remove(key);
        commit();
    }

    public static final void saveGame(GameScreen screen) {

        try {

            //remove existing save
            removeSave();

            Gdx.app.log("MyPreferences", "Saving players");

            Players players = screen.getPlayers();

            Gdx.app.debug("MyPreferences", "players currentId   = " + getInt(PREFS_SAVED_PLAYERS_CURRENT_ID));
            Gdx.app.debug("MyPreferences", "players is thinking = " + isBoolean(PREFS_SAVED_PLAYERS_THINKING));
            Gdx.app.debug("MyPreferences", "players human first = " + isBoolean(PREFS_SAVED_PLAYERS_HUMANFIRST));
            saveInt(PREFS_SAVED_PLAYERS_CURRENT_ID,     players.getCurrentId());
            saveBoolean(PREFS_SAVED_PLAYERS_THINKING,   players.isThinking());
            saveBoolean(PREFS_SAVED_PLAYERS_HUMANFIRST, players.isHumanFirst());

            String data = "";

            for (Player player : players.getPlayers()) {

                if (data.length() > 0)
                    data += SPLIT_ARRAY;

                data += player.getId() + SPLIT_ELEMENT + player.isHuman() + SPLIT_ELEMENT + player.isDead() + SPLIT_ELEMENT +
                        player.getOrder() + SPLIT_ELEMENT + player.getRemaining() + SPLIT_ELEMENT + player.getConsecutiveCount() + SPLIT_ELEMENT +
                        player.getColorIndex() + SPLIT_ELEMENT + player.isSelected();
            }

            Gdx.app.debug("MyPreferences", data);
            saveString(PREFS_SAVED_PLAYERS_ARRAY, data);
            Gdx.app.log("MyPreferences", "Players saved");

            Board board = screen.getBoard();

            Gdx.app.log("MyPreferences", "Saving board attributes");

            saveInt(PREFS_SAVED_BOARD_SIZE, getInt(PREFS_SIZE));
            saveInt(PREFS_SAVED_BOARD_COLS, board.getCols());
            saveInt(PREFS_SAVED_BOARD_ROWS, board.getRows());
            saveInt(PREFS_SAVED_BOARD_SEED, board.getSeed());

            Gdx.app.debug("MyPreferences", "board size = " + getInt(PREFS_SAVED_BOARD_SIZE));
            Gdx.app.debug("MyPreferences", "board cols = " + getInt(PREFS_SAVED_BOARD_COLS));
            Gdx.app.debug("MyPreferences", "board rows = " + getInt(PREFS_SAVED_BOARD_ROWS));
            Gdx.app.debug("MyPreferences", "board seed = " + getInt(PREFS_SAVED_BOARD_SEED));

            Territories territories = board.getTerritories();

            data = "";

            for (Territory territory: territories.getTerritoriesList()) {

                if (territory == null)
                    continue;

                if (data.length() > 0)
                    data += SPLIT_ARRAY;

                data += territory.getId() + SPLIT_ELEMENT + territory.getConsecutiveCount() + SPLIT_ELEMENT +
                        territory.getPlayerId() + SPLIT_ELEMENT + territory.isSelected() + SPLIT_ELEMENT +
                        territory.getDice() + SPLIT_ELEMENT + territory.getDiceX() + SPLIT_ELEMENT +
                        territory.getDiceY() + SPLIT_ELEMENT + territory.isChecked();
            }

            Gdx.app.debug("MyPreferences", data);
            saveString(PREFS_SAVED_BOARD_TERR, data);
            Gdx.app.log("MyPreferences", "Territories saved");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static final void loadGame(GameScreen screen) {

        Gdx.app.log("MyPreferences", "Loading players");
        int currentId = getInt(PREFS_SAVED_PLAYERS_CURRENT_ID);
        boolean thinking = isBoolean(PREFS_SAVED_PLAYERS_THINKING);
        boolean humanFirst = isBoolean(PREFS_SAVED_PLAYERS_HUMANFIRST);

        Gdx.app.debug("MyPreferences", "players current     = " + currentId);
        Gdx.app.debug("MyPreferences", "players is thinking = " + thinking);
        Gdx.app.debug("MyPreferences", "players human first = " + humanFirst);

        String data = getString(PREFS_SAVED_PLAYERS_ARRAY);
        Gdx.app.debug("MyPreferences", data);

        String[] tmp1 = data.split(SPLIT_ARRAY);

        Player[] tmpPlayerArray = new Player[tmp1.length];

        for (int i = 0; i < tmp1.length; i++) {
            String[] tmp2 = tmp1[i].split(SPLIT_ELEMENT);
            final int id = Integer.parseInt(tmp2[0]);
            final boolean human = Boolean.parseBoolean(tmp2[1]);
            final boolean dead = Boolean.parseBoolean(tmp2[2]);
            final int order = Integer.parseInt(tmp2[3]);
            final int remaining = Integer.parseInt(tmp2[4]);
            final int consecutiveCount = Integer.parseInt(tmp2[5]);
            final int colorIndex = Integer.parseInt(tmp2[6]);
            final boolean selected = Boolean.parseBoolean(tmp2[7]);

            Player player;

            if (human) {
                player = new Human(colorIndex, id);
            } else {
                player = new Computer(colorIndex, id);
            }

            player.setDead(dead);
            player.setOrder(order);
            player.setRemaining(remaining);
            player.setConsecutiveCount(consecutiveCount);
            player.setSelected(selected);
            tmpPlayerArray[id] = player;
        }

        Players players = new Players(tmpPlayerArray);
        players.setCurrentId(currentId);
        players.setThinking(thinking);
        players.setHumanFirst(humanFirst);

        Gdx.app.log("MyPreferences", "Players loaded");

        //assign players
        screen.setPlayers(players);

        Gdx.app.log("MyPreferences", "Loading board attributes");

        //get saved board size
        int value = getInt(PREFS_SAVED_BOARD_SIZE);
        saveInt(MyPreferences.PREFS_SIZE, value);

        int cols = getInt(PREFS_SAVED_BOARD_COLS);
        int rows = getInt(PREFS_SAVED_BOARD_ROWS);
        int seed = getInt(PREFS_SAVED_BOARD_SEED);

        Gdx.app.debug("MyPreferences", "board size = " + value);
        Gdx.app.debug("MyPreferences", "board cols = " + cols);
        Gdx.app.debug("MyPreferences", "board rows = " + rows);
        Gdx.app.debug("MyPreferences", "board seed = " + seed);

        Board board = new Board(cols, rows, seed);

        data = getString(PREFS_SAVED_BOARD_TERR);
        Gdx.app.debug("MyPreferences", data);

        tmp1 = data.split(SPLIT_ARRAY);
        for (int i = 0; i < tmp1.length; i++) {
            String[] tmp2 = tmp1[i].split(SPLIT_ELEMENT);
            final int id = Integer.parseInt(tmp2[0]);
            final int consecutiveCount = Integer.parseInt(tmp2[1]);
            final int playerId = Integer.parseInt(tmp2[2]);
            final boolean selected = Boolean.parseBoolean(tmp2[3]);
            final int dice = Integer.parseInt(tmp2[4]);
            final float diceX = Float.parseFloat(tmp2[5]);
            final float diceY = Float.parseFloat(tmp2[6]);
            final boolean checked = Boolean.parseBoolean(tmp2[7]);

            Territory territory = board.getTerritories().getTerritoryById(id);
            territory.setConsecutiveCount(consecutiveCount);
            territory.setPlayerId(playerId);
            territory.setSelected(selected);
            territory.setDice(dice);
            territory.setDiceX(diceX);
            territory.setDiceY(diceY);
            territory.setChecked(checked);
        }

        Gdx.app.log("MyPreferences", "Board loaded");

        //assign board
        screen.setBoard(board);

        //flag that we are resuming
        screen.getPlayers().getStatus().setResume(true);

        //if loading a game where all the human players are already dead
        if (players.isHumansDead()) {

            //don't prompt if we want to continue
            screen.getDialogs().setPromptContinue(true);
        }

        //make sure other components are setup
        screen.setupUI();
    }

    public static final int getInt(String name) {
        return getInt(name, 0);
    }

    public static final int getInt(String name, int defaultValue) {
        return getPreferences().getInteger(name, defaultValue);
    }

    public static final void saveInt(String name, int value) {
        getPreferences().putInteger(name, value);
        commit();
    }

    public static final String getString(String key) {

        String data = "";

        int index = 0;
        String uniqueKey = key + "-" + index;

        //continue as long as we have the unique key
        while (hasPreference(uniqueKey)) {
            Gdx.app.debug("MyPreferences", uniqueKey);
            String tmp = getPreferences().getString(uniqueKey, null);
            data += tmp;
            Gdx.app.debug("MyPreferences", tmp);
            index++;
            uniqueKey = key + "-" + index;
        }

        return data;
    }

    public static final void saveString(String key, String value) {

        int count = 0;
        int index = 0;

        while (index <= value.length()) {

            String tmp;

            if (index + PREFS_CHAR_LIMIT >= value.length()) {
                tmp = value.substring(index);
            } else {
                tmp = value.substring(index, index + PREFS_CHAR_LIMIT);
            }

            String uniqueKey = key + "-" + count;
            Gdx.app.debug(uniqueKey, tmp);
            getPreferences().putString(uniqueKey, tmp);
            count++;
            index += PREFS_CHAR_LIMIT;
        }

        commit();
    }

    public static final boolean isBoolean(String name) {
        return isBoolean(name, true);
    }

    public static final boolean isBoolean(String name, boolean defaultValue) {
        return getPreferences().getBoolean(name, defaultValue);
    }

    public static final void saveBoolean(String name, boolean value) {
        getPreferences().putBoolean(name, value);
        commit();
    }

    private static void commit() {
        getPreferences().flush();
    }

    public static final boolean hasPreference(String name) {
        return getPreferences().contains(name);
    }
}