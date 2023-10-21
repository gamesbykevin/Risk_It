package com.gamesbykevin.riskit.player;

import com.gamesbykevin.riskit.assets.Manager;
import com.gamesbykevin.riskit.board.Board;
import com.gamesbykevin.riskit.player.status.Status;
import com.gamesbykevin.riskit.util.MyGdxGameHelper;
import com.gamesbykevin.riskit.util.StatusHelper;
import com.gamesbykevin.riskit.screen.GameScreen;
import com.gamesbykevin.riskit.territory.Territory;
import com.gamesbykevin.riskit.territory.Tile;

public class Human extends Player {

    public Human(int colorIndex, int id) {
        super(colorIndex, id);
        setHuman(true);
    }

    @Override
    public void update(GameScreen screen) {

        //if game is busy don't continue
        if (!MyGdxGameHelper.canInteract(screen))
            return;

        if (!isSelected())
            return;

        Board board = screen.getBoard();

        //did the human make a valid selection?
        boolean valid = true;

        int col = (int)(getSelectedCoordinates().x / Tile.WIDTH);
        int row = (int)(getSelectedCoordinates().y / (Tile.HEIGHT * Board.HEIGHT_OFFSET));

        int offset = 1;
        for (int c = col - offset; c <= col + offset; c++) {
            for (int r = row - offset; r <= row + offset; r++) {

                if (!board.hasBounds(c, r))
                    continue;

                Tile tile = board.getTile(c, r);

                //check if coordinates are within boundaries
                if (!tile.hasCoordinates(getSelectedCoordinates().x, getSelectedCoordinates().y))
                    continue;

                Territory territory = board.getTerritories().getTerritory(tile);

                //have to select a valid territory
                if (territory == null) {
                    valid = false;
                    continue;
                }

                if (territory.getPlayerId() != getId()) {

                    //we can only select our territory at first
                    if (getTerritoryAttack() == null) {
                        valid = false;
                        continue;
                    }

                    //we can only pick a valid neighbor
                    if (!getTerritoryAttack().hasNeighborId(territory.getId())) {
                        valid = false;
                        continue;
                    }

                } else {

                    //we can only select our territories that have more than 1 dice on it
                    if (territory.getDice() == Territory.DICE_MIN) {
                        valid = false;
                        continue;
                    }

                    //can only pick 1 source territory at a time
                    if (!territory.isSelected() && getTerritoryAttack() != null) {
                        valid = false;
                        continue;
                    }

                    if (!territory.getNeighborIds().isEmpty()) {

                        //default to not valid
                        valid = false;

                        //make sure the territory we are selecting has a neighbor that we can attack
                        for (int index = 0; index < territory.getNeighborIds().size(); index++) {
                            Territory tmp = board.getTerritories().getTerritoryById(territory.getNeighborIds().get(index));
                            if (tmp == null || tmp.getPlayerId() == MyGdxGameHelper.UNASSIGNED)
                                continue;

                            if (tmp.getPlayerId() != getId()) {
                                valid = true;
                                break;
                            }
                        }
                    }

                    //if not valid, skip
                    if (!valid)
                        continue;
                }

                territory.setSelected(!territory.isSelected());

                if (territory.isSelected()) {

                    if (getTerritoryAttack() == null) {
                        setTerritoryAttack(territory);
                    } else {
                        setTerritoryDefend(territory);
                        screen.getPlayers().getStatus().setState(Status.State.Challenge);
                        screen.getPlayers().getStatus().setDelta(0);
                        screen.getGame().getAssetManager().playSoundShuffleDice(screen);
                        StatusHelper.updateScreen(screen.getPlayers());
                    }

                } else {
                    setTerritoryAttack(null);
                    setTerritoryDefend(null);
                }

                c = col + offset;
                r = row + offset;
                break;
            }
        }

        setSelected(false);
        setSelectedCoordinates(null);

        if (!valid) {
            screen.getGame().getAssetManager().playSound(Manager.SOUND_DIR_HUMAN_INVALID_SELECTION);
        }
    }

    @Override
    public void dispose() {
        super.dispose();
    }
}