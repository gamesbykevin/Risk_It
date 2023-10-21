package com.gamesbykevin.riskit.assets;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.assets.loaders.I18NBundleLoader;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.utils.I18NBundle;
import com.gamesbykevin.riskit.localization.Language;
import com.gamesbykevin.riskit.preferences.MyPreferences;
import com.gamesbykevin.riskit.screen.GameScreen;
import com.gamesbykevin.riskit.util.IMyGdxGame;
import com.gamesbykevin.riskit.util.MyGdxGameHelper;

import java.util.Locale;

public class Manager implements IMyGdxGame {

    private AssetManager assets;

    //used to free resources when changing skins
    private String skinPath;

    //used to load language translations
    private Language.Languages language;

    //used for localization
    public static final String LANGUAGE_BUNDLE_DIR = "i18n/MyBundle";

    //atlas here
    private static final String ATLAS_DIR = "atlas/";
    public static final String ATLAS_DIR_SPRITESHEET = ATLAS_DIR + "spritesheet.atlas";
    public static final String ATLAS_DIR_ANIMATION_1 = ATLAS_DIR + "animation-1/unnamed.atlas";

    private static final String AUDIO_DIR = "audio/";

    //songs here
    private static final String MUSIC_DIR = AUDIO_DIR + "music/";
    public static final String MUSIC_DIR_GAME_1 = MUSIC_DIR + "music_0.mp3";
    public static final String MUSIC_DIR_GAME_2 = MUSIC_DIR + "music_1.mp3";
    public static final String MUSIC_DIR_GAME_3 = MUSIC_DIR + "music_2.mp3";
    public static final String MUSIC_DIR_GAME_4 = MUSIC_DIR + "music_3.mp3";
    public static final String MUSIC_DIR_GAME_5 = MUSIC_DIR + "music_4.mp3";
    public static final String MUSIC_DIR_INTRO = MUSIC_DIR + "intro.mp3";

    //how many songs do we have
    public static final int SONG_COUNT = 5;
    public static final int SONG_INDEX_1 = 0;
    public static final int SONG_INDEX_2 = 1;
    public static final int SONG_INDEX_3 = 2;
    public static final int SONG_INDEX_4 = 3;
    public static final int SONG_INDEX_5 = 4;

    //which song are we playing
    private int songIndex = MyGdxGameHelper.UNASSIGNED;

    //audio here
    private static final String SOUND_DIR = AUDIO_DIR + "effects/";
    public static final String SOUND_DIR_BUTTON_CONFIRM = SOUND_DIR + "button_confirm.mp3";
    public static final String SOUND_DIR_BUTTON_DENY   = SOUND_DIR + "button_deny.mp3";
    public static final String SOUND_DIR_BUTTON_MENU = SOUND_DIR + "button_menu.mp3";
    public static final String SOUND_DIR_DICE_THROW_1 = SOUND_DIR + "dice_throw_1.mp3";
    public static final String SOUND_DIR_DICE_THROW_2 = SOUND_DIR + "dice_throw_2.mp3";
    public static final String SOUND_DIR_DICE_THROW_3 = SOUND_DIR + "dice_throw_3.mp3";
    public static final String SOUND_DIR_DICE_THROW_4 = SOUND_DIR + "dice_throw_4.mp3";
    public static final String SOUND_DIR_DICE_SHUFFLE_1 = SOUND_DIR + "die_shuffle_1.mp3";
    public static final String SOUND_DIR_DICE_SHUFFLE_2 = SOUND_DIR + "die_shuffle_2.mp3";
    public static final String SOUND_DIR_DICE_SHUFFLE_3 = SOUND_DIR + "die_shuffle_3.mp3";
    public static final String SOUND_DIR_GAME_OVER_WIN = SOUND_DIR + "game_over_win.mp3";
    public static final String SOUND_DIR_GAME_OVER_LOSE = SOUND_DIR + "game_over_lose.mp3";
    public static final String SOUND_DIR_HUMAN_INVALID_SELECTION = SOUND_DIR + "human_invalid_selection.mp3";
    public static final String SOUND_DIR_HUMAN_TURN = SOUND_DIR + "human_turn.mp3";

