package com.gamesbykevin.riskit.localization;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.loaders.I18NBundleLoader;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.I18NBundle;
import com.gamesbykevin.riskit.assets.Manager;
import com.gamesbykevin.riskit.preferences.MyPreferences;
import com.gamesbykevin.riskit.util.MyGdxGameHelper;

import java.util.Locale;

public class Language {

    public static final int LANGUAGE_DEFAULT_INDEX = 2;

    public static final String KEY_BUTTON_PLAY = "buttonPlay";
    public static final String KEY_BUTTON_RESUME = "buttonResume";
    public static final String KEY_BUTTON_RATE = "buttonRate";
    public static final String KEY_BUTTON_TUTORIAL = "buttonTutorial";
    public static final String KEY_BUTTON_TROPHIES = "buttonTrophies";
    public static final String KEY_BUTTON_BACK = "buttonBack";
    public static final String KEY_BUTTON_START = "buttonStart";
    public static final String KEY_LABEL_SETTINGS = "labelSettings";
    public static final String KEY_LABEL_PAUSED = "labelPaused";
    public static final String KEY_LABEL_PLAYERS = "labelPlayers";
    public static final String KEY_LABEL_CONTINUE = "labelContinue";
    public static final String KEY_LABEL_SAVE = "labelSave";
    public static final String KEY_LABEL_WIN = "labelWin";
    public static final String KEY_LABEL_LOSE = "labelLose";
    public static final String KEY_LABEL_DIFFICULTY = "labelDifficulty";
    public static final String KEY_LABEL_ORDER = "labelOrder";
    public static final String KEY_LABEL_SIZE = "labelSize";
    public static final String KEY_BUTTON_HUMAN = "buttonHuman";
    public static final String KEY_BUTTON_RANDOM = "buttonRandom";
    public static final String KEY_BUTTON_SMALL = "buttonSmall";
    public static final String KEY_BUTTON_LARGE = "buttonLarge";
    public static final String KEY_BUTTON_EASY = "buttonEasy";
    public static final String KEY_BUTTON_MED = "buttonMedium";
    public static final String KEY_BUTTON_HARD = "buttonHard";
    public static final String KEY_BUTTON_CONFIRM = "buttonConfirm";
    public static final String KEY_BUTTON_DECLINE = "buttonDecline";
    public static final String KEY_BUTTON_EXIT = "buttonExit";

    public static final String FILENAME_SKIN = "skin.json";
    public static final String FILENAME_FONT = "font_16.fnt";

    public enum Languages {
        Arabic("language_arabic", "skins/arabic/", "ar", 650, 90),
        Chinese("language_chinese", "skins/chinese/", "zh", 679, 90),
        English("language_english", "skins/default/", "en", 681, 90),
        French("language_french", "skins/french/", "fr", 652, 87),
        German("language_german", "skins/german/", "de", 698, 89),
        Hindi("language_hindi", "skins/hindi/", "hi", 609, 90),
        Indonesian("language_indonesian","skins/indonesian/", "in", 799, 90),
        Italian("language_italian","skins/italian/", "it", 664, 90),
        Japanese("language_japanese", "skins/japanese/", "ja", 733, 86),
        Korean("language_korean","skins/korean/", "ko", 660, 86),
        Portuguese("language_portuguese", "skins/spanish/", "pt", 800, 86),
        Russian("language_russian", "skins/russian/", "ru", 689, 90),
        Spanish("language_spanish", "skins/spanish/", "es", 694, 90),
        Urdu("language_urdu","skins/urdu/", "ur", 585, 87)
        ;
        private final String style;
        private final String pathSkin;
        private final String localeStr;
        private final float w, h;

        Languages(String style, String pathSkin, String localeStr, float w, float h) {
            this.style = style;
            this.pathSkin = pathSkin;
            this.localeStr = localeStr;
            this.w = w;
            this.h = h;
        }

        public String getLocaleStr() {
            return this.localeStr;
        }

        public String getPathSkin() {
            return this.pathSkin + FILENAME_SKIN;
        }

        public String getPathFont() {
            return this.pathSkin + FILENAME_FONT;
        }

        public String getStyle() {
            return this.style;
        }

        public float getH() {
            return this.h;
        }

        public float getW() {
            return this.w;
        }
    }

    public static Languages changeLanguage(int index) {
        return changeLanguage(null, index);
    }

