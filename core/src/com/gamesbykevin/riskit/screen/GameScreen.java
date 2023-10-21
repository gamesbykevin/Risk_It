package com.gamesbykevin.riskit.screen;

import com.badlogic.gdx.input.GestureDetector;
import com.gamesbykevin.riskit.MyGdxGame;
import com.gamesbykevin.riskit.board.Board;
import com.gamesbykevin.riskit.input.MyGestureListener;
import com.gamesbykevin.riskit.input.MyInputListener;
import com.gamesbykevin.riskit.player.Players;
import com.gamesbykevin.riskit.util.StatusHelper;
import com.gamesbykevin.riskit.preferences.MyPreferences;
import com.gamesbykevin.riskit.util.DialogHelper;
import com.gamesbykevin.riskit.screen.dialog.Dialogs;
import com.gamesbykevin.riskit.sprites.Sprites;
import com.gamesbykevin.riskit.util.GameScreenHelper;
import com.gamesbykevin.riskit.util.MyGdxGameHelper;

public class GameScreen extends ParentScreen {

    private Players players;
    private Board board;
    private MyGestureListener gestureListener;
    private Dialogs dialogs;

    public GameScreen(MyGdxGame game) {
        super(game);

        this.players = new Players();
        this.dialogs = new Dialogs(game);
        this.gestureListener = new MyGestureListener(this);
        int cols, rows;

        switch (MyPreferences.getInt(MyPreferences.PREFS_SIZE)) {
            case Settings.SIZE_SMALL:
            default:
                cols = GameScreenHelper.SMALL_COLS;
                rows = GameScreenHelper.SMALL_ROWS;
                break;

            case Settings.SIZE_MEDIUM:
                cols = GameScreenHelper.MEDIUM_COLS;
                rows = GameScreenHelper.MEDIUM_ROWS;
                break;

            case Settings.SIZE_LARGE:
                cols = GameScreenHelper.LARGE_COLS;
                rows = GameScreenHelper.LARGE_ROWS;
                break;
        }

        this.board = new Board(cols, rows, (int)System.currentTimeMillis());
        getPlayers().assignTerritories(getBoard().getTerritories());
        getPlayers().countConsecutive(getBoard());
        setupUI();
    }

    public void setPlayers(Players players) {
        this.players = players;
    }

    public void setBoard(Board board) {
        this.board = board;
    }

    public Dialogs getDialogs() {
        return this.dialogs;
    }

    public Board getBoard() {
        return this.board;
    }

    public Players getPlayers() {
        return this.players;
    }

    public MyGestureListener getGestureListener() {
        return this.gestureListener;
    }

    @Override
    public void show() {

        //prevent user from accidentally hitting back
        super.preventBackKey(true);

        //add input listeners
        getMultiplexer().addProcessor(new MyInputListener(getGame()));
        getMultiplexer().addProcessor(new GestureDetector(getGestureListener()));
        super.show();
    }

    @Override
    public void setupUI() {
        getStage().clear();
        getStage().addActor(getBackground());
        StatusHelper.createStatusUI(this);
        DialogHelper.createDialogs(this);
    }

    @Override
    public void pause() {
        if (getDialogs() != null)
            getDialogs().setPaused(true);
    }

    @Override
    public void resume() {
        super.resume();
        while (!getGame().getAssetManager().update()){}
    }

    @Override
    public void render(float delta) {

        //update player activity
        if (getPlayers() != null)
            getPlayers().update(this);

        //render background etc...
        super.render(delta);

        //render board and players, status etc...
        if (getBoard() != null) {
            getBatch().begin();
            getBoard().render(this);
            getBatch().end();
        }

        //render dialogs using the stage, on top of everything else
        if (getDialogs() != null && getStage() != null) {
            getStage().getBatch().begin();
            getDialogs().render(getStage().getBatch());
            getStage().getBatch().end();
        }
    }

    @Override
    public void dispose() {
        super.dispose();

        if (this.players != null)
            this.players.dispose();
        if (this.board != null)
            this.board.dispose();
        if (this.dialogs != null)
            this.dialogs.dispose();

        this.players = null;
        this.board = null;
        this.gestureListener = null;
        this.dialogs = null;
    }
}