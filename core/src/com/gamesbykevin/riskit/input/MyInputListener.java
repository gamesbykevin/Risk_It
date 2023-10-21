package com.gamesbykevin.riskit.input;

import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputProcessor;
import com.gamesbykevin.riskit.MyGdxGame;
import com.gamesbykevin.riskit.screen.ParentScreen;
import com.gamesbykevin.riskit.util.MyGdxGameHelper;
import com.gamesbykevin.riskit.screen.GameScreen;
import com.gamesbykevin.riskit.screen.dialog.Dialogs;

public class MyInputListener implements ApplicationListener, InputProcessor {

    private final MyGdxGame game;

    public MyInputListener(MyGdxGame game) {
        this.game = game;
    }

    private MyGdxGame getGame() {
        return this.game;
    }

    @Override
    public void create() {
        //don't do anything here
    }

    @Override
    public void resize(int width, int height) {
        //don't do anything here
    }

    @Override
    public void render() {
        //don't do anything here
    }

    @Override
    public void pause() {
        //don't do anything here
    }

    @Override
    public void resume() {
        //don't do anything here
    }

    @Override
    public void dispose() {
        //don't do anything here
    }

    @Override
    public boolean keyDown(int keycode) {
        return handleBackButton(keycode);
    }

    @Override
    public boolean keyUp(int keycode) {
        return handleBackButton(keycode);
    }

    @Override
    public boolean keyTyped(char character) {
        return false;
    }

    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button) {
        return false;
    }

    @Override
    public boolean touchUp(int screenX, int screenY, int pointer, int button) {
        return false;
    }

    @Override
    public boolean touchCancelled(int screenX, int screenY, int pointer, int button) {
        return false;
    }

    @Override
    public boolean touchDragged(int screenX, int screenY, int pointer) {
        return false;
    }

    @Override
    public boolean mouseMoved(int screenX, int screenY) {
        return false;
    }

    @Override
    public boolean scrolled(float amountX, float amountY) {
        return false;
    }

    private boolean handleBackButton(int button) {

        if (!MyGdxGameHelper.isAndroid())
            return false;

        if (button == Input.Keys.BACK || Gdx.input.isKeyPressed(Input.Keys.BACK)) {

            switch (getGame().getCurrentKey()) {

                case MainMenu:
                    getGame().getSprites().dispose();
                    getGame().getAssetManager().dispose();
                    getGame().dispose();
                    Gdx.app.exit();
                    break;

                case Settings:
                    getGame().selectScreen(ParentScreen.Screens.MainMenu);
                    break;

                case GameScreen:
                    GameScreen screen = (GameScreen)getGame().getScreen();

                    //if the computer is currently calculating moves we can't save
                    if (screen.getPlayers().isThinking())
                        return false;

                    Dialogs dialogs = screen.getDialogs();
                    if (dialogs != null) {
                        dialogs.setType(Dialogs.Type.Save);
                        return true;
                    }
                    break;
            }
        }

        return false;
    }
}
