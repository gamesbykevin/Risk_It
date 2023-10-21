package com.gamesbykevin.riskit.screen;

import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.ButtonGroup;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.gamesbykevin.riskit.MyGdxGame;
import com.gamesbykevin.riskit.assets.Manager;
import com.gamesbykevin.riskit.input.MyInputListener;
import com.gamesbykevin.riskit.localization.Language;
import com.gamesbykevin.riskit.preferences.MyPreferences;
import com.gamesbykevin.riskit.util.MyGdxGameHelper;
import com.gamesbykevin.riskit.util.StatusHelper;

public class Settings extends ParentScreen {

    public static final int PLAYERS_DEFAULT = 2;
    public static final int PLAYERS_MIN = 2;
    public static final int PLAYERS_MAX = 8;
    private int players;

    public static final int DIFFICULTY_EASY = 0;
    public static final int DIFFICULTY_MEDIUM = 1;
    public static final int DIFFICULTY_HARD = 2;

    public static final int SIZE_SMALL = 0;
    public static final int SIZE_MEDIUM = 1;
    public static final int SIZE_LARGE = 2;

    public static final int SETTINGS_WINDOW_WIDTH = 640;
    public static final int SETTINGS_WINDOW_HEIGHT = 292;

    public static final int TABLE_CONTAINER_WIDTH = 500;
    public static final int TABLE_CONTAINER_HEIGHT = 275;

    public static final int BUTTON_AUDIO_WIDTH = 80;
    public static final int BUTTON_AUDIO_HEIGHT = 80;

    public static final int PADDING = 8;

    private TextButton buttonEasy, buttonMedium, buttonHard;
    private TextButton buttonSM, buttonMD, buttonLG;
    private TextButton buttonHuman, buttonRandom;

    public Settings(MyGdxGame game) {
        super(game);
        setPlayers(PLAYERS_DEFAULT);
        setupUI();
    }

    public void setPlayers(int players) {
        this.players = players;
    }

    public int getPlayers() {
        return this.players;
    }

    public TextButton getButtonSM() {
        return this.buttonSM;
    }

    public TextButton getButtonMD() {
        return this.buttonMD;
    }

    public TextButton getButtonLG() {
        return this.buttonLG;
    }

    public TextButton getButtonEasy() {
        return this.buttonEasy;
    }

    public TextButton getButtonMedium() {
        return this.buttonMedium;
    }

    public TextButton getButtonHard() {
        return this.buttonHard;
    }

    public TextButton getButtonHuman() {
        return this.buttonHuman;
    }

    public TextButton getButtonRandom() {
        return this.buttonRandom;
    }

    private void saveSettings() {
        int difficulty = 0;
        if (getButtonEasy() != null && getButtonEasy().isChecked())
            difficulty = DIFFICULTY_EASY;
        if (getButtonMedium() != null && getButtonMedium().isChecked())
            difficulty = DIFFICULTY_MEDIUM;
        if (getButtonHard() != null && getButtonHard().isChecked())
            difficulty = DIFFICULTY_HARD;

        int size = 0;
        if (getButtonSM() != null && getButtonSM().isChecked())
            size = SIZE_SMALL;
        if (getButtonMD() != null && getButtonMD().isChecked())
            size = SIZE_MEDIUM;
        if (getButtonLG() != null && getButtonLG().isChecked())
            size = SIZE_LARGE;

        boolean order = true;
        if (getButtonHuman() != null && !getButtonHuman().isChecked())
            order = false;

        MyPreferences.saveInt(MyPreferences.PREFS_DIFFICULTY, difficulty);
        MyPreferences.saveInt(MyPreferences.PREFS_PLAYERS, getPlayers());
        MyPreferences.saveInt(MyPreferences.PREFS_SIZE, size);
        MyPreferences.saveBoolean(MyPreferences.PREFS_ORDER_HUMAN, order);
    }

    @Override
    public void show() {

        //prevent user from accidentally hitting back
        super.preventBackKey(true);

        //add input listeners
        getMultiplexer().addProcessor(new MyInputListener(getGame()));

        super.show();
    }

    @Override
    public void resume() {
        super.resume();
        while (!getGame().getAssetManager().update()){}
    }

