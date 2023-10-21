package com.gamesbykevin.riskit.board;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.gamesbykevin.riskit.player.Player;
import com.gamesbykevin.riskit.player.Players;
import com.gamesbykevin.riskit.preferences.MyPreferences;
import com.gamesbykevin.riskit.screen.GameScreen;
import com.gamesbykevin.riskit.screen.Settings;
import com.gamesbykevin.riskit.sprites.Sprite;
import com.gamesbykevin.riskit.sprites.Sprites;
import com.gamesbykevin.riskit.territory.*;
import com.gamesbykevin.riskit.util.BoardHelper;
import com.gamesbykevin.riskit.util.IMyGdxGame;
import com.gamesbykevin.riskit.util.MyGdxGameHelper;

import java.util.List;
import java.util.Random;

public class Board implements IMyGdxGame {

    private int cols, rows;

    //used to generate a random board
    private final int seed;

    private Tile[][] tiles;

    private Territories territories;

    private transient OrthographicCamera camera;

    //when setting coordinates we offset Y by this much
    public static final float HEIGHT_OFFSET = .65f;

    //when rendering territory dice, how many per column?
    public static final int DICE_PER_COLUMN = 4;

    public static final int HOLE_FACTOR_MAX = 6;
    public static final int HOLE_FACTOR_MIN = 2;
    public static final int HOLE_BUFFER = 1;

    //what size was used for this board
    private int boardSizeIndex;

    public Board(int cols, int rows, int seed) {

        this.seed = seed;

        //create random with seed
        MyGdxGameHelper.setRandom(getSeed());

        //store the size of the board for when we load a saved game
        setBoardSizeIndex(MyPreferences.getInt(MyPreferences.PREFS_SIZE));

        this.cols = cols;
        this.rows = rows;

        this.territories = new Territories();

        resetCamera();

        this.tiles = new Tile[getRows()][getCols()];

        for (int col = 0; col < getCols(); col++) {
            for (int row = 0; row < getRows(); row++) {
                Tile tile = new Tile(col, row);
                tile.setTerritoryId(MyGdxGameHelper.UNASSIGNED);
                tile.setW(Tile.WIDTH);
                tile.setH(Tile.HEIGHT);

                float startX = (tile.getRow() % 2 == 0) ? 0 : (tile.getW() / 2);
                tile.setX(startX + (tile.getCol() * tile.getW()));
                tile.setY((tile.getRow() * (tile.getH() * HEIGHT_OFFSET)));

                this.tiles[row][col] = tile;
            }
        }

        //create list of tiles
        List<Tile> remaining;

        //get our list of seeds to create territories
        List<Tile> seeds = BoardHelper.generateSeedList(this, false, 0);

        int playerCount = MyPreferences.getInt(MyPreferences.PREFS_PLAYERS);

        //let's make sure each player gets an equal number of territories
        while (seeds.size() % playerCount != 0) {
            Tile tile = seeds.get(0);

            //remove a territory from our list
            getTerritories().removeTerritory(tile.getTerritoryId());

            //make sure tile is unassigned
            tile.setTerritoryId(MyGdxGameHelper.UNASSIGNED);

            //remove from our array
            seeds.remove(0);
        }

        int seedsPerPlayer = (seeds.size() / (playerCount / 2));

        Random random = MyGdxGameHelper.getRandom();
        int holeFactor = random.nextInt(HOLE_FACTOR_MAX - HOLE_FACTOR_MIN + 1) + HOLE_FACTOR_MIN;
        int emptyTerritories = seedsPerPlayer * holeFactor;

        //let's grow around the initial seeds first
        for (int index = 0; index < seeds.size(); index++) {
            Tile tile = seeds.get(index);
            remaining = getTerritories().checkArea(this, tile.getCol(), tile.getRow(), true);

            //add every tile around the center seed
            for (int x = 0; x < remaining.size(); x++) {
                Tile tmp = remaining.get(x);
                tmp.setTerritoryId(tile.getTerritoryId());
                getTile(tmp.getCol(), tmp.getRow()).setTerritoryId(tile.getTerritoryId());
            }

            remaining.clear();
            remaining = null;
        }

        //now let's add holes on the board
        seeds = BoardHelper.generateSeedList(this, true, emptyTerritories);

        for (int index = 0; index < seeds.size(); index++) {
            Tile tile = seeds.get(index);
            tile.setTerritoryId(MyGdxGameHelper.BLOCKED);
            remaining = getTerritories().checkArea(this, tile.getCol(), tile.getRow(), true);

            //create our hole
            for (int x = 0; x < remaining.size(); x++) {
                Tile tmp = remaining.get(x);
                tmp.setTerritoryId(tile.getTerritoryId());
                getTile(tmp.getCol(), tmp.getRow()).setTerritoryId(tile.getTerritoryId());
            }

            remaining.clear();
            remaining = null;
        }

        //clear the list
        seeds.clear();

        //check the whole board, and anything part of a territory is possible for expansion
        for (int col = 0; col < getCols(); col++) {
            for (int row = 0; row < getRows(); row++) {

                Tile tile = getTile(col, row);
                if (tile.getTerritoryId() == MyGdxGameHelper.UNASSIGNED)
                    continue;
                if (tile.getTerritoryId() == MyGdxGameHelper.BLOCKED)
                    continue;

                seeds.add(tile);
            }
        }

        //randomly grow territories
        BoardHelper.growTerritories(this, seeds);

        //remove ends
        BoardHelper.cleanup(this);

        //see where all our neighbors are at currently
        getTerritories().identifyNeighbors(this);

        //make sure all territories are connected to one another
        BoardHelper.connectIslands(this);

        //now lets check our neighbors one more time
        getTerritories().identifyNeighbors(this);

        //identify which tiles are borders for our outline
        getTerritories().identifyBorders(this);
    }

