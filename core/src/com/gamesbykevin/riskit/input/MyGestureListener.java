package com.gamesbykevin.riskit.input;

import com.badlogic.gdx.input.GestureDetector;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.gamesbykevin.riskit.player.Player;
import com.gamesbykevin.riskit.screen.GameScreen;
import com.gamesbykevin.riskit.util.MyGdxGameHelper;

public class MyGestureListener implements GestureDetector.GestureListener {

    private final GameScreen screen;
    private Vector3 coordinates;

    public MyGestureListener(GameScreen screen) {
        this.screen = screen;
        this.coordinates = new Vector3();
    }

    private Vector3 getCoordinates() {
        return this.coordinates;
    }

    private GameScreen getScreen() {
        return this.screen;
    }

    @Override
    public boolean touchDown(float x, float y, int pointer, int button) {
        return false;
    }

    @Override
    public boolean tap(float x, float y, int count, int button) {

        //if game is paused, don't do anything here
        if (getScreen().getDialogs().hasDialog()) {
            getScreen().getDialogs().setPaused(false);
            return false;
        }

        if (!MyGdxGameHelper.canInteract(getScreen()))
            return false;

        Player player = getScreen().getPlayers().getPlayerCurrent();

        //don't accept input unless it's the humans turn
        if (!player.isHuman())
            return false;

        //if the player already made a selection
        if (player.isSelected())
            return false;

        getCoordinates().set(x, y, 0);
        Vector3 tmp = getScreen().getBoard().getCamera().unproject(getCoordinates());

        //flag the player has selected a location
        player.setSelectedCoordinates(tmp);
        player.setSelected(true);
        return false;
    }

    @Override
    public boolean longPress(float x, float y) {
        return false;
    }

    @Override
    public boolean fling(float velocityX, float velocityY, int button) {
        return false;
    }

    @Override
    public boolean pan(float x, float y, float deltaX, float deltaY) {
        return false;
    }

    @Override
    public boolean panStop(float x, float y, int pointer, int button) {
        return false;
    }

    @Override
    public boolean zoom(float initialDistance, float distance) {
        return false;
    }

    @Override
    public boolean pinch(Vector2 initialPointer1, Vector2 initialPointer2, Vector2 pointer1, Vector2 pointer2) {
        return false;
    }

    @Override
    public void pinchStop() {

    }
}
