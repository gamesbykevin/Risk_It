package com.gamesbykevin.riskit.territory;

import com.gamesbykevin.riskit.player.Player;
import com.gamesbykevin.riskit.util.IMyGdxGame;
import com.gamesbykevin.riskit.util.MyGdxGameHelper;

import java.util.ArrayList;

public class Territory implements IMyGdxGame {

    //give each territory space
    public static final int BUFFER = 2;

    public static final int WIDTH = 37;
    public static final int HEIGHT = 48;

    public static final int DICE_MAX = 8;
    public static final int DICE_MAX_SPAWN = 5;
    public static final int DICE_MIN = 1;

    //unique identifier of this territory
    private final int id;

    //how many territories are connected to this one
    private int consecutiveCount;

    //which player owns this territory
    private int playerId;

    //is this the current territory selected
    private boolean selected;

    //how many dice are at this territory
    private int dice;

    private float diceX, diceY;

    //where did the territory start to grow
    private transient final int seedCol, seedRow;

    //this will help us keep track when counting consecutive neighboring territories
    private boolean checked;

    //list of territory id's of our neighbors
    private ArrayList<Integer> neighborIds;

    public Territory(int id, int seedCol, int seedRow) {
        this.id = id;
        this.neighborIds = new ArrayList<>();
        this.seedCol = seedCol;
        this.seedRow = seedRow;
        setDice(DICE_MIN);
        setChecked(false);
        setSelected(false);
        setConsecutiveCount(0);
        setPlayerId(MyGdxGameHelper.UNASSIGNED);
    }

    public int getSeedCol() {
        return this.seedCol;
    }

    public int getSeedRow() {
        return this.seedRow;
    }

    public void setConsecutiveCount(int consecutiveCount) {
        this.consecutiveCount = consecutiveCount;
    }

    public int getConsecutiveCount() {
        return this.consecutiveCount;
    }

    public void setChecked(boolean checked) {
        this.checked = checked;
    }

    public boolean isChecked() {
        return this.checked;
    }

    public boolean isSelected() {
        return this.selected;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }

    public float getDiceX() {
        return this.diceX;
    }

    public float getDiceY() {
        return this.diceY;
    }

    public void setDiceX(float diceX) {
        this.diceX = diceX;
    }

    public void setDiceY(float diceY) {
        this.diceY = diceY;
    }

    public ArrayList<Integer> getNeighborIds() {
        return this.neighborIds;
    }

    public void addNeighborId(int territoryId) {

        if (territoryId == MyGdxGameHelper.UNASSIGNED)
            return;
        if (territoryId == MyGdxGameHelper.BLOCKED)
            return;

        if (!hasNeighborId(territoryId) && getId() != territoryId)
            getNeighborIds().add(territoryId);
    }

    //only add neighbor identifier if we don't have it, and don't add ourselves
    public void addNeighborId(Tile tile) {
        addNeighborId(tile.getTerritoryId());
    }

    public boolean hasNeighborId(int id) {
        for (int i = 0; i < getNeighborIds().size(); i++) {
            if (getNeighborIds().get(i) == id)
                return true;
        }

        return false;
    }

    public int getDice() {
        return this.dice;
    }

    public void setDice(int dice) {
        this.dice = dice;

        if (getDice() > DICE_MAX)
            this.dice = DICE_MAX;
        if (getDice() < DICE_MIN)
            this.dice = DICE_MIN;
    }

    public int getId() {
        return this.id;
    }

    public void setPlayerId(Player player) {
        setPlayerId(player.getId());
    }

    public void setPlayerId(int playerId) {
        this.playerId = playerId;
    }

    public int getPlayerId() {
        return this.playerId;
    }

    @Override
    public void dispose() {

        if (this.neighborIds != null)
            this.neighborIds.clear();

        this.neighborIds = null;
    }
}