    public int getSeed() {
        return this.seed;
    }

    public int getBoardSizeIndex() {
        return this.boardSizeIndex;
    }

    public void setBoardSizeIndex(int boardSizeIndex) {
        this.boardSizeIndex = boardSizeIndex;
    }

    public void resetCamera() {
        switch (getBoardSizeIndex()) {
            case Settings.SIZE_SMALL:
            default:
                getCamera().position.set(MyGdxGameHelper.WIDTH * .4f, MyGdxGameHelper.HEIGHT * .25f, 0);
                break;

            case Settings.SIZE_MEDIUM:
                getCamera().position.set(MyGdxGameHelper.WIDTH * .5f, MyGdxGameHelper.HEIGHT * .25f, 0);
                getCamera().zoom += .05f;
                break;

            case Settings.SIZE_LARGE:
                getCamera().position.set(MyGdxGameHelper.WIDTH * .625f, MyGdxGameHelper.HEIGHT * .35f, 0);
                getCamera().zoom += .3f;
                break;
        }
    }

    public Territories getTerritories() {
        return this.territories;
    }

    public Tile getTile(int col, int row) {
        return getTiles()[row][col];
    }

    private Tile[][] getTiles() {
        return this.tiles;
    }

    public int getCols() {
        return this.cols;
    }

    public int getRows() {
        return this.rows;
    }

    public boolean hasBounds(int col, int row) {
        if (col < 0 || row < 0)
            return false;
        if (col >= getCols() || row >= getRows())
            return false;

        return true;
    }

    public OrthographicCamera getCamera() {

        if (this.camera == null)
            this.camera = new OrthographicCamera(MyGdxGameHelper.WIDTH, MyGdxGameHelper.HEIGHT);

        return this.camera;
    }