    public static final int DICE_SHUFFLE_TOTAL = 3;
    public static final int DICE_THROW_TOTAL = 4;

    public Manager() {

        this.assets = new AssetManager();

        int indexLanguage = MyPreferences.getInt(MyPreferences.PREFS_LANGUAGE, MyGdxGameHelper.UNASSIGNED);

        Language.Languages language = Language.Languages.values()[MyPreferences.getInt(MyPreferences.PREFS_LANGUAGE, Language.LANGUAGE_DEFAULT_INDEX)];

        //if there is no language assigned, let's try to auto-detect
        if (indexLanguage == MyGdxGameHelper.UNASSIGNED) {
            language = Language.changeLanguage(this, indexLanguage);
        }

        //load language text
        loadLanguage(language);

        //load atlas assets
        loadAtlas(ATLAS_DIR_ANIMATION_1);
        loadAtlas(ATLAS_DIR_SPRITESHEET);

        //load game music
        loadMusic(MUSIC_DIR_GAME_1);
        loadMusic(MUSIC_DIR_GAME_2);
        loadMusic(MUSIC_DIR_GAME_3);
        loadMusic(MUSIC_DIR_GAME_4);
        loadMusic(MUSIC_DIR_GAME_5);
        loadSound(MUSIC_DIR_INTRO);

        //load sound effects
        loadSound(SOUND_DIR_BUTTON_CONFIRM);
        loadSound(SOUND_DIR_BUTTON_DENY);
        loadSound(SOUND_DIR_BUTTON_MENU);
        loadSound(SOUND_DIR_DICE_THROW_1);
        loadSound(SOUND_DIR_DICE_THROW_2);
        loadSound(SOUND_DIR_DICE_THROW_3);
        loadSound(SOUND_DIR_DICE_THROW_4);
        loadSound(SOUND_DIR_DICE_SHUFFLE_1);
        loadSound(SOUND_DIR_DICE_SHUFFLE_2);
        loadSound(SOUND_DIR_DICE_SHUFFLE_3);
        loadSound(SOUND_DIR_GAME_OVER_WIN);
        loadSound(SOUND_DIR_GAME_OVER_LOSE);
        loadSound(SOUND_DIR_HUMAN_INVALID_SELECTION);
        loadSound(SOUND_DIR_HUMAN_TURN);
    }

    private void setSongIndex(int songIndex) {
        this.songIndex = songIndex;
    }

    private int getSongIndex() {
        return this.songIndex;
    }

    private Language.Languages getLanguage() {
        return this.language;
    }

    private void setLanguage(Language.Languages language) {
        this.language = language;
    }

    private void setSkinPath(String skinPath) {
        this.skinPath = skinPath;
    }

    private String getSkinPath() {
        return this.skinPath;
    }

    public TextureAtlas getAtlas(String key) {
        return getAssets().get(key);
    }

    public void loadAtlas(String path) {
        getAssets().load(path, TextureAtlas.class);
    }

    public void loadSound(String path) {
        getAssets().load(path, Sound.class);
    }

    public void loadMusic(String path) {
        getAssets().load(path, Music.class);
    }

    public void playIntro() {
        playSound(MUSIC_DIR_INTRO);
    }

    public void playSong() {

        if (getSongIndex() == MyGdxGameHelper.UNASSIGNED)
            setSongIndex(MyGdxGameHelper.getRandom().nextInt(SONG_COUNT));

        playSong(getSongIndex());
    }

    public void playSong(int index) {

        switch (index) {
            default:
            case SONG_INDEX_1:
                playMusic(MUSIC_DIR_GAME_1);
                break;

            case SONG_INDEX_2:
                playMusic(MUSIC_DIR_GAME_2);
                break;

            case SONG_INDEX_3:
                playMusic(MUSIC_DIR_GAME_3);
                break;

            case SONG_INDEX_4:
                playMusic(MUSIC_DIR_GAME_4);
                break;

            case SONG_INDEX_5:
                playMusic(MUSIC_DIR_GAME_5);
                break;
        }
    }

