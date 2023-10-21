package com.gamesbykevin.riskit.screen;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.ButtonGroup;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.gamesbykevin.riskit.MyGdxGame;
import com.gamesbykevin.riskit.assets.Manager;
import com.gamesbykevin.riskit.localization.Language;
import com.gamesbykevin.riskit.preferences.MyPreferences;
import com.gamesbykevin.riskit.util.MyGdxGameHelper;
import com.gamesbykevin.riskit.localization.Language.Languages;
import com.gamesbykevin.riskit.util.StatusHelper;

import de.golfgl.gdxgamesvcs.GameServiceException;
import de.golfgl.gdxgamesvcs.IGameServiceClient;

public class MainMenu extends ParentScreen {

    public static final int WINDOW_MENU_WIDTH = 640;
    public static final int WINDOW_MENU_HEIGHT = 292;

    public static final int BUTTON_SOCIAL_WIDTH = 80;
    public static final int BUTTON_SOCIAL_HEIGHT = 80;
    public static final int PADDING = 16;
    public static final int BUTTON_CHANGE_WIDTH = 40;
    public static final int BUTTON_CHANGE_HEIGHT = 40;

    public static final float IMAGE_LANGUAGE_WIDTH = 390f;

    public MainMenu(MyGdxGame game) {
        super(game);
        setupUI();
    }

    @Override
    public void show() {
        super.show();
        super.preventBackKey(false);
        setupUI();
        getGame().getAssetManager().playSong();
    }

    @Override
    public void resume() {
        super.resume();
        while (!getGame().getAssetManager().update()){}
    }

