package com.gamesbykevin.riskit.territory;

import com.gamesbykevin.riskit.board.Board;
import com.gamesbykevin.riskit.util.IMyGdxGame;
import com.gamesbykevin.riskit.util.MyGdxGameHelper;

import java.util.ArrayList;
import java.util.List;

public class Territories implements IMyGdxGame {

    private ArrayList<Territory> territories;

    public Territories() {
        this.territories = new ArrayList<>();
    }

    public void addTerritory(int index, int seedCol, int seedRow) {
        getTerritoriesList().add(new Territory(index, seedCol, seedRow));
    }

    public void removeTerritory(int id) {
        for (int i = 0; i < getTerritoriesList().size(); i++) {
            Territory territory = getTerritoriesList().get(i);

            if (territory.getId() == id) {
                getTerritoriesList().remove(i);
                break;
            }
        }
    }

    public int getTerritoryCount(int playerId, boolean full) {

        int count = 0;

        for (int index = 0; index < getTerritoriesList().size(); index++) {
            Territory territory = getTerritoriesList().get(index);

            if (territory == null)
                continue;

            if (playerId != MyGdxGameHelper.UNASSIGNED && territory.getPlayerId() != playerId)
                continue;

            if (full) {
                if (territory.getDice() >= Territory.DICE_MAX) {
                    count++;
                }
            } else {
                count++;
            }
        }

        return count;
    }

    public ArrayList<Territory> getTerritoriesList() {
        return this.territories;
    }

    public Territory getTerritoryById(int id) {
        for (int i = 0; i < getTerritoriesList().size(); i++) {
            Territory territory = getTerritoriesList().get(i);
            if (territory.getId() == id)
                return territory;
        }

        return null;
    }

    public Territory getTerritory(Tile tile) {
        return getTerritoryById(tile.getTerritoryId());
    }

    //check surrounding area around location, return list of tiles around specified location
    public List<Tile> checkArea(Board board, int col, int row, boolean open) {

        List<Tile> results = new ArrayList<>();

        int x[] = Tile.getNeighborCoordinatesX(row);
        int y[] = Tile.getNeighborCoordinatesY(row);

        for (int index = 0; index < x.length; index++) {
            if (board.hasBounds(col + x[index], row + y[index])) {
                Tile tile = board.getTile(col + x[index], row + y[index]);
                if (open && tile.getTerritoryId() == MyGdxGameHelper.UNASSIGNED) {
                    results.add(tile);
                } else if (!open && tile.getTerritoryId() != MyGdxGameHelper.UNASSIGNED) {
                    results.add(tile);
                }
            }
        }

        return results;
    }

    public Territory getUnassigned() {
        for (int i = 0; i < getTerritoriesList().size(); i++) {
            Territory territory = getTerritoriesList().get(i);

            if (territory.getPlayerId() == MyGdxGameHelper.UNASSIGNED)
                return territory;
        }

        return null;
    }

    public void resetChecked() {
        for (int i = 0; i < getTerritoriesList().size(); i++) {
            getTerritoriesList().get(i).setChecked(false);
        }
    }