    public void addMusicCompleteListeners() {
        getMusic(MUSIC_DIR_GAME_1).setOnCompletionListener(new Music.OnCompletionListener() {
            @Override
            public void onCompletion(Music music) {
                setSongIndex(SONG_INDEX_1);
                playSong(SONG_INDEX_1);
            }
        });

        getMusic(MUSIC_DIR_GAME_2).setOnCompletionListener(new Music.OnCompletionListener() {
            @Override
            public void onCompletion(Music music) {
                setSongIndex(SONG_INDEX_2);
                playSong(SONG_INDEX_2);
            }
        });

        getMusic(MUSIC_DIR_GAME_3).setOnCompletionListener(new Music.OnCompletionListener() {
            @Override
            public void onCompletion(Music music) {
                setSongIndex(SONG_INDEX_3);
                playSong(SONG_INDEX_3);
            }
        });

        getMusic(MUSIC_DIR_GAME_4).setOnCompletionListener(new Music.OnCompletionListener() {
            @Override
            public void onCompletion(Music music) {
                setSongIndex(SONG_INDEX_4);
                playSong(SONG_INDEX_4);
            }
        });

        getMusic(MUSIC_DIR_GAME_5).setOnCompletionListener(new Music.OnCompletionListener() {
            @Override
            public void onCompletion(Music music) {
                setSongIndex(SONG_INDEX_5);
                playSong(SONG_INDEX_5);
            }
        });
    }

    private Music getMusic(String key) {
        return getAssets().get(key, Music.class);
    }

    private void playMusic(String key) {

        //don't play anything if sound is off
        if (!MyPreferences.isBoolean(MyPreferences.PREFS_AUDIO_ENABLED))
            return;

        Music music = getMusic(key);

        //don't continue if already playing
        if (music.isPlaying())
            return;

        stopMusic();
        music.setVolume(1.0f);
        music.play();
        music.setPosition(0f);
    }

    public void playSound(String key) {

        //don't play anything if sound is off
        if (!MyPreferences.isBoolean(MyPreferences.PREFS_AUDIO_ENABLED))
            return;

        getAssets().get(key, Sound.class).play();
    }

    public void playSoundDiceThrow(GameScreen screen) {
        if (screen.getPlayers().getPlayerCurrent().isHuman() || !screen.getPlayers().getStatus().hasSpeed()) {
            switch (MyGdxGameHelper.getRandom().nextInt(DICE_THROW_TOTAL)) {
                default:
                case 0:
                    playSound(Manager.SOUND_DIR_DICE_THROW_1);
                    break;

                case 1:
                    playSound(Manager.SOUND_DIR_DICE_THROW_2);
                    break;

                case 2:
                    playSound(Manager.SOUND_DIR_DICE_THROW_3);
                    break;

                case 3:
                    playSound(Manager.SOUND_DIR_DICE_THROW_4);
                    break;
            }
        }
    }

    public void playSoundShuffleDice(GameScreen screen) {
        if (screen.getPlayers().getPlayerCurrent().isHuman() || !screen.getPlayers().getStatus().hasSpeed()) {
            switch (MyGdxGameHelper.getRandom().nextInt(DICE_SHUFFLE_TOTAL)) {
                default:
                case 0:
                    playSound(Manager.SOUND_DIR_DICE_SHUFFLE_1);
                    break;

                case 1:
                    playSound(Manager.SOUND_DIR_DICE_SHUFFLE_2);
                    break;

                case 2:
                    playSound(Manager.SOUND_DIR_DICE_SHUFFLE_3);
                    break;
            }
        }
    }

    public void stopSoundDiceShuffle() {
        stopSound(SOUND_DIR_DICE_SHUFFLE_1);
        stopSound(SOUND_DIR_DICE_SHUFFLE_2);
        stopSound(SOUND_DIR_DICE_SHUFFLE_3);
    }