    public void render(GameScreen screen) {

        SpriteBatch spriteBatch = screen.getBatch();
        Players players = screen.getPlayers();
        Sprites sprites = screen.getGame().getSprites();

        getCamera().update();
        spriteBatch.setProjectionMatrix(getCamera().combined);

        //render hexagon tiles
        for (int row = getRows() - 1; row >= 0; row--) {
            for (int col = 0; col < getCols(); col++) {
                Tile tile = getTile(col, row);
                if (tile.getTerritoryId() == MyGdxGameHelper.UNASSIGNED)
                    continue;
                if (tile.getTerritoryId() == MyGdxGameHelper.BLOCKED)
                    continue;

                Territory territory = getTerritories().getTerritory(tile);
                Player player = players.getPlayer(territory.getPlayerId());
                Sprite sprite = sprites.getSprite(player, true);

                if (sprite != null) {
                    if (territory.isSelected())
                        sprite = sprites.getSprite(Sprite.Regions.TERRITORY_DARK);

                    renderSprite(spriteBatch, sprite, tile);
                }
            }
        }

        //render outline
        for (int row = getRows() - 1; row >= 0; row--) {
            for (int col = 0; col < getCols(); col++) {
                Tile tile = getTile(col, row);

                if (tile.getTerritoryId() == MyGdxGameHelper.BLOCKED)
                    continue;
                if (tile.getTerritoryId() == MyGdxGameHelper.UNASSIGNED)
                    continue;

                Territory territory = getTerritories().getTerritory(tile);
                if (territory != null && territory.isSelected()) {
                    if (tile.hasBorderW())
                        renderSprite(spriteBatch, sprites.getSprite(Sprite.Regions.TERRITORY_HIGHLIGHT_OUTLINE_W), tile);
                    if (tile.hasBorderE())
                        renderSprite(spriteBatch, sprites.getSprite(Sprite.Regions.TERRITORY_HIGHLIGHT_OUTLINE_E), tile);
                    if (tile.hasBorderNE())
                        renderSprite(spriteBatch, sprites.getSprite(Sprite.Regions.TERRITORY_HIGHLIGHT_OUTLINE_NE), tile);
                    if (tile.hasBorderNW())
                        renderSprite(spriteBatch, sprites.getSprite(Sprite.Regions.TERRITORY_HIGHLIGHT_OUTLINE_NW), tile);
                    if (tile.hasBorderSE())
                        renderSprite(spriteBatch, sprites.getSprite(Sprite.Regions.TERRITORY_HIGHLIGHT_OUTLINE_SE), tile);
                    if (tile.hasBorderSW())
                        renderSprite(spriteBatch, sprites.getSprite(Sprite.Regions.TERRITORY_HIGHLIGHT_OUTLINE_SW), tile);
                } else {
                    if (tile.hasBorderW())
                        renderSprite(spriteBatch, sprites.getSprite(Sprite.Regions.TERRITORY_OUTLINE_W), tile);
                    if (tile.hasBorderE())
                        renderSprite(spriteBatch, sprites.getSprite(Sprite.Regions.TERRITORY_OUTLINE_E), tile);
                    if (tile.hasBorderNE())
                        renderSprite(spriteBatch, sprites.getSprite(Sprite.Regions.TERRITORY_OUTLINE_NE), tile);
                    if (tile.hasBorderNW())
                        renderSprite(spriteBatch, sprites.getSprite(Sprite.Regions.TERRITORY_OUTLINE_NW), tile);
                    if (tile.hasBorderSE())
                        renderSprite(spriteBatch, sprites.getSprite(Sprite.Regions.TERRITORY_OUTLINE_SE), tile);
                    if (tile.hasBorderSW())
                        renderSprite(spriteBatch, sprites.getSprite(Sprite.Regions.TERRITORY_OUTLINE_SW), tile);
                }
            }
        }

        //render dice
        for (int i = 0; i < getTerritories().getTerritoriesList().size(); i++) {
            Territory territory = getTerritories().getTerritoriesList().get(i);

            //if not assigned to player skip
            Player player = players.getPlayer(territory.getPlayerId());
            if (player == null)
                continue;

            Sprite sprite = sprites.getSprite(player, false);

            int startX;
            int startY = (int)(territory.getDiceY() - (sprite.getHeight() / 2));

            if (territory.getDice() <= DICE_PER_COLUMN) {
                startX = (int)(territory.getDiceX() - (sprite.getWidth() * .3));
            } else {
                startX = (int)(territory.getDiceX() - sprite.getWidth());
            }

            for (int x = 1; x <= territory.getDice(); x++) {
                sprite.setX(startX);
                sprite.setY(startY);
                sprite.draw(spriteBatch, 0);

                startY += (sprite.getHeight() / 2);

                if (x == DICE_PER_COLUMN) {
                    startY = (int)(territory.getDiceY() - (sprite.getHeight() / 2));
                    startX += (sprite.getWidth() * 1.1);
                }
            }
        }
    }

    private void renderSprite(SpriteBatch spriteBatch, Sprite sprite, Tile tile) {
        sprite.setWidth(tile.getW());
        sprite.setHeight(tile.getH());
        sprite.setX(tile.getX());
        sprite.setY(tile.getY());
        sprite.draw(spriteBatch, 0);
    }

    @Override
    public void dispose() {

        if (this.territories != null)
            this.territories.dispose();

        this.camera = null;
        this.tiles = null;
        this.territories = null;
    }
}