    public static Languages changeLanguage(Manager manager, int index) {
        Languages[] languages = Languages.values();

        //if no language is saved, let's see if we can detect
        if (index == MyGdxGameHelper.UNASSIGNED && manager != null) {

            //if we can't find we will default English
            index = LANGUAGE_DEFAULT_INDEX;
            Gdx.app.log("Language", "no language set, auto-detecting");

            //get system locale
            String locale = java.util.Locale.getDefault().toString().toLowerCase();
            Gdx.app.log("Language", "system locale detected: " + locale);

            //let's see if we can auto-detect language
            for (int i = 0; i < languages.length; i++) {
                Languages tmpLanguage = languages[i];

                if (locale.indexOf(tmpLanguage.getLocaleStr().toLowerCase()) == 0) {
                    Gdx.app.log("Language", "locale match: " + tmpLanguage.getLocaleStr());
                    manager.loadLanguage(tmpLanguage);

                    //verify we have all characters present
                    if (verify(manager.getBundle(), tmpLanguage)) {
                        Gdx.app.log("Language", "locale verified");
                        index = i;
                        break;
                    } else {
                        Gdx.app.log("Language", "locale verification failed");
                    }
                }
            }
        }

        //keep index in bounds
        if (index < 0)
            index = languages.length - 1;
        if (index >= languages.length)
            index = 0;

        Gdx.app.log("Language", "locale chosen: " + languages[index].getLocaleStr());
        MyPreferences.saveInt(MyPreferences.PREFS_LANGUAGE, index);

        return Languages.values()[index];
    }

    private static boolean verify(I18NBundle bundle, Languages language) {

        //now check bitmap font file to see all used characters
        FileHandle handle = Gdx.files.internal(language.getPathFont());
        String text = handle.readString();
        String fontFileData[] = text.split("\\r\\n");

        if (!verifyString(bundle, language, fontFileData, KEY_BUTTON_PLAY))
            return false;
        if (!verifyString(bundle, language, fontFileData, KEY_LABEL_SIZE))
            return false;
        if (!verifyString(bundle, language, fontFileData, KEY_BUTTON_HUMAN))
            return false;
        if (!verifyString(bundle, language, fontFileData, KEY_BUTTON_RANDOM))
            return false;
        if (!verifyString(bundle, language, fontFileData, KEY_BUTTON_SMALL))
            return false;
        if (!verifyString(bundle, language, fontFileData, KEY_BUTTON_LARGE))
            return false;
        if (!verifyString(bundle, language, fontFileData, KEY_BUTTON_EASY))
            return false;
        if (!verifyString(bundle, language, fontFileData, KEY_BUTTON_MED))
            return false;
        if (!verifyString(bundle, language, fontFileData, KEY_BUTTON_HARD))
            return false;
        if (!verifyString(bundle, language, fontFileData, KEY_BUTTON_CONFIRM))
            return false;
        if (!verifyString(bundle, language, fontFileData, KEY_BUTTON_DECLINE))
            return false;
        if (!verifyString(bundle, language, fontFileData, KEY_BUTTON_EXIT))
            return false;
        if (!verifyString(bundle, language, fontFileData, KEY_LABEL_ORDER))
            return false;
        if (!verifyString(bundle, language, fontFileData, KEY_LABEL_DIFFICULTY))
            return false;
        if (!verifyString(bundle, language, fontFileData, KEY_LABEL_LOSE))
            return false;
        if (!verifyString(bundle, language, fontFileData, KEY_LABEL_WIN))
            return false;
        if (!verifyString(bundle, language, fontFileData, KEY_LABEL_SAVE))
            return false;
        if (!verifyString(bundle, language, fontFileData, KEY_LABEL_CONTINUE))
            return false;
        if (!verifyString(bundle, language, fontFileData, KEY_LABEL_PLAYERS))
            return false;
        if (!verifyString(bundle, language, fontFileData, KEY_LABEL_PAUSED))
            return false;
        if (!verifyString(bundle, language, fontFileData, KEY_LABEL_SETTINGS))
            return false;
        if (!verifyString(bundle, language, fontFileData, KEY_BUTTON_START))
            return false;
        if (!verifyString(bundle, language, fontFileData, KEY_BUTTON_BACK))
            return false;
        if (!verifyString(bundle, language, fontFileData, KEY_BUTTON_TROPHIES))
            return false;
        if (!verifyString(bundle, language, fontFileData, KEY_BUTTON_TUTORIAL))
            return false;
        if (!verifyString(bundle, language, fontFileData, KEY_BUTTON_RATE))
            return false;
        if (!verifyString(bundle, language, fontFileData, KEY_BUTTON_RESUME))
            return false;

        return true;
    }

    private static boolean verifyString(I18NBundle bundle, Languages language, String fontFileData[], String key) {
        String value = bundle.get(key);

        for (int index = 0; index < value.length(); index++) {
            int charCode = value.charAt(index);

            boolean hasCode = false;

            for (int i = 0; i < fontFileData.length; i++) {
                String line = fontFileData[i];

                if (line.indexOf("char id=") < 0)
                    continue;

                int x = Integer.parseInt(line.substring(8, 13).trim());

                if (x == charCode) {
                    hasCode = true;
                    //System.out.println("Found char code: " + charCode);
                    break;
                }
            }

            if (!hasCode) {
                Gdx.app.log("Language", "code not found: " + charCode);
                Gdx.app.log("Language", "lang - " + language.getPathFont());
                return false;
            }
        }

        return true;
    }
}