    public void identifyBorders(Board board) {
        for (int col = 0; col < board.getCols(); col++) {
            for (int row = 0; row < board.getRows(); row++) {
                Tile tile = board.getTile(col, row);

                //regardless of row, the west and east coordinates are the same
                if (!board.hasBounds(tile.getCol() - 1, tile.getRow()) && tile.getTerritoryId() != MyGdxGameHelper.UNASSIGNED)
                    tile.setBorderW(true);
                if (!board.hasBounds(tile.getCol() + 1, tile.getRow()) && tile.getTerritoryId() != MyGdxGameHelper.UNASSIGNED)
                    tile.setBorderE(true);
                if (board.hasBounds(tile.getCol() - 1, tile.getRow()) && board.getTile(tile.getCol() - 1, tile.getRow()).getTerritoryId() != tile.getTerritoryId())
                    tile.setBorderW(true);
                if (board.hasBounds(tile.getCol() + 1, tile.getRow()) && board.getTile(tile.getCol() + 1, tile.getRow()).getTerritoryId() != tile.getTerritoryId())
                    tile.setBorderE(true);

                if (tile.getRow() % 2 == 0) {
                    if (!board.hasBounds(tile.getCol() - 1, tile.getRow() - 1) && tile.getTerritoryId() != MyGdxGameHelper.UNASSIGNED)
                        tile.setBorderSW(true);
                    if (board.hasBounds(tile.getCol() - 1, tile.getRow() - 1) && board.getTile(tile.getCol() - 1, tile.getRow() - 1).getTerritoryId() != tile.getTerritoryId())
                        tile.setBorderSW(true);

                    if (!board.hasBounds(tile.getCol(), tile.getRow() - 1) && tile.getTerritoryId() != MyGdxGameHelper.UNASSIGNED)
                        tile.setBorderSE(true);
                    if (board.hasBounds(tile.getCol(), tile.getRow() - 1) && board.getTile(tile.getCol(), tile.getRow() - 1).getTerritoryId() != tile.getTerritoryId())
                        tile.setBorderSE(true);

                    if (!board.hasBounds(tile.getCol() - 1, tile.getRow() + 1) && tile.getTerritoryId() != MyGdxGameHelper.UNASSIGNED)
                        tile.setBorderNW(true);
                    if (board.hasBounds(tile.getCol() - 1, tile.getRow() + 1) && board.getTile(tile.getCol() - 1, tile.getRow() + 1).getTerritoryId() != tile.getTerritoryId())
                        tile.setBorderNW(true);

                    if (!board.hasBounds(tile.getCol(), tile.getRow() + 1) && tile.getTerritoryId() != MyGdxGameHelper.UNASSIGNED)
                        tile.setBorderNE(true);
                    if (board.hasBounds(tile.getCol(), tile.getRow() + 1) && board.getTile(tile.getCol(), tile.getRow() + 1).getTerritoryId() != tile.getTerritoryId())
                        tile.setBorderNE(true);
                } else {

                    if (!board.hasBounds(tile.getCol(), tile.getRow() - 1) && tile.getTerritoryId() != MyGdxGameHelper.UNASSIGNED)
                        tile.setBorderSW(true);
                    if (board.hasBounds(tile.getCol(), tile.getRow() - 1) && board.getTile(tile.getCol(), tile.getRow() - 1).getTerritoryId() != tile.getTerritoryId())
                        tile.setBorderSW(true);

                    if (!board.hasBounds(tile.getCol() + 1, tile.getRow() - 1) && tile.getTerritoryId() != MyGdxGameHelper.UNASSIGNED)
                        tile.setBorderSE(true);
                    if (board.hasBounds(tile.getCol() + 1, tile.getRow() - 1) && board.getTile(tile.getCol() + 1, tile.getRow() - 1).getTerritoryId() != tile.getTerritoryId())
                        tile.setBorderSE(true);

                    if (!board.hasBounds(tile.getCol(), tile.getRow() + 1) && tile.getTerritoryId() != MyGdxGameHelper.UNASSIGNED)
                        tile.setBorderNW(true);
                    if (board.hasBounds(tile.getCol(), tile.getRow() + 1) && board.getTile(tile.getCol(), tile.getRow() + 1).getTerritoryId() != tile.getTerritoryId())
                        tile.setBorderNW(true);

                    if (!board.hasBounds(tile.getCol() + 1, tile.getRow() + 1) && tile.getTerritoryId() != MyGdxGameHelper.UNASSIGNED)
                        tile.setBorderNE(true);
                    if (board.hasBounds(tile.getCol() + 1, tile.getRow() + 1) && board.getTile(tile.getCol() + 1, tile.getRow() + 1).getTerritoryId() != tile.getTerritoryId())
                        tile.setBorderNE(true);
                }
            }
        }

        //sort territories by y coordinate to keep dice visible
        for (int i = 0; i < getTerritoriesList().size(); i++) {
            for (int j = i + 1; j < getTerritoriesList().size(); j++) {
                Territory t1 = getTerritoriesList().get(i);
                Territory t2 = getTerritoriesList().get(j);

                if (t1.getDiceY() < t2.getDiceY()) {
                    getTerritoriesList().set(i, t2);
                    getTerritoriesList().set(j, t1);
                }
            }
        }
    }

    public void identifyNeighbors(Board board) {
        for (int col = 0; col < board.getCols(); col++) {
            for (int row = 0; row < board.getRows(); row++) {
                Tile tile = board.getTile(col, row);
                if (tile.getTerritoryId() == MyGdxGameHelper.UNASSIGNED)
                    continue;
                if (tile.getTerritoryId() == MyGdxGameHelper.BLOCKED)
                    continue;

                Territory territory = getTerritory(tile);

                if (territory == null)
                    continue;

                int x[] = Tile.getNeighborCoordinatesX(row);
                int y[] = Tile.getNeighborCoordinatesY(row);

                for (int i = 0; i < x.length; i++) {
                    if (board.hasBounds(tile.getCol() + x[i], tile.getRow() + y[i]))
                        territory.addNeighborId(board.getTile(tile.getCol() + x[i], tile.getRow() + y[i]));
                }
            }
        }
    }

    public int countConsecutive(Territory current, int count, final boolean ignorePlayer) {

        current.setChecked(true);

        for (int i = 0; i < current.getNeighborIds().size(); i++) {

            //get id of neighboring territory
            Territory next = getTerritoryById(current.getNeighborIds().get(i));

            if (next == null || next.isChecked())
                continue;

            //if the neighbor has the same player
            if (current.getPlayerId() == next.getPlayerId() || ignorePlayer) {
                count = countConsecutive(next, count + 1, ignorePlayer);
            }
        }

        return count;
    }

    @Override
    public void dispose() {
        if (this.territories != null) {

            for (int i = 0; i < this.territories.size(); i++) {
                if (this.territories.get(i) != null)
                    this.territories.get(i).dispose();

                this.territories.set(i, null);
            }

            this.territories.clear();
        }

        this.territories = null;
    }
}