    @Override
    public void setupUI() {

        final boolean isAndroid = MyGdxGameHelper.isAndroid();

        Table tableMain = new Table();
        tableMain.setDebug(false);

        ButtonGroup buttonGroup = new ButtonGroup<>();
        buttonGroup.setMinCheckCount(0);
        buttonGroup.setMaxCheckCount(1);

        String buttonStyle = MyGdxGameHelper.STYLE_BUTTON_EMPTY_140;

        TextButton buttonPlay = new TextButton(getGame().getAssetManager().getTranslatedText(Language.KEY_BUTTON_PLAY), getSkin(), buttonStyle);
        buttonPlay.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                getGame().getAssetManager().playSound(Manager.SOUND_DIR_BUTTON_MENU);
                getGame().selectScreen(Screens.Settings);
            }
        });

        TextButton buttonResume = new TextButton(getGame().getAssetManager().getTranslatedText(Language.KEY_BUTTON_RESUME), getSkin(), buttonStyle);
        buttonResume.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                try {
                    getGame().getAssetManager().playSound(Manager.SOUND_DIR_BUTTON_MENU);
                    getGame().addScreen(Screens.GameScreen, new GameScreen(getGame()));
                    GameScreen screen = (GameScreen) getGame().getScreen(Screens.GameScreen);
                    MyPreferences.loadGame(screen);
                    getGame().selectScreen(Screens.GameScreen);
                } catch (Exception e) {
                    e.printStackTrace();

                    //in case of error go to settings page
                    getGame().selectScreen(Screens.Settings);
                }
            }
        });

        TextButton buttonTutorial = new TextButton(getGame().getAssetManager().getTranslatedText(Language.KEY_BUTTON_TUTORIAL), getSkin(), buttonStyle);
        addWebsiteListener(buttonTutorial, MyGdxGameHelper.URL_YOUTUBE_TUTORIAL);

        float width = buttonPlay.getWidth();
        float height = buttonPlay.getHeight();

        tableMain.setWidth(width * 3);
        tableMain.setHeight(height * 3);
        MyGdxGameHelper.centerActor(tableMain);

        if (MyPreferences.hasSavedGame()) {
            tableMain.add(buttonResume).width(width).height(height).pad(PADDING);
            tableMain.add(buttonPlay).width(width).height(height).pad(PADDING);
        } else {
            tableMain.add(buttonPlay).width(width).height(height).pad(PADDING);

            if (isAndroid)
                tableMain.add();
        }

        tableMain.add(buttonTutorial).width(width).height(height).pad(PADDING);
        tableMain.row();

        if (isAndroid) {

            TextButton buttonRate = new TextButton(getGame().getAssetManager().getTranslatedText(Language.KEY_BUTTON_RATE), getSkin(), buttonStyle);
            addWebsiteListener(buttonRate, MyGdxGame.GOOGLE_PLAY ? MyGdxGameHelper.URL_RATE_ANDROID : MyGdxGameHelper.URL_RATE_AMAZON);

            buttonGroup.add(buttonRate);
            tableMain.add(buttonRate).width(width).height(height).pad(PADDING);
            tableMain.add();

            if (MyGdxGame.GOOGLE_PLAY) {
                TextButton buttonTrophy = new TextButton(getGame().getAssetManager().getTranslatedText(Language.KEY_BUTTON_TROPHIES), getSkin(), buttonStyle);
                buttonTrophy.addListener(new ClickListener() {
                    @Override
                    public void clicked(InputEvent event, float x, float y) {
                        try {
                            loginServices(true);

                            if (getGame().getGameServiceClient().isFeatureSupported(IGameServiceClient.GameServiceFeature.ShowAchievementsUI))
                                getGame().getGameServiceClient().showAchievements();
                        } catch (Exception e) {
                            e.printStackTrace();
                        } catch (GameServiceException e) {
                            e.printStackTrace();
                        }
                    }
                });

                buttonGroup.add(buttonTrophy);
                tableMain.add(buttonTrophy).width(width).height(height).pad(PADDING);
            }

            tableMain.row();

        }

        buttonGroup.add(buttonPlay);
        buttonGroup.add(buttonResume);
        buttonGroup.add(buttonTutorial);

        Button buttonUp = new Button(getSkin(), MyGdxGameHelper.STYLE_BUTTON_UP);
        buttonUp.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                changeLanguage(1);
                getGame().getAssetManager().playSound(Manager.SOUND_DIR_BUTTON_MENU);
                loginServices(false);
            }
        });

        Button buttonDown = new Button(getSkin(), MyGdxGameHelper.STYLE_BUTTON_DOWN);
        buttonDown.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                changeLanguage(-1);
                getGame().getAssetManager().playSound(Manager.SOUND_DIR_BUTTON_MENU);
                loginServices(false);
            }
        });

        Languages language = Languages.values()[MyPreferences.getInt(MyPreferences.PREFS_LANGUAGE, Language.LANGUAGE_DEFAULT_INDEX)];
        Image image = new Image(getSkin().getDrawable(language.getStyle()));
        image.setWidth(IMAGE_LANGUAGE_WIDTH);
        image.setHeight((language.getH() / language.getW()) * IMAGE_LANGUAGE_WIDTH);

        Table tableLang = new Table();
        tableLang.add(buttonDown).width(BUTTON_CHANGE_WIDTH).height(BUTTON_CHANGE_WIDTH);
        tableLang.add(image);
        tableLang.add(buttonUp).width(BUTTON_CHANGE_WIDTH).height(BUTTON_CHANGE_WIDTH);

        tableMain.add(tableLang).height(image.getHeight()).colspan(3);

        Image logo = new Image(getSkin().getDrawable(MyGdxGameHelper.DRAWABLE_LOGO));
        MyGdxGameHelper.centerActorX(logo);
        logo.setY(MyGdxGameHelper.HEIGHT - logo.getHeight());

        Image window = new Image(getSkin().getDrawable(MyGdxGameHelper.DRAWABLE_HORIZONTAL_WINDOW_3));
        window.setWidth(WINDOW_MENU_WIDTH);
        window.setHeight(WINDOW_MENU_HEIGHT);
        window.setY(logo.getY() - window.getHeight() - PADDING);
        MyGdxGameHelper.centerActorX(window);

        getStage().clear();
        getStage().addActor(getBackground());
        getStage().addActor(window);
        getStage().addActor(logo);
        setupSocialButtons();
        getStage().addActor(tableMain);
        setupAudioButton();
    }

    private void changeLanguage(int change) {

        //apply change in index
        int index = MyPreferences.getInt(MyPreferences.PREFS_LANGUAGE, Language.LANGUAGE_DEFAULT_INDEX);
        int languageIndex = index + change;

        //stay in bounds
        if (languageIndex < 0) {
            languageIndex = Languages.values().length - 1;
        } else if (languageIndex >= Languages.values().length) {
            languageIndex = 0;
        }

        //change our language
        Languages language = Language.changeLanguage(languageIndex);

        getGame().getAssetManager().loadLanguage(language);

        getGame().selectScreen(Screens.Splash);
        getGame().getScreen(Screens.MainMenu).dispose();
        getGame().addScreen(Screens.MainMenu, null);
        getGame().getScreen(Screens.Settings).dispose();
        getGame().addScreen(Screens.Settings, null);
    }

    private void setupAudioButton() {
        Button button = MyGdxGameHelper.createAudioButton(getSkin(), getGame().getAssetManager(), MyGdxGameHelper.WIDTH - Settings.BUTTON_AUDIO_WIDTH - PADDING, StatusHelper.BUTTON_BACK_Y, BUTTON_SOCIAL_WIDTH, BUTTON_SOCIAL_HEIGHT);
        getStage().addActor(button);
    }

    private void setupSocialButtons() {

        Button buttonYoutube = new Button(getSkin(), MyGdxGameHelper.STYLE_BUTTON_YOUTUBE);
        Button buttonFacebook = new Button(getSkin(), MyGdxGameHelper.STYLE_BUTTON_FACEBOOK);
        Button buttonInstagram = new Button(getSkin(), MyGdxGameHelper.STYLE_BUTTON_INSTAGRAM);

        addWebsiteListener(buttonYoutube, MyGdxGameHelper.URL_YOUTUBE);
        addWebsiteListener(buttonFacebook, MyGdxGameHelper.URL_FACEBOOK);
        addWebsiteListener(buttonInstagram, MyGdxGameHelper.URL_INSTAGRAM);

        Table tableBottom = new Table();
        tableBottom.setDebug(false);
        tableBottom.setHeight(BUTTON_SOCIAL_HEIGHT);
        tableBottom.add(buttonFacebook).width(BUTTON_SOCIAL_WIDTH).height(BUTTON_SOCIAL_HEIGHT).pad(PADDING);
        tableBottom.add(buttonYoutube).width(BUTTON_SOCIAL_WIDTH).height(BUTTON_SOCIAL_HEIGHT).pad(PADDING);
        tableBottom.add(buttonInstagram).width(BUTTON_SOCIAL_WIDTH).height(BUTTON_SOCIAL_HEIGHT).pad(PADDING);
        tableBottom.setY(PADDING / 2);
        MyGdxGameHelper.centerActorX(tableBottom);

        getStage().addActor(tableBottom);
    }

    private void addWebsiteListener(Button button, String url) {
        button.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                getGame().getAssetManager().playSound(Manager.SOUND_DIR_BUTTON_MENU);
                Gdx.net.openURI(url);
            }
        });
    }

    @Override
    public void render(float delta) {
        super.render(delta);
    }

    @Override
    public void dispose() {
        super.dispose();
    }
}