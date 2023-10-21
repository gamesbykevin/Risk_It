package com.gamesbykevin.riskit.util;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.gamesbykevin.riskit.assets.Manager;
import com.gamesbykevin.riskit.preferences.MyPreferences;
import com.gamesbykevin.riskit.screen.dialog.Dialogs;
import com.gamesbykevin.riskit.localization.Language;
import com.gamesbykevin.riskit.screen.GameScreen;
import com.gamesbykevin.riskit.screen.ParentScreen;

public class DialogHelper {

    public static final float TRANSPARENCY = .75f;
    public static final int DIALOG_WIDTH = 400;
    public static final int DIALOG_HEIGHT = 258;

    public static final int BUTTON_WIDTH = 100;
    public static final int BUTTON_HEIGHT = 48;

    public static final void createDialogs(GameScreen screen) {
        createDialog(screen);
        createPauseDialog(screen);
        screen.getDialogs().setType(null);
    }

    private static void createDialog(GameScreen screen) {

        Dialogs dialogs = screen.getDialogs();
        Skin skin = screen.getGame().getAssetManager().getSkin();
        Manager assetManager = screen.getGame().getAssetManager();

        Image dialogWindow = new Image(skin.getDrawable(MyGdxGameHelper.DRAWABLE_HORIZONTAL_WINDOW_4));
        dialogWindow.setSize(DIALOG_WIDTH, DIALOG_HEIGHT);
        MyGdxGameHelper.centerActor(dialogWindow);
        dialogs.setDialogWindow(dialogWindow);

        Label dialogLabel = new Label("", skin, MyGdxGameHelper.FONT_44, MyGdxGameHelper.STYLE_COLOR);

        dialogs.setFontWidthSave(new GlyphLayout(dialogLabel.getStyle().font, assetManager.getTranslatedText(Language.KEY_LABEL_SAVE)).width);
        dialogs.setFontWidthContinue(new GlyphLayout(dialogLabel.getStyle().font, assetManager.getTranslatedText(Language.KEY_LABEL_CONTINUE)).width);
        dialogs.setFontWidthWin(new GlyphLayout(dialogLabel.getStyle().font, assetManager.getTranslatedText(Language.KEY_LABEL_WIN)).width);
        dialogs.setFontWidthLose(new GlyphLayout(dialogLabel.getStyle().font, assetManager.getTranslatedText(Language.KEY_LABEL_LOSE)).width);

        MyGdxGameHelper.centerActorY(dialogLabel);
        dialogs.setDialogLabel(dialogLabel);

        TextButton buttonConfirm = new TextButton(assetManager.getTranslatedText(Language.KEY_BUTTON_CONFIRM), skin, MyGdxGameHelper.STYLE_BUTTON_EMPTY_100);
        buttonConfirm.setSize(BUTTON_WIDTH, BUTTON_HEIGHT);
        buttonConfirm.setPosition(dialogWindow.getX(), dialogWindow.getY() - BUTTON_HEIGHT - (StatusHelper.PADDING * 2));
        buttonConfirm.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {

                screen.getGame().getAssetManager().playSound(Manager.SOUND_DIR_BUTTON_CONFIRM);

                switch (screen.getDialogs().getType()) {
                    case Continue:
                        Gdx.app.log("DialogHelper", "Continue clicked");
                        screen.getDialogs().setType(null);
                        break;

                    case Save:
                        Gdx.app.log("DialogHelper", "Save clicked");
                        MyPreferences.saveGame(screen);
                        Gdx.app.log("DialogHelper", "Preferences saved");
                        screen.getGame().selectScreen(ParentScreen.Screens.MainMenu);
                        screen.getGame().getScreen(ParentScreen.Screens.GameScreen).dispose();
                        break;
                }
            }
        });
        dialogs.setButtonConfirm(buttonConfirm);
        screen.getStage().addActor(dialogs.getButtonConfirm());

        TextButton buttonDecline = new TextButton(assetManager.getTranslatedText(Language.KEY_BUTTON_DECLINE), skin, MyGdxGameHelper.STYLE_BUTTON_EMPTY_100);
        buttonDecline.setSize(BUTTON_WIDTH, BUTTON_HEIGHT);
        buttonDecline.setPosition(dialogWindow.getX() + dialogWindow.getWidth() - BUTTON_WIDTH, dialogWindow.getY() - BUTTON_HEIGHT - (StatusHelper.PADDING * 2));
        buttonDecline.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                Gdx.app.log("DialogHelper", "Decline clicked");
                screen.getGame().getAssetManager().playSound(Manager.SOUND_DIR_BUTTON_DENY);
                screen.getGame().selectScreen(ParentScreen.Screens.MainMenu);
                screen.getGame().dispose();
            };
        });
        dialogs.setButtonDecline(buttonDecline);
        screen.getStage().addActor(dialogs.getButtonDecline());

        TextButton buttonExit = new TextButton(assetManager.getTranslatedText(Language.KEY_BUTTON_EXIT), skin, MyGdxGameHelper.STYLE_BUTTON_EMPTY_100);
        buttonExit.setSize(BUTTON_WIDTH, BUTTON_HEIGHT);
        MyGdxGameHelper.centerActorX(buttonExit);
        buttonExit.setY(dialogWindow.getY() - BUTTON_HEIGHT - (StatusHelper.PADDING * 2));
        buttonExit.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                Gdx.app.log("DialogHelper", "Exit clicked");
                screen.getGame().getAssetManager().playSound(Manager.SOUND_DIR_BUTTON_CONFIRM);
                screen.getGame().selectScreen(ParentScreen.Screens.MainMenu);
                screen.getGame().dispose();
            };
        });
        dialogs.setButtonExit(buttonExit);
        screen.getStage().addActor(dialogs.getButtonExit());
    }

    private static void createPauseDialog(GameScreen screen) {

        Skin skin = screen.getGame().getScreen().getSkin();

        Label pausedLabel = new Label(screen.getGame().getAssetManager().getTranslatedText(Language.KEY_LABEL_PAUSED), skin, MyGdxGameHelper.FONT_44, MyGdxGameHelper.STYLE_COLOR);
        screen.getDialogs().setPausedLabel(pausedLabel);
        MyGdxGameHelper.centerActor(screen.getDialogs().getPausedLabel());

        Pixmap pixmap = new Pixmap(1,1, Pixmap.Format.RGBA8888);
        pixmap.setColor(Color.BLACK);
        pixmap.fillRectangle(0, 0, 1, 1);
        Texture texture = new Texture(pixmap);
        pixmap.dispose();

        Image transparentImage = new Image(texture);
        transparentImage.setSize(MyGdxGameHelper.WIDTH, MyGdxGameHelper.HEIGHT);
        transparentImage.getColor().a = TRANSPARENCY;
        screen.getDialogs().setTransparentImage(transparentImage);
    }
}