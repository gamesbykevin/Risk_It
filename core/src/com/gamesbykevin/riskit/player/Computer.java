package com.gamesbykevin.riskit.player;

import com.badlogic.gdx.Gdx;
import com.gamesbykevin.riskit.board.Board;
import com.gamesbykevin.riskit.player.status.Status;
import com.gamesbykevin.riskit.preferences.MyPreferences;
import com.gamesbykevin.riskit.screen.Settings;
import com.gamesbykevin.riskit.util.StatusHelper;
import com.gamesbykevin.riskit.screen.GameScreen;
import com.gamesbykevin.riskit.territory.Territories;
import com.gamesbykevin.riskit.territory.Territory;
import com.gamesbykevin.riskit.util.MyGdxGameHelper;

import java.util.ArrayList;
import java.util.List;

public class Computer extends Player {

    //sometimes we will have the computer wait
    private float delta;

    //how long to wait till acting
    public static final float PAUSE_DURATION = .5f;
    public static final float PAUSE_DURATION_SPEED = .1f;

    private transient List<Territory> attackTargets;
    private transient List<Territory> defendTargets;

    public Computer(int colorIndex, int id) {
        super(colorIndex, id);
        setHuman(false);
    }

    public void setDelta(float delta) {
        this.delta = delta;
    }

    public float getDelta() {
        return this.delta;
    }

    @Override
    public void update(GameScreen screen) {

        //if game is busy don't continue
        if (!MyGdxGameHelper.canInteract(screen))
            return;

        //keep track of time lapsed
        setDelta(getDelta() + Gdx.graphics.getDeltaTime());

        float duration = screen.getPlayers().getStatus().hasSpeed() ?  PAUSE_DURATION_SPEED : PAUSE_DURATION;
        if (getDelta() < duration)
            return;

        //flag that the computer is thinking
        screen.getPlayers().setThinking(true);

        //setup the list of targets we can attack
        setupAttackTargets(screen.getBoard().getTerritories());

        //set the score to beat
        int scoreWin = Integer.MIN_VALUE;
        int territoryWinId = MyGdxGameHelper.UNASSIGNED;
        final int tmpDepth = getDepth();

        //check the players list of moves, then simulate all other moves until this player has another turn
        for (int index = 0; index < getAttackTargets().size(); index++) {
            Territory tmp = getAttackTargets().get(index);

            int score = simulateGame(screen.getBoard(), screen.getPlayers(), tmp, getMoves(tmp, screen.getBoard()), tmpDepth);

            if (score > scoreWin) {

                //if score is better, this is the winner
                scoreWin = score;
                territoryWinId = tmp.getId();

            } else if (score == scoreWin && territoryWinId != MyGdxGameHelper.UNASSIGNED) {

                //if score is the same, pick the territory with more dice as the winner
                if (tmp.getDice() > screen.getBoard().getTerritories().getTerritoryById(territoryWinId).getDice()) {
                    scoreWin = score;
                    territoryWinId = tmp.getId();
                }
            }
        }

        //remove the non winning targets
        for (int index = 0; index < getAttackTargets().size(); index++) {
            Territory territory = getAttackTargets().get(index);
            if (territory.getId() != territoryWinId) {
                removeTarget(index);
                index--;
            }
        }

        setDelta(0);

        //if there are no targets we will now regenerate
        if (getAttackTargets().isEmpty()) {

            screen.getPlayers().countConsecutive(screen.getBoard());
            screen.getPlayers().getStatus().setState(Status.State.Regenerate);
            screen.getPlayers().getStatus().setDelta(0);
            StatusHelper.updateScreen(screen.getPlayers());

        } else {

            getAttackTargets().get(0).setSelected(true);
            getDefendTargets().get(0).setSelected(true);
            setTerritoryAttack(getAttackTargets().get(0));
            setTerritoryDefend(getDefendTargets().get(0));
            screen.getPlayers().getStatus().setState(Status.State.Challenge);
            screen.getPlayers().getStatus().setDelta(0);
            screen.getGame().getAssetManager().playSoundShuffleDice(screen);

            StatusHelper.updateScreen(screen.getPlayers());
        }

        //we are no longer thinking
        screen.getPlayers().setThinking(false);
    }

    private List<Territory> getDefendTargets() {

        if (this.defendTargets == null)
            this.defendTargets = new ArrayList<>();

        return this.defendTargets;
    }

    private List<Territory> getAttackTargets() {

        if (this.attackTargets == null)
            this.attackTargets = new ArrayList<>();

        return this.attackTargets;
    }

    private void addTarget(Territory attack, Territory defend) {
        getAttackTargets().add(attack);
        getDefendTargets().add(defend);
    }

    private void removeTarget(int index) {
        getAttackTargets().remove(index);
        getDefendTargets().remove(index);
    }

    private int simulateGame(Board board, Players players, Territory source, List<Territory> moves, int depth) {

        int topScore = Integer.MIN_VALUE;

        if (depth > 0 && !moves.isEmpty()) {

            //loop through each move here
            for (int index = 0; index < moves.size(); index++) {

                Territory tmp = moves.get(index);
                final int originalPlayerId = tmp.getPlayerId();
                final int originalDice = tmp.getDice();
                final int sourceDice = source.getDice();

                //update target territory as if we won the battle and remove 1 dice
                tmp.setPlayerId(this);
                tmp.setDice(source.getDice() - 1);
                source.setDice(1);

                //continue simulating moves with the current territory
                int score = simulateGame(board, players, tmp, getMoves(tmp, board), depth - 1);

                if (score > topScore)
                    topScore = score;

                //undo the previous move
                tmp.setPlayerId(originalPlayerId);
                tmp.setDice(originalDice);
                source.setDice(sourceDice);
            }

            moves.clear();
            moves = null;

        } else {

            moves.clear();
            moves = null;

            return calculateScore(players, board);
        }

        return topScore;
    }

