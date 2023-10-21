package com.gamesbykevin.riskit.util;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.gamesbykevin.riskit.assets.Manager;
import com.gamesbykevin.riskit.player.Players;
import com.gamesbykevin.riskit.player.status.Status;
import com.gamesbykevin.riskit.preferences.MyPreferences;
import com.gamesbykevin.riskit.screen.GameScreen;
import com.gamesbykevin.riskit.territory.Tile;

import java.util.List;
import java.util.Random;

public class MyGdxGameHelper {

    public static final int WIDTH = 800;
    public static final int HEIGHT = 480;

    private static Random RANDOM;

    public static final int UNASSIGNED = -1;
    public static final int BLOCKED = -2;

    public static final String STYLE_COLOR = "RGBA_254_189_11_255";

    public static final String URL_TWITTER = "https://twitter.com/gamesbykevin";
    public static final String URL_YOUTUBE = "https://youtube.com/@Gamesbykevin/videos";
    public static final String URL_FACEBOOK = "https://facebook.com/kevinsgames";
    public static final String URL_INSTAGRAM = "https://instagram.com/gamesbykevin";
    public static final String URL_YOUTUBE_TUTORIAL = "https://youtu.be/P3MSfxFhJto";
    public static final String URL_RATE_ANDROID = "https://play.google.com/store/apps/details?id=com.gamesbykevin.riskit";
    public static final String URL_RATE_AMAZON = "https://www.amazon.com/gp/mas/dl/android?p=com.gamesbykevin.riskit";

    public static final int FPS = 60;
    public static final int FRAME_DURATION = (1000 / FPS);

    public static final String STYLE_PROGRESS_BAR_BLUE = "progressBarBlue";
    public static final String DRAWABLE_WEBSITE_45PX = "website_45px_font";
    public static final String DRAWABLE_BACKGROUND = "bg";
    public static final String DRAWABLE_LOGO = "logo_250";
    public static final String DRAWABLE_STATUS_BAR = "table_down";
    public static final String DRAWABLE_PLAYER_STATUS = "table_top";
    public static final String DRAWABLE_PLAYER_STATUS_HIGHLIGHTED = "table_top_highlighted";

    public static final String DRAWABLE_DICE_BLUE = "dice_blue";
    public static final String DRAWABLE_DICE_DARK_GREEN = "dice_dark_green";
    public static final String DRAWABLE_DICE_DARK_PURPLE = "dice_dark_purple";
    public static final String DRAWABLE_DICE_LIGHT_GREEN = "dice_light_green";
    public static final String DRAWABLE_DICE_LIGHT_PURPLE = "dice_light_purple";
    public static final String DRAWABLE_DICE_ORANGE = "dice_orange";
    public static final String DRAWABLE_DICE_RED = "dice_red";
    public static final String DRAWABLE_DICE_YELLOW = "dice_yellow";
    public static final String DRAWABLE_DICE_WHITE = "dice_white";

    public static final String STYLE_BUTTON_COMPLETE_TURN = "button_complete";
    public static final String STYLE_BUTTON_UP = "button_up";
    public static final String STYLE_BUTTON_DOWN = "button_down";
    public static final String STYLE_BUTTON_SPEED = "button_speed";
    public static final String STYLE_BUTTON_EMPTY_75 = "button_75";
    public static final String STYLE_BUTTON_EMPTY_100 = "button_100";
    public static final String STYLE_BUTTON_EMPTY_140 = "button_140";
    public static final String STYLE_BUTTON_EMPTY_200 = "button_200";
    public static final String STYLE_BUTTON_EMPTY_250 = "button_250";
    public static final String STYLE_BUTTON_TWITTER = "button_twitter";
    public static final String STYLE_BUTTON_FACEBOOK = "button_facebook";
    public static final String STYLE_BUTTON_INSTAGRAM = "button_instagram";
    public static final String STYLE_BUTTON_YOUTUBE = "button_youtube";
    public static final String STYLE_BUTTON_AUDIO = "button_audio";
    public static final String STYLE_BUTTON_VIBRATE = "button_vibrate";

