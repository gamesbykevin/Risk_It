package com.gamesbykevin.riskit.screen.dialog;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.gamesbykevin.riskit.MyGdxGame;
import com.gamesbykevin.riskit.util.IMyGdxGame;
import com.gamesbykevin.riskit.util.MyGdxGameHelper;
import com.gamesbykevin.riskit.localization.Language;

public class Dialogs implements IMyGdxGame {

    private final MyGdxGame game;

    private Image transparentImage, dialogWindow;
    private Button buttonConfirm, buttonDecline, buttonExit;
    private Label pausedLabel, dialogLabel;

    private float fontWidthWin, fontWidthLose, fontWidthContinue, fontWidthSave;

    private boolean paused;

    //did we prompt to continue after all humans died?
    private boolean promptContinue;

    public enum Type {
        Save,       //if you exit during gameplay
        End,        //game is completely over
        Continue    //if all humans are dead, do you still want to watch?
    }

    private Type type;

    public Dialogs(MyGdxGame game) {
        this.game = game;
        setPaused(false);
        setPromptContinue(false);
    }

    public MyGdxGame getGame() {
        return this.game;
    }

    public boolean hasPromptContinue() {
        return this.promptContinue;
    }

    public void setPromptContinue(boolean promptContinue) {
        this.promptContinue = promptContinue;
    }

    public float getFontWidthContinue() {
        return this.fontWidthContinue;
    }

    public float getFontWidthLose() {
        return this.fontWidthLose;
    }

    public float getFontWidthSave() {
        return this.fontWidthSave;
    }

    public float getFontWidthWin() {
        return this.fontWidthWin;
    }

    public void setFontWidthContinue(float fontWidthContinue) {
        this.fontWidthContinue = fontWidthContinue;
    }

    public void setFontWidthLose(float fontWidthLose) {
        this.fontWidthLose = fontWidthLose;
    }

    public void setFontWidthSave(float fontWidthSave) {
        this.fontWidthSave = fontWidthSave;
    }

    public void setFontWidthWin(float fontWidthWin) {
        this.fontWidthWin = fontWidthWin;
    }

    public Button getButtonExit() {
        return this.buttonExit;
    }

    public Button getButtonConfirm() {
        return this.buttonConfirm;
    }

    public Button getButtonDecline() {
        return this.buttonDecline;
    }

    public Label getDialogLabel() {
        return this.dialogLabel;
    }

    public Image getDialogWindow() {
        return this.dialogWindow;
    }

    public Type getType() {
        return this.type;
    }

    public void setButtonExit(Button buttonExit) {
        this.buttonExit = buttonExit;
    }

    public void setButtonConfirm(Button buttonConfirm) {
        this.buttonConfirm = buttonConfirm;
    }

    public void setButtonDecline(Button buttonDecline) {
        this.buttonDecline = buttonDecline;
    }

    public void setDialogLabel(Label dialogLabel) {
        this.dialogLabel = dialogLabel;
    }

    public void setDialogWindow(Image dialogWindow) {
        this.dialogWindow = dialogWindow;
    }

    public void setType(Type type) {
        this.type = type;

        if (this.type == null) {
            getDialogWindow().setVisible(false);
            getDialogLabel().setVisible(false);
            getButtonConfirm().setVisible(false);
            getButtonDecline().setVisible(false);
            getButtonExit().setVisible(false);
        } else {

            getDialogWindow().setVisible(true);
            getDialogLabel().setVisible(true);

            switch (getType()) {

                case Save:
                    getButtonConfirm().setVisible(true);
                    getButtonDecline().setVisible(true);
                    getButtonExit().setVisible(false);
                    getDialogLabel().setText(getGame().getAssetManager().getTranslatedText(Language.KEY_LABEL_SAVE));
                    getDialogLabel().setX((MyGdxGameHelper.WIDTH / 2) - (getFontWidthSave() / 2));
                    break;

                case Continue:
                    getButtonConfirm().setVisible(true);
                    getButtonDecline().setVisible(true);
                    getButtonExit().setVisible(false);
                    getDialogLabel().setText(getGame().getAssetManager().getTranslatedText(Language.KEY_LABEL_CONTINUE));
                    getDialogLabel().setX((MyGdxGameHelper.WIDTH / 2) - (getFontWidthContinue() / 2));
                    break;

                case End:
                    getButtonConfirm().setVisible(false);
                    getButtonDecline().setVisible(false);
                    getButtonExit().setVisible(true);
                    break;
            }
        }
    }

    public boolean hasDialog() {
        return isPaused() || getType() != null;
    }

    public Label getPausedLabel() {
        return this.pausedLabel;
    }

    public void setPausedLabel(Label pausedLabel) {
        this.pausedLabel = pausedLabel;
    }

    public void setPaused(boolean paused) {
        this.paused = paused;
    }

    public boolean isPaused() {
        return this.paused;
    }

    public void setTransparentImage(Image transparentImage) {
        this.transparentImage = transparentImage;
    }

    public Image getTransparentImage() {
        return this.transparentImage;
    }

    public void render(Batch batch) {

        if (isPaused()) {
            getTransparentImage().draw(batch, 1f);
            getPausedLabel().draw(batch, 1f);
        } else {

            if (hasDialog()) {
                getTransparentImage().draw(batch, 1f);
                getDialogWindow().draw(batch, 1f);
                getDialogLabel().draw(batch, 1f);

                if (getButtonConfirm().isVisible())
                    getButtonConfirm().draw(batch, 1f);
                if (getButtonDecline().isVisible())
                    getButtonDecline().draw(batch, 1f);
                if (getButtonExit().isVisible())
                    getButtonExit().draw(batch, 1f);
            }
        }
    }

    @Override
    public void dispose() {
        this.transparentImage = null;
        this.dialogWindow = null;
        this.buttonConfirm = null;
        this.buttonDecline = null;
        this.buttonExit = null;
        this.pausedLabel = null;
        this.dialogLabel = null;
    }
}