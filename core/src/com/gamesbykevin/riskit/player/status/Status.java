package com.gamesbykevin.riskit.player.status;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.gamesbykevin.riskit.assets.Manager;
import com.gamesbykevin.riskit.gameservices.Achievements;
import com.gamesbykevin.riskit.preferences.MyPreferences;
import com.gamesbykevin.riskit.util.IMyGdxGame;
import com.gamesbykevin.riskit.util.MyGdxGameHelper;
import com.gamesbykevin.riskit.localization.Language;
import com.gamesbykevin.riskit.player.Player;
import com.gamesbykevin.riskit.player.Players;
import com.gamesbykevin.riskit.screen.GameScreen;
import com.gamesbykevin.riskit.screen.dialog.Dialogs;
import com.gamesbykevin.riskit.sprites.Sprite;
import com.gamesbykevin.riskit.territory.Territory;
import com.gamesbykevin.riskit.util.StatusHelper;

public class Status implements IMyGdxGame {

    //are we going fast?
    private boolean speed;

    //are we playing a saved game?
    private boolean resume;

    //what are we currently doing
    public enum State {
        Thinking,
        Challenge,
        Result,
        Regenerate
    }

    private State state;

    //UI needed to display challenge
    private Image playerStatusAttack, playerStatusDefend;
    private Label playerCountAttackLabel, playerCountDefendLabel;
    private Image[] playerDiceAttack, playerDiceDefend;

    //these are the animated dice we roll when challenging
    private Sprite[] diceRollAttack, diceRollDefend;

    private float[] playerDiceRenderX, playerDiceRenderY;
    private float[] playerStatusRenderX, playerStatusRenderY;
    private float[] playerCountRenderX, playerCountRenderY;
    private Image[] statusImage, statusImageHighlighted, diceImage;
    private Label[] countLabel;

    private Image[] diceRemainingImage;

    private Button playerButtonRest, exitButton, buttonSpeed;

    //track time lapsed
    private float delta = 0f;

    //normal speed
    public static final float CHALLENGE_DURATION = .75f;
    public static final float RESULT_DURATION = 1f;
    public static final float REGENERATE_DURATION = .175f;

    //fast speed
    public static final float CHALLENGE_DURATION_SPEED = .1f;
    public static final float RESULT_DURATION_SPEED = .1f;
    public static final float REGENERATE_DURATION_SPEED = .0075f;

    public Status(Players players) {

        //default to this
        setState(State.Thinking);

        //flag false for now
        setResume(false);

        this.diceRemainingImage = new Image[StatusHelper.SPAWN_DICE_RENDER_MAX];

        this.diceRollAttack = new Sprite[Territory.DICE_MAX];
        this.diceRollDefend = new Sprite[Territory.DICE_MAX];

        int count = players.getPlayerCount();

        this.countLabel = new Label[count];

        this.diceImage = new Image[count];
        this.statusImage = new Image[count];
        this.statusImageHighlighted = new Image[count];

        this.playerDiceAttack = new Image[count];
        this.playerDiceDefend = new Image[count];

        this.playerDiceRenderX = new float[count];
        this.playerDiceRenderY = new float[count];

        this.playerStatusRenderX = new float[count];
        this.playerStatusRenderY = new float[count];

        this.playerCountRenderX = new float[count];
        this.playerCountRenderY = new float[count];
    }

    public boolean hasResume() {
        return this.resume;
    }

    public void setResume(boolean resume) {
        this.resume = resume;
    }

    public boolean hasState(State state) {
        return (getState() == state);
    }

    public void setState(State state) {
        this.state = state;
    }

    public State getState() {
        return this.state;
    }

    public boolean hasSpeed() {
        return this.speed;
    }

    public void setSpeed(boolean speed) {
        this.speed = speed;
    }

    public Button getButtonSpeed() {
        return this.buttonSpeed;
    }

    public void setButtonSpeed(Button buttonSpeed) {
        this.buttonSpeed = buttonSpeed;
    }

    public void setExitButton(Button exitButton) {
        this.exitButton = exitButton;
    }

    public Button getExitButton() {
        return this.exitButton;
    }

    public Button getPlayerButtonRest() {
        return this.playerButtonRest;
    }

    public void setPlayerButtonRest(Button playerButtonRest) {
        this.playerButtonRest = playerButtonRest;
    }