    public static final String DRAWABLE_HORIZONTAL_WINDOW_2 = "table_horizontal_2";
    public static final String DRAWABLE_HORIZONTAL_WINDOW_3 = "table_horizontal_3";
    public static final String DRAWABLE_HORIZONTAL_WINDOW_4 = "table_horizontal_4";

    public static final String FONT_16 = "font_16";
    public static final String FONT_20 = "font_20";
    public static final String FONT_24 = "font_24";
    public static final String FONT_28 = "font_28";
    public static final String FONT_32 = "font_32";
    public static final String FONT_36 = "font_36";
    public static final String FONT_44 = "font_44";

    public static final int VIBRATE_DURATION_SHORT = 333;
    public static final long[] VIBRATE_PATTERN = new long[] { 0, 200, 200, 200};

    public static void setRandom(int seed) {
        RANDOM = new Random(seed);
    }

    public static Random getRandom() {

        if (RANDOM == null)
            setRandom((int)System.currentTimeMillis());
        return RANDOM;
    }

    public static final boolean hasTile(List<Tile> tiles, Tile tile) {
        for (int index = 0; index < tiles.size(); index++) {
            Tile tmp = tiles.get(index);

            if (tmp.getCol() != tile.getCol())
                continue;
            if (tmp.getRow() != tile.getRow())
                continue;

            return true;
        }

        return false;
    }

    public static final Button createAudioButton(Skin skin, Manager assetManager, float x, float y, float w, float h) {

        Button buttonAudio = new Button(skin, MyGdxGameHelper.STYLE_BUTTON_AUDIO);
        buttonAudio.setPosition(x, y);
        buttonAudio.setSize(w, h);
        buttonAudio.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                boolean enabled = MyPreferences.isBoolean(MyPreferences.PREFS_AUDIO_ENABLED);
                MyPreferences.saveBoolean(MyPreferences.PREFS_AUDIO_ENABLED, !enabled);

                if (enabled) {
                    //if sound is off, stop everything
                    assetManager.stopAllSound();
                } else {
                    //play new song
                    assetManager.playSong();
                }
            }
        });
        buttonAudio.setChecked(MyPreferences.isBoolean(MyPreferences.PREFS_AUDIO_ENABLED));
        return buttonAudio;
    }

    public static boolean isAndroid() {
        switch (Gdx.app.getType()) {
            case Android:
                return true;
            default:
                return false;
        }
    }

    public static boolean isHtml() {
        switch (Gdx.app.getType()) {
            case WebGL:
            case Applet:
                return true;
            default:
                return false;
        }
    }

    public static void vibrate(boolean pattern) {

        //vibrate must be enabled
        if (!MyPreferences.isBoolean(MyPreferences.PREFS_VIBRATE))
            return;

        //can only vibrate on android
        if (!isAndroid())
            return;

        try {
            //vibrate for given duration
            if (pattern) {
                Gdx.input.vibrate(VIBRATE_DURATION_SHORT);//VIBRATE_PATTERN, -1);
            } else {
                Gdx.input.vibrate(VIBRATE_DURATION_SHORT);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static boolean canInteract(GameScreen screen) {

        Players players = screen.getPlayers();

        //don't do anything if there is a dice challenge, or are regenerating
        if (!players.getStatus().hasState(Status.State.Thinking))
            return false;

        //if game over stop
        if (screen.getPlayers().isGameOver())
            return false;

        //if a dialog is displayed don't continue
        if (screen.getDialogs().hasDialog())
            return false;

        return true;
    }

    public static void centerActor(Actor actor) {
        centerActorX(actor);
        centerActorY(actor);
    }

    public static void centerActorX(Actor actor) {
        actor.setX((WIDTH / 2) - (actor.getWidth() / 2));
    }

    public static void centerActorY(Actor actor) {
        actor.setY((HEIGHT / 2) - (actor.getHeight() / 2));
    }
}