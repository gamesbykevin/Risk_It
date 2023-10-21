package com.gamesbykevin.riskit.util;

import com.gamesbykevin.riskit.board.Board;
import com.gamesbykevin.riskit.territory.Territory;
import com.gamesbykevin.riskit.territory.Tile;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class BoardHelper {

    public static final float RATIO_3Q = 0.75f;

    public static final void connectIslands(Board board) {

        //count total number of occupied tiles
        int total = 0;
        for (int col = 0; col < board.getCols(); col++) {
            for (int row = 0; row < board.getRows(); row++) {
                Tile tile = board.getTile(col, row);
                if (tile.getTerritoryId() == MyGdxGameHelper.BLOCKED)
                    continue;
                if (tile.getTerritoryId() == MyGdxGameHelper.UNASSIGNED)
                    continue;
                total++;
            }
        }

        //continue until all territories are connected
        while (true) {

            int tilesCountLowest = 0;
            Territory territoryLowest = null;

            for (int index = 0; index < board.getTerritories().getTerritoriesList().size(); index++) {
                Territory territory = board.getTerritories().getTerritoryById(index);
                if (territory == null)
                    continue;

                resetTiles(board);

                int tilesCount = countTiles(board, territory.getSeedCol(), territory.getSeedRow(), 1);

                //if the count is less than 3Q of the total, it's a possible grow candidate
                if (tilesCount <= total * RATIO_3Q) {

                    //if we don't have an initial territory yet, pick a default
                    if (territoryLowest == null) {
                        territoryLowest = territory;
                        tilesCountLowest = tilesCount;
                    }

                    //make sure this is less than our lowest count
                    if (tilesCount < tilesCountLowest) {
                        territoryLowest = territory;
                        tilesCountLowest = tilesCount;
                    }
                }
            }

            //if we didn't find a lowest we are done
            if (territoryLowest == null) {
                break;
            } else {
                //grow the smallest territory
                growTerritory(board, territoryLowest);
            }
        }
    }

    private static void growTerritory(Board board, Territory territory) {

        List<Tile> options = new ArrayList<>();

        for (int col = 0; col < board.getCols(); col++) {
            for (int row = 0; row < board.getRows(); row++) {
                Tile tile = board.getTile(col, row);
                if (tile.getTerritoryId() != territory.getId())
                    continue;

                //check surrounding areas to grow
                int x[] = Tile.getNeighborCoordinatesX(row);
                int y[] = Tile.getNeighborCoordinatesY(row);

                for (int index = 0; index < x.length; index++) {
                    int tmpCol = col + x[index];
                    int tmpRow = row + y[index];

                    if (board.hasBounds(tmpCol, tmpRow)) {
                        Tile tmp = board.getTile(tmpCol, tmpRow);
                        if (tmp.getTerritoryId() != MyGdxGameHelper.UNASSIGNED && tmp.getTerritoryId() != MyGdxGameHelper.BLOCKED)
                            continue;

                        if (!MyGdxGameHelper.hasTile(options, tmp))
                            options.add(tmp);
                    }
                }
            }
        }

        Random random = MyGdxGameHelper.getRandom();
        Tile tile = options.get(random.nextInt(options.size()));
        tile.setTerritoryId(territory);

        options.clear();
        options = null;
    }

    private static int countTiles(Board board, int col, int row, int count) {

        int x[] = Tile.getNeighborCoordinatesX(row);
        int y[] = Tile.getNeighborCoordinatesY(row);

        board.getTile(col, row).setChecked(true);

        for (int index = 0; index < x.length; index++) {
            int tmpCol = col + x[index];
            int tmpRow = row + y[index];

            if (board.hasBounds(tmpCol, tmpRow)) {
                Tile tile = board.getTile(tmpCol, tmpRow);
                if (tile.getTerritoryId() == MyGdxGameHelper.UNASSIGNED)
                    continue;
                if (tile.getTerritoryId() == MyGdxGameHelper.BLOCKED)
                    continue;
                if (tile.hasChecked())
                    continue;

                tile.setChecked(true);
                count++;
                count = countTiles(board, tmpCol, tmpRow, count);
            }
        }

        return count;
    }

    public static final void cleanup(Board board) {

        while(true) {

            //keep track if we trimmed a piece
            boolean trim = false;

            for (int col = 0; col < board.getCols(); col++) {
                for (int row = 0; row < board.getRows(); row++) {

                    //check current tile
                    Tile tile = board.getTile(col, row);

                    if (tile.getTerritoryId() == MyGdxGameHelper.UNASSIGNED)
                        continue;
                    if (tile.getTerritoryId() == MyGdxGameHelper.BLOCKED)
                        continue;

                    int x[] = Tile.getNeighborCoordinatesX(row);
                    int y[] = Tile.getNeighborCoordinatesY(row);

                    int neighborCount = 0;
                    boolean neighborDifferent = false;

                    //check surrounding area
                    for (int i = 0; i < x.length; i++) {

                        int tmpCol = col + x[i];
                        int tmpRow = row + y[i];

                        //skip if we are out of bounds
                        if (!board.hasBounds(tmpCol, tmpRow))
                            continue;

                        Tile tmp = board.getTile(tmpCol, tmpRow);
                        if (tmp.getTerritoryId() == MyGdxGameHelper.UNASSIGNED)
                            continue;
                        if (tmp.getTerritoryId() == MyGdxGameHelper.BLOCKED)
                            continue;

                        if (tile.getTerritoryId() != tmp.getTerritoryId())
                            neighborDifferent = true;

                        neighborCount++;
                    }

                    //check if we can trim
                    if (neighborCount <= 2 && !neighborDifferent) {
                        trim = true;
                        tile.setTerritoryId(MyGdxGameHelper.UNASSIGNED);
                    }
                }
            }

            if (!trim)
                break;
        }

        //check remaining tiles that need to get unassigned
        for (int index = 0; index < board.getTerritories().getTerritoriesList().size(); index++) {
            Territory territory = board.getTerritories().getTerritoryById(index);
            if (territory == null)
                continue;

            checkTiles(board, territory.getSeedCol(), territory.getSeedRow(), territory.getId());
            removeUnchecked(board, territory.getId());
        }

        //check for any edges remaining
        for (int col = 0; col < board.getCols(); col++) {
            for (int row = 0; row < board.getRows(); row++) {
                Tile tile = board.getTile(col, row);
                if (tile.getTerritoryId() == MyGdxGameHelper.UNASSIGNED)
                    continue;
                if (tile.getTerritoryId() == MyGdxGameHelper.BLOCKED)
                    continue;

                int x[] = Tile.getNeighborCoordinatesX(row);
                int y[] = Tile.getNeighborCoordinatesY(row);

                int count = 0;

                for (int index = 0; index < x.length; index++) {
                    int tmpCol = col + x[index];
                    int tmpRow = row + y[index];

                    if (board.hasBounds(tmpCol, tmpRow)) {
                        Tile tmp = board.getTile(tmpCol, tmpRow);

                        if (tile.getTerritoryId() == tmp.getTerritoryId())
                            count++;
                    }
                }

                if (count <= 0)
                    tile.setTerritoryId(MyGdxGameHelper.UNASSIGNED);
            }
        }
    }

    private static void resetTiles(Board board) {
        for (int col = 0; col < board.getCols(); col++) {
            for (int row = 0; row < board.getRows(); row++) {
                Tile tile = board.getTile(col, row);
                tile.setChecked(false);
            }
        }
    }

    private static void checkTiles(Board board, int col, int row, final int territoryId) {

        int x[] = Tile.getNeighborCoordinatesX(row);
        int y[] = Tile.getNeighborCoordinatesY(row);

        board.getTile(col, row).setChecked(true);

        for (int index = 0; index < x.length; index++) {
            int tmpCol = col + x[index];
            int tmpRow = row + y[index];

            if (board.hasBounds(tmpCol, tmpRow)) {
                Tile tile = board.getTile(tmpCol, tmpRow);
                if (tile.getTerritoryId() != territoryId)
                    continue;
                if (tile.hasChecked())
                    continue;

                tile.setChecked(true);
                checkTiles(board, tmpCol, tmpRow, territoryId);
            }
        }
    }

    private static void removeUnchecked(Board board, int territoryId) {
        for (int col = 0; col < board.getCols(); col++) {
            for (int row = 0; row < board.getRows(); row++) {
                Tile tile = board.getTile(col, row);
                if (tile.getTerritoryId() != territoryId)
                    continue;

                if (!tile.hasChecked())
                    tile.setTerritoryId(MyGdxGameHelper.UNASSIGNED);
            }
        }
    }

    public static final void growTerritories(Board board, List<Tile> seeds) {

        Random random = MyGdxGameHelper.getRandom();

        //continue to expand at random while we have seeds to check
        while (!seeds.isEmpty()) {

            int index = random.nextInt(seeds.size());
            Tile tile = seeds.get(index);

            List<Tile> remaining = board.getTerritories().checkArea(board, tile.getCol(), tile.getRow(), true);

            if (remaining.isEmpty()) {

                //if area around tile is not available remove from list
                seeds.remove(index);

            } else {

                //pick a random remaining area to grow
                int tmpIndex = random.nextInt(remaining.size());

                //mark as part of the same territory
                Tile tmp = remaining.get(tmpIndex);
                tmp.setTerritoryId(tile.getTerritoryId());
                board.getTile(tmp.getCol(), tmp.getRow()).setTerritoryId(tile.getTerritoryId());

                //now let's also check this area as well
                if (!MyGdxGameHelper.hasTile(seeds, tmp))
                    seeds.add(tmp);
            }

            remaining.clear();
            remaining = null;
        }

    }

    public static final List<Tile> generateSeedList(Board board, boolean hole, int limit) {

        List<Tile> remaining = new ArrayList<>();

        final int buffer = (hole) ? Board.HOLE_BUFFER : Territory.BUFFER;

        //by default check all positions with enough buffer room as possible
        for (int col = 0; col < board.getCols(); col++) {
            for (int row = 0; row < board.getRows(); row++) {
                if (col - buffer < 0 || col + buffer >= board.getCols())
                    continue;
                if (row - buffer < 0 || row + buffer >= board.getRows())
                    continue;

                Tile tile = board.getTile(col, row);
                if (tile.getTerritoryId() == MyGdxGameHelper.UNASSIGNED)
                    remaining.add(tile);
            }
        }

        //keep track of our seeds for us to plant
        List<Tile> seeds = new ArrayList<>();

        int id = 0;

        Random random = MyGdxGameHelper.getRandom();

        while (true) {

            //pick random spot
            int index = random.nextInt(remaining.size());
            Tile tile = remaining.get(index);

            boolean valid = true;

            //now let's make sure this space has buffer area around it
            for (int col = tile.getCol() - buffer; col <= tile.getCol() + buffer; col++) {
                for (int row = tile.getRow() - buffer; row <= tile.getRow() + buffer; row++) {
                    if (!board.hasBounds(col, row))
                        continue;

                    if (board.getTile(col, row).getTerritoryId() != MyGdxGameHelper.UNASSIGNED) {
                        valid = false;
                        break;
                    }
                }

                if (!valid)
                    break;
            }

            if (hole) {

                //add to list of seeds
                if (valid) {
                    Tile tmp = board.getTile(tile.getCol(), tile.getRow());
                    tmp.setTerritoryId(MyGdxGameHelper.BLOCKED);
                    seeds.add(tmp);
                }

            } else {

                //also make sure there is enough height between seeds if they are near each other have the same column
                if (valid) {
                    for (int i = 0; i < seeds.size(); i++) {
                        Tile tmp = seeds.get(i);

                        int diffCol = (tile.getCol() > tmp.getCol()) ? tile.getCol() - tmp.getCol() : tmp.getCol() - tile.getCol();
                        int diffRow = (tile.getRow() > tmp.getRow()) ? tile.getRow() - tmp.getRow() : tmp.getRow() - tile.getRow();

                        if (diffCol <= 2 && diffRow <= buffer * 2) {
                            valid = false;
                            break;
                        }
                    }
                }

                if (valid) {

                    //create new territory
                    board.getTerritories().addTerritory(id, tile.getCol(), tile.getRow());

                    //mark tile as part of territory
                    Tile tmp = board.getTile(tile.getCol(), tile.getRow());
                    tmp.setTerritoryId(id);

                    Territory territory = board.getTerritories().getTerritory(tmp);
                    territory.setDiceX(tmp.getX() + (tmp.getW() / 2));
                    territory.setDiceY(tmp.getY() + (tmp.getH() / 2));

                    //add to list of seeds
                    seeds.add(tmp);

                    //each territory gets a unique id
                    id++;
                }
            }

            //remove location from our remaining list so we don't pick it again
            remaining.remove(index);

            if (hole) {
                if (seeds.size() >= limit) {
                    while(seeds.size() > limit) {
                        seeds.remove(0);
                    }
                    break;
                }
            }

            if (remaining.isEmpty())
                break;
        }

        remaining.clear();
        remaining = null;

        return seeds;
    }
}