    private void setupAttackTargets(Territories territories) {

        setTerritoryAttack(null);
        setTerritoryDefend(null);
        getAttackTargets().clear();
        getDefendTargets().clear();

        //start out with a default list of targets
        identifyTargets(territories);

        //filter list of targets
        filterTargets(MyPreferences.getInt(MyPreferences.PREFS_DIFFICULTY));
    }

    private void filterTargets(int difficulty) {

        //do we have at least 1 territory with a greater dice count than a neighbor
        boolean greater = false;

        for (int index = 0; index < getAttackTargets().size(); index++) {
            Territory territoryAttack = getAttackTargets().get(index);
            Territory territoryDefend = getDefendTargets().get(index);

            if (territoryAttack.getDice() > territoryDefend.getDice()) {
                greater = true;
                break;
            }
        }

        //if we have options for a better chance to win, remove the others
        if (greater) {
            for (int index = 0; index < getAttackTargets().size(); index++) {
                Territory territoryAttack = getAttackTargets().get(index);
                Territory territoryDefend = getDefendTargets().get(index);

                if (territoryAttack.getDice() <= territoryDefend.getDice()) {
                    removeTarget(index);
                    index--;
                }
            }
        }

        //if easy difficulty let's go with this list of targets
        if (difficulty == Settings.DIFFICULTY_EASY)
            return;

        //if we don't have anything greater, we can still target territories with the same count
        if (!greater) {

            for (int index = 0; index < getAttackTargets().size(); index++) {
                Territory territoryAttack = getAttackTargets().get(index);
                Territory territoryDefend = getDefendTargets().get(index);

                //if we have less dice than the territory we are attacking, we will skip
                if (territoryAttack.getDice() < territoryDefend.getDice()) {
                    removeTarget(index);
                    index--;
                }
            }
        }
    }

    private void identifyTargets(Territories territories) {

        //first check if there are territories to attack
        for (int i = 0; i < territories.getTerritoriesList().size(); i++) {

            Territory territoryAttack = territories.getTerritoriesList().get(i);

            //only check our own territory, that have more than the minimum dice
            if (territoryAttack.getPlayerId() != getId() || territoryAttack.getDice() <= Territory.DICE_MIN)
                continue;

            for (int j = 0; j < territoryAttack.getNeighborIds().size(); j++) {
                int neighborId = territoryAttack.getNeighborIds().get(j);
                Territory territoryDefend = territories.getTerritoryById(neighborId);

                //we can't target ourselves
                if (territoryDefend.getPlayerId() == getId())
                    continue;

                //at first all neighbors are targets
                addTarget(territoryAttack, territoryDefend);
            }
        }
    }

    private List<Territory> getMoves(Territory territory, Board board) {

        List<Territory> moves = new ArrayList<>();

        //look at each neighbor territory and see if we can attack it
        for (int index = 0; index < territory.getNeighborIds().size(); index++) {
            int neighborId = territory.getNeighborIds().get(index);
            Territory tmp = board.getTerritories().getTerritoryById(neighborId);

            //we can't target ourselves
            if (tmp.getPlayerId() == territory.getPlayerId())
                continue;

            //we won't target territories with a smaller dice count
            if (tmp.getDice() >= territory.getDice())
                continue;

            //add this territory to list of possible moves
            moves.add(tmp);
        }

        return moves;
    }

    private int calculateScore(Players players, Board board) {

        //count consecutive tiles
        players.countConsecutive(board);

        int totalScore = 0;

        for (int index = 0; index < players.getPlayerCount(); index++) {
            Player player = players.getPlayer(index);

            if (player.getId() == getId()) {

                //add our count to the total score
                totalScore += player.getConsecutiveCount();

            } else {

                //take away the enemy territory count
                totalScore -= player.getConsecutiveCount();
            }
        }

        //we also want to factor in our neighbors that could conquer us next turn
        for (int index = 0; index < board.getTerritories().getTerritoriesList().size(); index++) {

            Territory territory = board.getTerritories().getTerritoryById(index);

            if (territory == null || territory.getPlayerId() != getId())
                continue;

            for (int j = 0; j < territory.getNeighborIds().size(); j++) {

                Territory tmp = board.getTerritories().getTerritoryById(j);
                if (tmp == null || tmp.getPlayerId() == getId())
                    continue;

                //if neighboring territories have more dice, that is bad for us
                if (territory.getDice() < tmp.getDice()) {
                    totalScore--;
                }
            }
        }

        //return the total score
        return totalScore;
    }

    private int getDepth() {
        switch (MyPreferences.getInt(MyPreferences.PREFS_DIFFICULTY)) {
            default:
            case Settings.DIFFICULTY_EASY:
                return DEPTH_EASY;
            case Settings.DIFFICULTY_MEDIUM:
                return DEPTH_MEDIUM;
            case Settings.DIFFICULTY_HARD:
                return DEPTH_HARD;
        }
    }

    @Override
    public void dispose() {
        super.dispose();


        if (this.attackTargets != null) {
            for (int i = 0; i < this.attackTargets.size(); i++) {

                if (this.attackTargets.get(i) != null)
                    this.attackTargets.get(i).dispose();
                this.attackTargets.set(i, null);
            }

            this.attackTargets.clear();
        }

        if (this.defendTargets != null) {
            for (int i = 0; i < this.defendTargets.size(); i++) {

                if (this.defendTargets.get(i) != null)
                    this.defendTargets.get(i).dispose();
                this.defendTargets.set(i, null);
            }

            this.defendTargets.clear();
        }

        this.attackTargets = null;
        this.defendTargets = null;
    }
}