package com.gamesbykevin.riskit.territory;

import com.gamesbykevin.riskit.util.MyGdxGameHelper;

import java.util.List;

public class Tile {

    private int col;
    private int row;
    private float x = 0, y = 0, w = 0, h = 0;
    private int territoryId;

    public static final int WIDTH = 20;
    public static final int HEIGHT = 24;

    private boolean borderW, borderNW, borderNE, borderE, borderSE, borderSW;
    private transient boolean checked;

    private static final int[] X_COORDINATE_EVEN = new int[]{0,  0, -1, -1, -1, 1};
    private static final int[] Y_COORDINATE_EVEN = new int[]{1, -1, -1,  1,  0, 0};

    private static final int[] X_COORDINATE_ODD = new int[]{-1, 1,  0, 0, 1,  1};
    private static final int[] Y_COORDINATE_ODD = new int[]{ 0, 0, -1, 1, 1, -1};

    //needed to load saved games from json
    public Tile() {
        //do nothing here...
    }

    public Tile(int col, int row) {
        setX(0);
        setY(0);
        setW(0);
        setH(0);
        setCol(col);
        setRow(row);
        setBorderE(false);
        setBorderNE(false);
        setBorderNW(false);
        setBorderSE(false);
        setBorderSW(false);
        setBorderW(false);
        setTerritoryId(MyGdxGameHelper.UNASSIGNED);
    }

    public static final int[] getNeighborCoordinatesX(int row) {
        return (row % 2 == 0) ? X_COORDINATE_EVEN : X_COORDINATE_ODD;
    }

    public static final int[] getNeighborCoordinatesY(int row) {
        return (row % 2 == 0) ? Y_COORDINATE_EVEN : Y_COORDINATE_ODD;
    }

    public boolean hasCoordinates(float coordinateX, float coordinateY) {
        if (coordinateX < getX() || coordinateY < getY())
            return false;
        if (coordinateX > getX() + getW() || coordinateY > getY() + getH())
            return false;

        return true;
    }

    public void setChecked(boolean checked) {
        this.checked = checked;
    }

    public boolean hasChecked() {
        return this.checked;
    }

    public void setW(float w) {
        this.w = w;
    }

    public void setH(float h) {
        this.h = h;
    }

    public void setX(float x) {
        this.x = x;
    }

    public void setY(float y) {
        this.y = y;
    }

    public float getX() {
        return this.x;
    }

    public float getY() {
        return this.y;
    }

    public float getW() {
        return this.w;
    }

    public float getH() {
        return this.h;
    }

    public boolean hasBorderE() {
        return this.borderE;
    }

    public boolean hasBorderNE() {
        return this.borderNE;
    }

    public boolean hasBorderNW() {
        return this.borderNW;
    }

    public boolean hasBorderSE() {
        return this.borderSE;
    }

    public boolean hasBorderSW() {
        return this.borderSW;
    }

    public boolean hasBorderW() {
        return this.borderW;
    }

    public void setBorderE(boolean borderE) {
        this.borderE = borderE;
    }

    public void setBorderNE(boolean borderNE) {
        this.borderNE = borderNE;
    }

    public void setBorderNW(boolean borderNW) {
        this.borderNW = borderNW;
    }

    public void setBorderSE(boolean borderSE) {
        this.borderSE = borderSE;
    }

    public void setBorderSW(boolean borderSW) {
        this.borderSW = borderSW;
    }

    public void setBorderW(boolean borderW) {
        this.borderW = borderW;
    }

    public void setTerritoryId(Territory territory) {
        setTerritoryId(territory.getId());
    }

    public void setTerritoryId(int territoryId) {
        this.territoryId = territoryId;
    }

    public int getTerritoryId() {
        return this.territoryId;
    }

    public static boolean hasTile(List<Tile> tiles, Tile tile) {
        return hasTile(tiles, tile.getCol(), tile.getRow());
    }

    public static boolean hasTile(List<Tile> tiles, int col, int row) {

        for (int i = 0; i < tiles.size(); i++) {
            Tile tile = tiles.get(i);

            if (tile.getCol() != col)
                continue;
            if (tile.getRow() != row)
                continue;

            return true;
        }

        return false;
    }

    public int getCol() {
        return this.col;
    }

    public int getRow() {
        return this.row;
    }

    public void setCol(int col) {
        this.col = col;
    }

    public void setRow(int row) {
        this.row = row;
    }
}