package com.gamesbykevin.riskit.screen;

import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.ProgressBar;
import com.gamesbykevin.riskit.MyGdxGame;
import com.gamesbykevin.riskit.preferences.MyPreferences;
import com.gamesbykevin.riskit.util.MyGdxGameHelper;

public class Splash extends ParentScreen {

    private ProgressBar progressBar;

    private float lapsed = 0;

    //how long till we go to next screen (in milliseconds)
    public static final int DURATION_DELAY = 333;

    public static final float PROGRESS_BAR_WIDTH = 580;
    public static final float PROGRESS_BAR_HEIGHT = 38;

    public static final float WINDOW_WIDTH = 680;
    public static final float WINDOW_HEIGHT = 440;

    public Splash(MyGdxGame game) {
        super(game);

        //default audio to on if not already configured
        if (!MyPreferences.hasPreference(MyPreferences.PREFS_AUDIO_ENABLED))
            MyPreferences.saveBoolean(MyPreferences.PREFS_AUDIO_ENABLED, true);

        setupUI();
    }

    public void setLapsed(float lapsed) {
        this.lapsed = lapsed;
    }

    public float getLapsed() {
        return this.lapsed;
    }

    public ProgressBar getProgressBar() {
        return this.progressBar;
    }

    @Override
    public void setupUI() {
        this.progressBar = new ProgressBar(0, 1, .01f, false, getSkin(), MyGdxGameHelper.STYLE_PROGRESS_BAR_BLUE);
        getProgressBar().setSize(PROGRESS_BAR_WIDTH, PROGRESS_BAR_HEIGHT);

        Image image = new Image(getSkin().getDrawable(MyGdxGameHelper.DRAWABLE_WEBSITE_45PX));
        MyGdxGameHelper.centerActorX(image);
        image.setY(MyGdxGameHelper.HEIGHT - image.getHeight() - (MyGdxGameHelper.HEIGHT * .2f));

        Image window = new Image(getSkin().getDrawable(MyGdxGameHelper.DRAWABLE_HORIZONTAL_WINDOW_2));
        window.setSize(WINDOW_WIDTH, WINDOW_HEIGHT);
        MyGdxGameHelper.centerActor(window);

        MyGdxGameHelper.centerActor(getProgressBar());
        getStage().clear();
        getStage().addActor(getBackground());
        getStage().addActor(window);
        getStage().addActor(image);
        getStage().addActor(getProgressBar());
    }

    @Override
    public void show() {
        super.show();
        setLapsed(0);
    }

    @Override
    public void render(float delta) {

        getProgressBar().setValue(getGame().getAssetManager().getProgress());

        super.render(delta);

        if (getGame().getAssetManager().isLoading()) {
            getGame().getAssetManager().update();
        } else if (getGame().getScreen(Screens.MainMenu) == null) {

            getGame().getAssetManager().addMusicCompleteListeners();

            getGame().addScreen(Screens.MainMenu, new MainMenu(getGame()));
        } else if (getGame().getScreen(Screens.Settings) == null) {
            getGame().addScreen(Screens.Settings, new Settings(getGame()));
        } else if (getGame().getScreen(Screens.Intro) == null) {
            getGame().addScreen(Screens.Intro, new Intro(getGame()));
        } else {
            getGame().selectScreen(Screens.Intro);
        }
    }

    @Override
    public void dispose() {
        super.dispose();
        progressBar = null;
    }
}