    @Override
    public void setupUI() {

        String buttonStyle = MyGdxGameHelper.STYLE_BUTTON_EMPTY_100;

        TextButton buttonBack = new TextButton(getGame().getAssetManager().getTranslatedText(Language.KEY_BUTTON_BACK), getSkin(), buttonStyle);
        buttonBack.setPosition(StatusHelper.BUTTON_BACK_X, StatusHelper.BUTTON_BACK_Y);
        buttonBack.setSize(StatusHelper.BUTTON_BACK_W, StatusHelper.BUTTON_BACK_H);
        buttonBack.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                getGame().getAssetManager().playSound(Manager.SOUND_DIR_BUTTON_MENU);
                saveSettings();
                getGame().selectScreen(Screens.MainMenu);
            }
        });

        TextButton buttonPlay = new TextButton(getGame().getAssetManager().getTranslatedText(Language.KEY_BUTTON_START), getSkin(), MyGdxGameHelper.STYLE_BUTTON_EMPTY_140);
        MyGdxGameHelper.centerActorX(buttonPlay);
        buttonPlay.setY(StatusHelper.BUTTON_BACK_Y);
        buttonPlay.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                getGame().getAssetManager().playSound(Manager.SOUND_DIR_BUTTON_MENU);
                saveSettings();
                getGame().addScreen(Screens.GameScreen, new GameScreen(getGame()));
                getGame().selectScreen(Screens.GameScreen);
            }
        });

        Button buttonAudio = MyGdxGameHelper.createAudioButton(getSkin(), getGame().getAssetManager(), MyGdxGameHelper.WIDTH - BUTTON_AUDIO_WIDTH - (PADDING * 2), StatusHelper.BUTTON_BACK_Y, BUTTON_AUDIO_WIDTH, BUTTON_AUDIO_HEIGHT);

        Table table = new Table();
        table.setWidth(TABLE_CONTAINER_WIDTH);
        table.setHeight(TABLE_CONTAINER_HEIGHT);
        MyGdxGameHelper.centerActor(table);
        table.setY(table.getY() + (PADDING * 2));
        table.setDebug(false);

        table.add().colspan(4).pad(PADDING);
        table.row();

        Label labelPlayers = new Label(getGame().getAssetManager().getTranslatedText(Language.KEY_LABEL_PLAYERS), getSkin(), MyGdxGameHelper.FONT_24, MyGdxGameHelper.STYLE_COLOR);

        setPlayers(MyPreferences.getInt(MyPreferences.PREFS_PLAYERS, PLAYERS_DEFAULT));
        Label labelPlayerCount = new Label(players + "", getSkin(), MyGdxGameHelper.FONT_24, MyGdxGameHelper.STYLE_COLOR);

        Button buttonDown = new Button(getSkin(), MyGdxGameHelper.STYLE_BUTTON_DOWN);
        buttonDown.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                getGame().getAssetManager().playSound(Manager.SOUND_DIR_BUTTON_MENU);
                setPlayers(getPlayers() - 1);
                if (getPlayers() < PLAYERS_MIN)
                    setPlayers(PLAYERS_MAX);

                labelPlayerCount.setText(getPlayers());
            };
        });

        Button buttonUp = new Button(getSkin(), MyGdxGameHelper.STYLE_BUTTON_UP);
        buttonUp.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                getGame().getAssetManager().playSound(Manager.SOUND_DIR_BUTTON_MENU);
                setPlayers(getPlayers() + 1);
                if (getPlayers() > PLAYERS_MAX)
                    setPlayers(PLAYERS_MIN);

                labelPlayerCount.setText(getPlayers());
            };
        });

        table.add(labelPlayers).pad(PADDING);
        table.add(buttonDown).pad(PADDING);
        table.add(labelPlayerCount).pad(PADDING);
        table.add(buttonUp).pad(PADDING);
        table.row();

        Label labelDifficulty = new Label(getGame().getAssetManager().getTranslatedText(Language.KEY_LABEL_DIFFICULTY), getSkin(), MyGdxGameHelper.FONT_24, MyGdxGameHelper.STYLE_COLOR);

        this.buttonEasy = new TextButton(getGame().getAssetManager().getTranslatedText(Language.KEY_BUTTON_EASY), getSkin(), buttonStyle);
        this.buttonMedium = new TextButton(getGame().getAssetManager().getTranslatedText(Language.KEY_BUTTON_MED), getSkin(), buttonStyle);
        this.buttonHard = new TextButton(getGame().getAssetManager().getTranslatedText(Language.KEY_BUTTON_HARD), getSkin(), buttonStyle);

        ButtonGroup buttonGroup1 = new ButtonGroup<>(getButtonEasy(), getButtonMedium(), getButtonHard());
        buttonGroup1.setMaxCheckCount(1);
        buttonGroup1.setMinCheckCount(1);

        switch (MyPreferences.getInt(MyPreferences.PREFS_DIFFICULTY)) {
            case DIFFICULTY_EASY:
            default:
                getButtonEasy().setChecked(true);
                break;

            case DIFFICULTY_MEDIUM:
                getButtonMedium().setChecked(true);
                break;

            case DIFFICULTY_HARD:
                getButtonHard().setChecked(true);
                break;
        }

        table.add(labelDifficulty).pad(PADDING);
        table.add(getButtonEasy()).pad(PADDING);
        table.add(getButtonMedium()).pad(PADDING);
        table.add(getButtonHard()).pad(PADDING);

        table.row();

        Label labelSize = new Label(getGame().getAssetManager().getTranslatedText(Language.KEY_LABEL_SIZE), getSkin(), MyGdxGameHelper.FONT_24, MyGdxGameHelper.STYLE_COLOR);

        this.buttonSM = new TextButton(getGame().getAssetManager().getTranslatedText(Language.KEY_BUTTON_SMALL), getSkin(), buttonStyle);
        this.buttonMD = new TextButton(getGame().getAssetManager().getTranslatedText(Language.KEY_BUTTON_MED), getSkin(), buttonStyle);
        this.buttonLG = new TextButton(getGame().getAssetManager().getTranslatedText(Language.KEY_BUTTON_LARGE), getSkin(), buttonStyle);

        ButtonGroup buttonGroup2 = new ButtonGroup<>(getButtonSM(), getButtonMD(), getButtonLG());
        buttonGroup2.setMaxCheckCount(1);
        buttonGroup2.setMinCheckCount(1);

        switch (MyPreferences.getInt(MyPreferences.PREFS_SIZE)) {
            case SIZE_SMALL:
            default:
                getButtonSM().setChecked(true);
                break;

            case SIZE_MEDIUM:
                getButtonMD().setChecked(true);
                break;

            case SIZE_LARGE:
                getButtonLG().setChecked(true);
                break;
        }

        table.add(labelSize).pad(PADDING);
        table.add(getButtonSM()).pad(PADDING);
        table.add(getButtonMD()).pad(PADDING);
        table.add(getButtonLG()).pad(PADDING);
        table.row();

        Label labelOrder = new Label(getGame().getAssetManager().getTranslatedText(Language.KEY_LABEL_ORDER), getSkin(), MyGdxGameHelper.FONT_24, MyGdxGameHelper.STYLE_COLOR);

        this.buttonHuman = new TextButton(getGame().getAssetManager().getTranslatedText(Language.KEY_BUTTON_HUMAN), getSkin(), buttonStyle);
        this.buttonRandom = new TextButton(getGame().getAssetManager().getTranslatedText(Language.KEY_BUTTON_RANDOM), getSkin(), buttonStyle);

        ButtonGroup buttonGroup3 = new ButtonGroup<>(getButtonHuman(), getButtonRandom());
        buttonGroup3.setMaxCheckCount(1);
        buttonGroup3.setMinCheckCount(1);

        if (MyPreferences.isBoolean(MyPreferences.PREFS_ORDER_HUMAN, true)) {
            getButtonHuman().setChecked(true);
            getButtonRandom().setChecked(false);
        } else {
            getButtonHuman().setChecked(false);
            getButtonRandom().setChecked(true);
        }

        table.add(labelOrder).pad(PADDING);
        table.add(getButtonHuman()).pad(PADDING);
        table.add(getButtonRandom()).pad(PADDING);
        table.row();

        Image window = new Image(getSkin().getDrawable(MyGdxGameHelper.DRAWABLE_HORIZONTAL_WINDOW_3));
        window.setWidth(SETTINGS_WINDOW_WIDTH);
        window.setHeight(SETTINGS_WINDOW_HEIGHT);
        MyGdxGameHelper.centerActor(window);

        Label labelSettings = new Label(getGame().getAssetManager().getTranslatedText(Language.KEY_LABEL_SETTINGS), getSkin(), MyGdxGameHelper.FONT_44, MyGdxGameHelper.STYLE_COLOR);
        MyGdxGameHelper.centerActorX(labelSettings);
        labelSettings.setY(window.getY() + window.getHeight());

        getStage().addActor(getBackground());
        getStage().addActor(window);
        getStage().addActor(labelSettings);
        getStage().addActor(table);

        if (!MyGdxGameHelper.isAndroid())
            getStage().addActor(buttonBack);

        getStage().addActor(buttonPlay);
        getStage().addActor(buttonAudio);


        //add sound to buttons
        addSoundEffect(getButtonHuman());
        addSoundEffect(getButtonRandom());
        addSoundEffect(getButtonSM());
        addSoundEffect(getButtonMD());
        addSoundEffect(getButtonLG());
        addSoundEffect(getButtonEasy());
        addSoundEffect(getButtonMedium());
        addSoundEffect(getButtonHard());
    }

    private void addSoundEffect(TextButton textButton) {
        textButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                getGame().getAssetManager().playSound(Manager.SOUND_DIR_BUTTON_MENU);
            }
        });
    }
}