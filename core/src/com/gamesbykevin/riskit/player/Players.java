package com.gamesbykevin.riskit.player;

import com.badlogic.gdx.utils.Json;
import com.gamesbykevin.riskit.board.Board;
import com.gamesbykevin.riskit.player.status.Status;
import com.gamesbykevin.riskit.screen.GameScreen;
import com.gamesbykevin.riskit.territory.Territories;
import com.gamesbykevin.riskit.territory.Territory;
import com.gamesbykevin.riskit.preferences.MyPreferences;
import com.gamesbykevin.riskit.util.IMyGdxGame;
import com.gamesbykevin.riskit.util.MyGdxGameHelper;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Players implements IMyGdxGame {

    private Player[] players;

    //the id of the player with a current turn
    private int currentId;

    private transient List<Territory> spawnList;

    //this is transient so it won't be included when writing to json
    private transient Status status;

    //is one of the cpu's thinking?
    private boolean thinking;

    //did the human take the first turn
    private boolean humanFirst;

    public Players(Player[] players) {
        this.players = players;
        this.status = new Status(this);
    }

    public Players() {
        this.players = new Player[MyPreferences.getInt(MyPreferences.PREFS_PLAYERS)];
        this.status = new Status(this);

        //does the human go first
        boolean orderHuman = MyPreferences.isBoolean(MyPreferences.PREFS_ORDER_HUMAN, true);

        for (int id = 0; id < getPlayerCount(); id++) {

            Player player;

            //make 1 player human, and let them go first
            if (id == 0) {
                player = new Human(id, id);

                //set human goes first if enabled
                if (orderHuman)
                    setCurrentId(id);

            } else {
                player = new Computer(id, id);
            }

            player.setOrder(id);
            this.players[id] = player;
        }

        //pick random player to go first
        if (!orderHuman) {
            Random random = MyGdxGameHelper.getRandom();
            setCurrentId(random.nextInt(getPlayerCount()));
            setHumanFirst(getPlayerCurrent().isHuman());
        } else {
            setHumanFirst(true);
        }

        setThinking(false);
    }

    public void setHumanFirst(boolean humanFirst) {
        this.humanFirst = humanFirst;
    }

    public boolean isHumanFirst() {
        return this.humanFirst;
    }

    public void setThinking(boolean thinking) {
        this.thinking = thinking;
    }

    public boolean isThinking() {
        return this.thinking;
    }

    public Status getStatus() {
        return this.status;
    }

    private List<Territory> getSpawnList() {

        if (this.spawnList == null)
            this.spawnList = new ArrayList<>();

        return this.spawnList;
    }

    public void assignTerritories(Territories territories) {

        List<Territory> territoryList = territories.getTerritoriesList();

        //each player gets an equal number of territories and dice
        int territoriesEach = territoryList.size() / getPlayerCount();

        //give each player a default # of dice depending on the # of territories
        for (int i = 0; i < getPlayerCount(); i++) {
            getPlayer(i).setRemaining(Player.DICE_DEFAULT * territoriesEach);
        }

        int index = 0;

        //assign all territories to our players
        while (territories.getUnassigned() != null) {

            Player player = getPlayer(index);
            Territory territory = territories.getUnassigned();
            territory.setPlayerId(player);

            index++;

            if (index >= getPlayerCount())
                index = 0;
        }

        //now we populate dice for each player
        for (int i = 0; i < getPlayerCount(); i++) {

            Player player = getPlayer(i);

            while (player.getRemaining() > 0) {
                populate(player, territories, Territory.DICE_MAX_SPAWN);
            }
        }
    }

    //here we populate dice for territories owned by the player
    public void populate(Player player, Territories territories, int maxDice) {

        List<Territory> territoryList = territories.getTerritoriesList();

        getSpawnList().clear();
        for (int j = 0; j < territoryList.size(); j++) {
            Territory territory = territoryList.get(j);

            //only check territories the player owns
            if (territory.getPlayerId() != player.getId())
                continue;

            //only add territories that have room to grow
            if (territory.getDice() < maxDice)
                getSpawnList().add(territory);
        }

        //don't continue if no spawn options are left
        if (getSpawnList().isEmpty()) {
            player.setRemaining(0);
            return;
        }

        //now that we have our list of territories let's pick one at random
        Random random = MyGdxGameHelper.getRandom();

        int index = random.nextInt(getSpawnList().size());
        Territory territory = getSpawnList().get(index);
        territory.setDice(territory.getDice() + 1);
        player.setRemaining(player.getRemaining() - 1);
    }

    public int getCurrentId() {
        return this.currentId;
    }

    public void setCurrentId(int currentId) {
        this.currentId = currentId;

        if (getCurrentId() >= getPlayerCount()) {
            this.currentId = 0;

            if (!getPlayerCurrent().isDead() && getPlayerCurrent().isHuman())
                getStatus().getPlayerButtonRest().setVisible(true);
        }
    }

    public Player getPlayerCurrent() {
        return getPlayer(getCurrentId());
    }

    public Player[] getPlayers() {
        return this.players;
    }

    public Player getPlayer(int id) {

        for (int index = 0; index < getPlayerCount(); index++) {
            Player player = this.players[index];

            if (player.getId() == id)
                return player;
        }

        return null;
    }

    public int getPlayerCount() {
        return this.players.length;
    }

    public boolean isGameOver() {

        //count alive players
        int count = 0;

        for (int index = 0; index < getPlayerCount(); index++) {
            Player player = getPlayer(index);

            if (!player.isDead())
                count++;
        }

        return (count <= 1);
    }

    //are all humans dead?
    public boolean isHumansDead() {

        for (int index = 0; index < getPlayerCount(); index++) {
            Player player = getPlayer(index);

            if (player.isHuman() && !player.isDead())
                return false;
        }

        return true;
    }

    public void countConsecutive(Board board) {

        for (int index = 0; index < getPlayerCount(); index++) {

            Player player = getPlayer(index);
            player.setConsecutiveCount(0);
            player.setRemaining(0);

            for (int j = 0; j < board.getTerritories().getTerritoriesList().size(); j++) {
                Territory territory = board.getTerritories().getTerritoriesList().get(j);

                if (territory.getPlayerId() != player.getId())
                    continue;

                //reset territories counted
                board.getTerritories().resetChecked();

                //count number of territories connected to this territory
                int count = board.getTerritories().countConsecutive(territory, 1, false);
                territory.setConsecutiveCount(count);

                if (territory.getConsecutiveCount() > player.getConsecutiveCount()) {
                    player.setConsecutiveCount(count);
                    player.setRemaining(count);
                }
            }

            //if the player doesn't have any territories then they are dead
            player.setDead(player.getConsecutiveCount() <= 0);
        }
    }

    public void update(GameScreen screen) {
        getPlayerCurrent().update(screen);
        getStatus().update(screen);
    }

    @Override
    public void dispose() {

        if (this.status != null)
            this.status.dispose();

        if (this.players != null) {
            for (int i = 0; i < this.players.length; i++) {

                if (this.players[i] != null)
                    this.players[i].dispose();

                this.players[i] = null;
            }
        }

        if (this.spawnList != null) {
            for (int i = 0; i < this.spawnList.size(); i++) {
                if (this.spawnList.get(i) != null)
                    this.spawnList.get(i).dispose();
                this.spawnList.set(i, null);
            }

            this.spawnList.clear();
        }

        this.status = null;
        this.players = null;
        this.spawnList = null;
    }
}