    public Image[] getDiceRemainingImage() {
        return this.diceRemainingImage;
    }

    public Image[] getDiceImage() {
        return this.diceImage;
    }

    public Label[] getCountLabel() {
        return this.countLabel;
    }

    public Image[] getStatusImage() {
        return this.statusImage;
    }

    public Image[] getStatusImageHighlighted() {
        return this.statusImageHighlighted;
    }

    public float[] getPlayerDiceRenderX() {
        return this.playerDiceRenderX;
    }

    public float[] getPlayerDiceRenderY() {
        return this.playerDiceRenderY;
    }

    public float[] getPlayerStatusRenderX() {
        return this.playerStatusRenderX;
    }

    public float[] getPlayerStatusRenderY() {
        return this.playerStatusRenderY;
    }

    public float[] getPlayerCountRenderX() {
        return this.playerCountRenderX;
    }

    public float[] getPlayerCountRenderY() {
        return this.playerCountRenderY;
    }

    public Sprite[] getDiceRollAttack() {
        return this.diceRollAttack;
    }

    public Sprite[] getDiceRollDefend() {
        return this.diceRollDefend;
    }

    public Image getPlayerStatusAttack() {
        return this.playerStatusAttack;
    }

    public void setPlayerStatusAttack(Image playerStatusAttack) {
        this.playerStatusAttack = playerStatusAttack;
    }

    public Image getPlayerStatusDefend() {
        return this.playerStatusDefend;
    }

    public void setPlayerStatusDefend(Image playerStatusDefend) {
        this.playerStatusDefend = playerStatusDefend;
    }

    public void setPlayerCountAttackLabel(Label playerCountAttackLabel) {
        this.playerCountAttackLabel = playerCountAttackLabel;
    }

    public Label getPlayerCountAttackLabel() {
        return playerCountAttackLabel;
    }

    public void setPlayerCountDefendLabel(Label playerCountDefendLabel) {
        this.playerCountDefendLabel = playerCountDefendLabel;
    }

    public Label getPlayerCountDefendLabel() {
        return playerCountDefendLabel;
    }

    public Image[] getPlayerDiceAttack() {
        return this.playerDiceAttack;
    }

    public Image[] getPlayerDiceDefend() {
        return this.playerDiceDefend;
    }

    public float getDelta() {
        return this.delta;
    }

    public void setDelta(float delta) {
        this.delta = delta;
    }

    private void checkSpeed() {

        setDelta(0);

        if (!hasSpeed()) {
            if (getButtonSpeed().isChecked())
                setSpeed(true);
        } else {
            if (!getButtonSpeed().isChecked())
                setSpeed(false);
        }
    }

