package com.gamesbykevin.riskit.player;

import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.gamesbykevin.riskit.assets.Manager;
import com.gamesbykevin.riskit.board.Board;
import com.gamesbykevin.riskit.preferences.MyPreferences;
import com.gamesbykevin.riskit.screen.GameScreen;
import com.gamesbykevin.riskit.screen.Settings;
import com.gamesbykevin.riskit.territory.Territories;
import com.gamesbykevin.riskit.territory.Territory;
import com.gamesbykevin.riskit.util.IMyGdxGame;
import com.gamesbykevin.riskit.util.MyGdxGameHelper;

import java.util.ArrayList;
import java.util.List;

public abstract class Player implements IMyGdxGame {

    private final int id;

    private boolean human;

    private boolean dead;

    //what order do we go
    private int order;

    //remaining dice when populating the board
    private int remaining;

    //greatest number of consecutive territories
    private int consecutiveCount;

    //used for when challenging neighbors
    private transient Territory territoryAttack, territoryDefend;

    //when we start, how much dice do we get per territory
    public static final int DICE_DEFAULT = 2;

    public static final int COLOR_BLUE = 0;
    public static final int COLOR_DARK_GREEN = 1;
    public static final int COLOR_DARK_PURPLE = 2;
    public static final int COLOR_LIGHT_GREEN = 3;
    public static final int COLOR_LIGHT_PURPLE = 4;
    public static final int COLOR_ORANGE = 5;
    public static final int COLOR_RED = 6;
    public static final int COLOR_YELLOW = 7;

    private final int colorIndex;

    private boolean selected;
    private transient Vector3 selectedCoordinates;

    public static final int DEPTH_EASY = 1;
    public static final int DEPTH_MEDIUM = 3;
    public static final int DEPTH_HARD = 9;

    public Player(int colorIndex, int id) {
        this.id = id;
        this.colorIndex = colorIndex;
        setConsecutiveCount(0);
        setDead(false);
    }

    public void setDead(boolean dead) {
        this.dead = dead;
    }

    public boolean isDead() {
        return this.dead;
    }

    public Territory getTerritoryAttack() {
        return this.territoryAttack;
    }

    public void setTerritoryAttack(Territory territoryAttack) {
        this.territoryAttack = territoryAttack;
    }

    public Territory getTerritoryDefend() {
        return this.territoryDefend;
    }

    public void setTerritoryDefend(Territory territoryDefend) {
        this.territoryDefend = territoryDefend;
    }

    public boolean isSelected() {
        return this.selected;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }

    public Vector3 getSelectedCoordinates() {
        if (this.selectedCoordinates == null)
            this.selectedCoordinates = new Vector3();

        return this.selectedCoordinates;
    }

    public void setSelectedCoordinates(Vector3 selectedCoordinates) {
        this.selectedCoordinates = selectedCoordinates;
    }

    public int getConsecutiveCount() {
        return this.consecutiveCount;
    }

    public void setConsecutiveCount(int consecutiveCount) {
        this.consecutiveCount = consecutiveCount;
    }

    public int getColorIndex() {
        return this.colorIndex;
    }

    public void setOrder(int order) {
        this.order = order;
    }

    public void setRemaining(int remaining) {
        this.remaining = remaining;
    }

    public int getRemaining() {
        return this.remaining;
    }

    public int getOrder() {
        return this.order;
    }

    public boolean isHuman() {
        return this.human;
    }

    public void setHuman(boolean human) {
        this.human = human;
    }

    public int getId() {
        return this.id;
    }

    public abstract void update(GameScreen screen);

    @Override
    public void dispose() {

        if (this.territoryAttack != null)
            this.territoryAttack.dispose();
        if (this.territoryDefend != null)
            this.territoryDefend.dispose();

        this.territoryAttack = null;
        this.territoryDefend = null;
        this.selectedCoordinates = null;
    }
}