package com.gamesbykevin.riskit.screen;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.StretchViewport;
import com.gamesbykevin.riskit.MyGdxGame;
import com.gamesbykevin.riskit.localization.Language;
import com.gamesbykevin.riskit.preferences.MyPreferences;
import com.gamesbykevin.riskit.util.MyGdxGameHelper;

public abstract class ParentScreen implements Screen {

    public enum Screens {
        Intro,
        Splash,
        MainMenu,
        Settings,
        GameScreen
    }

    private final MyGdxGame game;
    private Stage stage;
    private SpriteBatch batch;

    private InputMultiplexer multiplexer;

    private static boolean PROMPT = false;

    public ParentScreen(MyGdxGame game) {
        this.game = game;
        this.stage = new Stage(new StretchViewport(MyGdxGameHelper.WIDTH, MyGdxGameHelper.HEIGHT));
        this.batch = new SpriteBatch();
        this.multiplexer = new InputMultiplexer();
    }

    public abstract void setupUI();
    public Skin getSkin() {
        return getGame().getAssetManager().getSkin();
    }

    public SpriteBatch getBatch() {
        return this.batch;
    }

    public Stage getStage() {
        return this.stage;
    }

    public MyGdxGame getGame() {
        return this.game;
    }

    public InputMultiplexer getMultiplexer() {
        return this.multiplexer;
    }

    public Image getBackground() {
        Image image = new Image(getSkin().getDrawable(MyGdxGameHelper.DRAWABLE_BACKGROUND));
        MyGdxGameHelper.centerActor(image);
        return image;
    }

    public void preventBackKey(boolean catchKey) {
        //prevent user from accidentally hitting back
        Gdx.input.setCatchKey(Input.Keys.BACK, catchKey);
        Gdx.input.setCatchKey(Input.Keys.MENU, catchKey);
    }

    public void loginServices(boolean force) {

        //services are only for android
        if (!MyGdxGameHelper.isAndroid())
            return;

        try {

            //if we haven't prompted yet or we are forcing login
            if (!PROMPT || force) {
                if (!getGame().getGameServiceClient().isSessionActive()) {
                    PROMPT = true;
                    getGame().getGameServiceClient().logIn();
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void pause() {
        //do anything?
        getGame().getGameServiceClient().pauseSession();
    }

    @Override
    public void resize(int width, int height) {
        //do anything?
    }

    @Override
    public void resume() {
        //do anything?
        getGame().getGameServiceClient().resumeSession();
    }

    @Override
    public void hide() {
        //do anything?
    }

    @Override
    public void show() {
        loginServices(false);
        getMultiplexer().addProcessor(getStage());
        Gdx.input.setInputProcessor(getMultiplexer());
    }

    @Override
    public void render(float delta) {
        ScreenUtils.clear(1, 1, 1, 1);

        if (getBatch() != null) {
            getBatch().begin();
            getStage().draw();
            getBatch().end();
        }
    }

    @Override
    public void dispose() {

        if (this.stage != null) {
            this.stage.clear();
            this.stage.dispose();
        }

        if (this.batch != null)
            this.batch.dispose();

        if (this.multiplexer != null)
            this.multiplexer.clear();

        this.stage = null;
        this.batch = null;
        this.multiplexer = null;
    }
}