    public void update(GameScreen screen) {

        Player player = screen.getPlayers().getPlayerCurrent();

        if (hasState(State.Challenge)) {
            final float duration = (hasSpeed() && !player.isHuman()) ? CHALLENGE_DURATION_SPEED : CHALLENGE_DURATION;
            boolean finished = (getDelta() > duration);
            setDelta(getDelta() + Gdx.graphics.getDeltaTime());

            //time passed and we can now display our results
            if (!finished && getDelta() > duration) {
                checkSpeed();
                setState(State.Result);
                screen.getGame().getAssetManager().stopSoundDiceShuffle();
                screen.getGame().getAssetManager().playSoundDiceThrow(screen);
                StatusHelper.completeChallenge(screen);
            }

        } else if (hasState(State.Result)) {

            final float duration = (hasSpeed() && !player.isHuman()) ? RESULT_DURATION_SPEED : RESULT_DURATION;
            boolean finished = (getDelta() > duration);
            setDelta(getDelta() + Gdx.graphics.getDeltaTime());

            //we have shown our results for long enough
            if (!finished && getDelta() >= duration) {
                checkSpeed();
                setState(State.Thinking);
                Players players = screen.getPlayers();

                StatusHelper.updateScreen(players);

                //count all territories to determine if we won the game
                players.countConsecutive(screen.getBoard());

                if (players.isGameOver()) {

                    Dialogs dialogs = screen.getDialogs();

                    if (players.getPlayerCurrent().isHuman()) {
                        screen.getGame().getAssetManager().playSound(Manager.SOUND_DIR_GAME_OVER_WIN);
                        dialogs.getDialogLabel().setText(dialogs.getGame().getAssetManager().getTranslatedText(Language.KEY_LABEL_WIN));
                        dialogs.getDialogLabel().setX((MyGdxGameHelper.WIDTH / 2) - (screen.getDialogs().getFontWidthWin() / 2));
                    } else {
                        screen.getGame().getAssetManager().playSound(Manager.SOUND_DIR_GAME_OVER_LOSE);
                        screen.getDialogs().getDialogLabel().setText(dialogs.getGame().getAssetManager().getTranslatedText(Language.KEY_LABEL_LOSE));
                        screen.getDialogs().getDialogLabel().setX((MyGdxGameHelper.WIDTH / 2) - (screen.getDialogs().getFontWidthLose() / 2));
                    }

                    //remove current saved game if we resumed a saved one
                    if (hasResume()) {
                        MyPreferences.removeSave();
                    }

                    //set type which will align label
                    screen.getDialogs().setType(Dialogs.Type.End);

                    //check to unlock achievements
                    Achievements.update(screen.getGame());

                } else {

                    //if all humans dead prompt to continue?
                    if (players.isHumansDead()) {
                        if (!screen.getDialogs().hasPromptContinue()) {
                            screen.getDialogs().setPromptContinue(true);
                            screen.getDialogs().setType(Dialogs.Type.Continue);
                        }
                    }
                }
            }

        } else if (hasState(State.Regenerate)) {

            final float duration = (hasSpeed() && !player.isHuman()) ? REGENERATE_DURATION_SPEED : REGENERATE_DURATION;
            boolean finished = (getDelta() > duration);
            setDelta(getDelta() + Gdx.graphics.getDeltaTime());

            //if enough time has lapsed
            if (!finished && getDelta() >= duration) {
                checkSpeed();
                Players players = screen.getPlayers();

                if (player.getRemaining() > 0) {

                    //add 1 dice to territory
                    players.populate(player, screen.getBoard().getTerritories(), Territory.DICE_MAX);

                } else {

                    setState(State.Thinking);

                    //change players turn
                    players.setCurrentId(players.getCurrentId() + 1);

                    //keep cycling through players if current one is dead
                    while (players.getPlayerCurrent().isDead()) {
                        players.setCurrentId(players.getCurrentId() + 1);

                        if (players.getCurrentId() >= players.getPlayerCount())
                            players.setCurrentId(0);
                    }

                    if (players.getPlayerCurrent().isHuman()) {
                        players.getPlayerCurrent().setSelected(false);
                        screen.getGame().getAssetManager().playSound(Manager.SOUND_DIR_HUMAN_TURN);
                        MyGdxGameHelper.vibrate(true);

                        int countFull = screen.getBoard().getTerritories().getTerritoryCount(players.getPlayerCurrent().getId(), true);
                        int countAll = screen.getBoard().getTerritories().getTerritoryCount(MyGdxGameHelper.UNASSIGNED, false);

                        //if we have all but 1, and all are full of dice
                        if (countFull >= countAll - 1)
                            Achievements.unlock(screen.getGame().getGameServiceClient(), Achievements.FULL_HOUSE);
                    }
                }

                //update screen
                StatusHelper.updateScreen(players);
            }
        }
    }

    @Override
    public void dispose() {

        if (this.diceRollAttack != null) {
            for (Sprite sprite : this.diceRollAttack) {
                sprite.dispose();
            }
        }

        if (this.diceRollDefend != null) {
            for (Sprite sprite : this.diceRollDefend) {
                sprite.dispose();
            }
        }

        this.playerStatusAttack = null;
        this.playerStatusDefend = null;
        this.playerCountAttackLabel = null;
        this.playerCountDefendLabel = null;
        this.playerDiceAttack = null;
        this.playerDiceDefend = null;
        this.diceRollAttack = null;
        this.diceRollDefend = null;
        this.statusImage = null;
        this.statusImageHighlighted = null;
        this.diceImage = null;
        this.countLabel = null;
        this.diceRemainingImage = null;
        this.playerCountRenderX = null;
        this.playerCountRenderY = null;
        this.playerStatusRenderX = null;
        this.playerStatusRenderY = null;
        this.playerDiceRenderY = null;
        this.playerDiceRenderX = null;
        this.buttonSpeed = null;
        this.exitButton = null;
        this.playerButtonRest = null;
    }
}