    //stop everything
    public void stopAllSound() {
        stopSound(SOUND_DIR_GAME_OVER_LOSE);
        stopSound(SOUND_DIR_GAME_OVER_WIN);
        stopSound(SOUND_DIR_DICE_SHUFFLE_3);
        stopSound(SOUND_DIR_DICE_SHUFFLE_2);
        stopSound(SOUND_DIR_DICE_SHUFFLE_1);
        stopSound(SOUND_DIR_DICE_THROW_4);
        stopSound(SOUND_DIR_DICE_THROW_3);
        stopSound(SOUND_DIR_DICE_THROW_2);
        stopSound(SOUND_DIR_DICE_THROW_1);
        stopSound(SOUND_DIR_BUTTON_MENU);
        stopSound(SOUND_DIR_BUTTON_DENY);
        stopSound(SOUND_DIR_BUTTON_CONFIRM);
        stopSound(SOUND_DIR_HUMAN_INVALID_SELECTION);
        stopSound(SOUND_DIR_HUMAN_TURN);
        stopSound(MUSIC_DIR_INTRO);
        stopMusic(MUSIC_DIR_GAME_1);
        stopMusic(MUSIC_DIR_GAME_2);
        stopMusic(MUSIC_DIR_GAME_3);
        stopMusic(MUSIC_DIR_GAME_4);
        stopMusic(MUSIC_DIR_GAME_5);

        //determine next song to play when volume is enabled again
        switch (getSongIndex()) {
            case SONG_INDEX_1:
                setSongIndex(SONG_INDEX_2);
                break;

            case SONG_INDEX_2:
                setSongIndex(SONG_INDEX_3);
                break;

            case SONG_INDEX_3:
                setSongIndex(SONG_INDEX_4);
                break;

            case SONG_INDEX_4:
                setSongIndex(SONG_INDEX_5);
                break;

            case SONG_INDEX_5:
                setSongIndex(SONG_INDEX_1);
                break;
        }
    }

    public void stopMusic() {
        stopMusic(MUSIC_DIR_GAME_1);
        stopMusic(MUSIC_DIR_GAME_2);
        stopMusic(MUSIC_DIR_GAME_3);
        stopMusic(MUSIC_DIR_GAME_4);
        stopMusic(MUSIC_DIR_GAME_5);
    }

    public void stopSound(String key) {
        getAssets().get(key, Sound.class).stop();
    }

    public void stopMusic(String key) {
        getMusic(key).stop();
    }

    public void loadLanguage(Language.Languages language) {
        //load the skin with the language characters first
        setLanguage(language);
        loadSkin(getLanguage().getPathSkin());
        loadBundle(getLanguage());
    }

    public String getTranslatedText(String key) {
        return getBundle().get(key);
    }

    private void loadBundle(Language.Languages language) {

        if (getAssets().isLoaded(LANGUAGE_BUNDLE_DIR))
            getAssets().unload(LANGUAGE_BUNDLE_DIR);

        getAssets().load(LANGUAGE_BUNDLE_DIR, I18NBundle.class, new I18NBundleLoader.I18NBundleParameter(new Locale(language.getLocaleStr())));
        getAssets().finishLoadingAsset(LANGUAGE_BUNDLE_DIR);
    }

    public I18NBundle getBundle() {
        return getAssets().get(LANGUAGE_BUNDLE_DIR, I18NBundle.class);
    }

    public void loadSkin(String path) {

        if (getAssets().isLoaded(getSkinPath()))
            getAssets().unload(getSkinPath());

        setSkinPath(path);
        getAssets().load(getSkinPath(), Skin.class);
        getAssets().finishLoadingAsset(getSkinPath());
    }

    public Skin getSkin() {
        return getAssets().get(getSkinPath());
    }

    public boolean isLoading() {
        return (!getAssets().isFinished());
    }

    public float getProgress() {
        return getAssets().getProgress();
    }

    public boolean update() {
        return getAssets().update();
    }

    private AssetManager getAssets() {
        return this.assets;
    }

    @Override
    public void dispose() {
        if (this.assets != null)
            this.assets.dispose();

        this.assets = null;
    }
}