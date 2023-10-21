package com.gamesbykevin.riskit.util;

import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.gamesbykevin.riskit.assets.Manager;
import com.gamesbykevin.riskit.gameservices.Achievements;
import com.gamesbykevin.riskit.localization.Language;
import com.gamesbykevin.riskit.player.Player;
import com.gamesbykevin.riskit.player.Players;
import com.gamesbykevin.riskit.player.status.Status;
import com.gamesbykevin.riskit.preferences.MyPreferences;
import com.gamesbykevin.riskit.screen.GameScreen;
import com.gamesbykevin.riskit.screen.ParentScreen;
import com.gamesbykevin.riskit.screen.dialog.Dialogs;
import com.gamesbykevin.riskit.sprites.Sprite;
import com.gamesbykevin.riskit.territory.Territories;
import com.gamesbykevin.riskit.territory.Territory;

import java.util.Random;

public class StatusHelper {

    public static final int STATUS_PLAYER_WIDTH = 126;
    public static final int STATUS_PLAYER_HEIGHT = 48;
    public static final int PADDING = 5;

    public static final int STATUS_BAR_WIDTH = 560;
    public static final int STATUS_BAR_HEIGHT = 102;
    public static final int STATUS_BAR_Y = 10;
    public static final int STATUS_BAR_X = (MyGdxGameHelper.WIDTH / 2) - (STATUS_BAR_WIDTH / 2);

    public static final int BUTTON_BACK_W = 100;
    public static final int BUTTON_BACK_H = 48;
    public static final int BUTTON_BACK_X = (PADDING * 2);
    public static final int BUTTON_BACK_Y = (PADDING * 2);

    public static final int PLAYER_REST_W = 45;
    public static final int PLAYER_REST_H = 48;
    public static final int PLAYER_REST_X = MyGdxGameHelper.WIDTH - PLAYER_REST_W - (PADDING * 2);
    public static final int PLAYER_REST_Y = (PADDING * 2);

    public static final int PLAYER_SPEED_W = 45;
    public static final int PLAYER_SPEED_H = 48;
    public static final int PLAYER_SPEED_X = PLAYER_REST_X - PLAYER_SPEED_W - (PADDING * 2);
    public static final int PLAYER_SPEED_Y = (PADDING * 2);
    public static final int PLAYER_AUDIO_Y = PLAYER_SPEED_Y + (PLAYER_SPEED_H + PADDING);

    public static final int PLAYER_CHALLENGE_ATTACK_X = STATUS_BAR_X + (PADDING * 7);

    public static final int SPAWN_DICE_PER_ROW = 20;
    public static final int SPAWN_DICE_RENDER_MAX = 40;

    public static void createStatusUI(GameScreen screen) {
        createStatusBackground(screen);
        createStatusPlayersArea(screen);
        createChallengeArea(screen);
        createRemainingArea(screen);
        createSpeedButton(screen);
        createAudioButton(screen);
        createRestButton(screen);
        createBackButton(screen);
        createVibrateButton(screen);
        updateScreen(screen.getPlayers());
    }

    public static void updateScreen(Players players) {

        Status status = players.getStatus();

        switch (status.getState()) {

            case Challenge:
            case Result:
                status.getPlayerCountAttackLabel().setText(0);
                status.getPlayerCountDefendLabel().setText(0);
                setChallengeAreaVisible(players, true);
                setRemainingAreaVisible(players, false);
                setStatusPlayersAreaVisible(players, false);
                break;
            case Regenerate:
                setRemainingAreaVisible(players, true);
                setChallengeAreaVisible(players, false);
                setStatusPlayersAreaVisible(players, false);
                break;

            case Thinking:
                setStatusPlayersAreaVisible(players, true);
                setChallengeAreaVisible(players, false);
                setRemainingAreaVisible(players, false);
                break;
        }
    }

    public static final void createVibrateButton(GameScreen screen) {

        if (!MyGdxGameHelper.isAndroid())
            return;

        Manager manager = screen.getGame().getAssetManager();
        Skin skin = manager.getSkin();

        Button buttonVibrate = new Button(skin, MyGdxGameHelper.STYLE_BUTTON_VIBRATE);
        buttonVibrate.setSize(PLAYER_SPEED_W, PLAYER_SPEED_H);
        buttonVibrate.setPosition(PLAYER_REST_X, PLAYER_AUDIO_Y);
        buttonVibrate.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                boolean enabled = MyPreferences.isBoolean(MyPreferences.PREFS_VIBRATE);
                MyPreferences.saveBoolean(MyPreferences.PREFS_VIBRATE, !enabled);
            }
        });
        buttonVibrate.setChecked(MyPreferences.isBoolean(MyPreferences.PREFS_VIBRATE));
        screen.getStage().addActor(buttonVibrate);
    }

    public static final void createBackButton(GameScreen screen) {

        if (MyGdxGameHelper.isAndroid())
            return;

        Manager manager = screen.getGame().getAssetManager();
        Skin skin = manager.getSkin();

        TextButton exitButton = new TextButton(manager.getTranslatedText(Language.KEY_BUTTON_BACK), skin, MyGdxGameHelper.STYLE_BUTTON_EMPTY_100);
        exitButton.setPosition(BUTTON_BACK_X, BUTTON_BACK_Y);
        exitButton.setSize(BUTTON_BACK_W, BUTTON_BACK_H);

        exitButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {

                screen.getGame().getAssetManager().playSound(Manager.SOUND_DIR_BUTTON_MENU);

                if (screen.getDialogs().hasDialog())
                    return;

                screen.getDialogs().setType(Dialogs.Type.Save);
            }
        });

        Status status = screen.getPlayers().getStatus();

        status.setExitButton(exitButton);
        screen.getStage().addActor(status.getExitButton());
    }

    public static final void createSpeedButton(GameScreen screen) {
        Button buttonSpeed = new Button(screen.getGame().getScreen().getSkin(), MyGdxGameHelper.STYLE_BUTTON_SPEED);
        buttonSpeed.setSize(PLAYER_SPEED_W, PLAYER_SPEED_H);
        buttonSpeed.setPosition(PLAYER_SPEED_X, PLAYER_SPEED_Y);
        buttonSpeed.setChecked(false);
        buttonSpeed.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                screen.getGame().getAssetManager().playSound(Manager.SOUND_DIR_BUTTON_MENU);
            }
        });
        Status status = screen.getPlayers().getStatus();
        status.setSpeed(false);
        status.setButtonSpeed(buttonSpeed);
        screen.getStage().addActor(status.getButtonSpeed());
    }

    public static final void createAudioButton(GameScreen screen) {
        Button button = MyGdxGameHelper.createAudioButton(screen.getSkin(), screen.getGame().getAssetManager(), PLAYER_SPEED_X, PLAYER_AUDIO_Y, PLAYER_SPEED_W, PLAYER_SPEED_H);
        screen.getStage().addActor(button);
    }

    public static final void createRestButton(GameScreen screen) {
        Button playerButtonRest = new Button(screen.getGame().getAssetManager().getSkin(), MyGdxGameHelper.STYLE_BUTTON_COMPLETE_TURN);
        playerButtonRest.setSize(PLAYER_REST_W, PLAYER_REST_H);
        playerButtonRest.setPosition(PLAYER_REST_X, PLAYER_REST_Y);
        playerButtonRest.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {

                //don't do anything if the game is paused
                if (screen.getDialogs().hasDialog())
                    return;

                Players players = screen.getPlayers();

                if (!players.getStatus().hasState(Status.State.Thinking))
                    return;

                screen.getGame().getAssetManager().playSound(Manager.SOUND_DIR_BUTTON_MENU);

                players.countConsecutive(screen.getBoard());

                if (players.getPlayerCurrent().getTerritoryAttack() != null)
                    players.getPlayerCurrent().getTerritoryAttack().setSelected(false);
                if (players.getPlayerCurrent().getTerritoryDefend() != null)
                    players.getPlayerCurrent().getTerritoryDefend().setSelected(false);

                players.getPlayerCurrent().setTerritoryAttack(null);
                players.getPlayerCurrent().setTerritoryDefend(null);

                Status status = players.getStatus();
                status.setState(Status.State.Regenerate);
                status.setDelta(0);
                status.getPlayerButtonRest().setVisible(false);
                StatusHelper.updateScreen(players);
            }
        });

        screen.getPlayers().getStatus().setPlayerButtonRest(playerButtonRest);
        screen.getStage().addActor(screen.getPlayers().getStatus().getPlayerButtonRest());
    }

    public static void completeChallenge(GameScreen screen) {

        Players players = screen.getPlayers();
        Status status = players.getStatus();
        Random random = MyGdxGameHelper.getRandom();
        Territory territoryAttack = players.getPlayerCurrent().getTerritoryAttack();
        Territory territoryDefend = players.getPlayerCurrent().getTerritoryDefend();

        int playerTotalAttack = 0;
        int playerTotalDefend = 0;

        for (int index = 0; index < territoryAttack.getDice(); index++) {
            int diceNumber = random.nextInt(6) + 1;
            playerTotalAttack += diceNumber;
            status.getDiceRollAttack()[index].setPaused(true);
            status.getDiceRollAttack()[index].setStateTime(getDiceFrameDuration(diceNumber));
        }

        for (int index = 0; index < territoryDefend.getDice(); index++) {
            int diceNumber = random.nextInt(6) + 1;
            playerTotalDefend += diceNumber;
            status.getDiceRollDefend()[index].setPaused(true);
            status.getDiceRollDefend()[index].setStateTime(getDiceFrameDuration(diceNumber));
        }

        status.getPlayerCountAttackLabel().setText(playerTotalAttack);
        status.getPlayerCountDefendLabel().setText(playerTotalDefend);

        if (playerTotalAttack > playerTotalDefend) {

            //only vibrate for the human
            if (players.getPlayerCurrent().isHuman()) {
                MyGdxGameHelper.vibrate(false);

                if (territoryAttack.getDice() < territoryDefend.getDice())
                    Achievements.unlock(screen.getGame().getGameServiceClient(), Achievements.TERRITORY_UNDERDOG);
            }

            territoryDefend.setDice(territoryAttack.getDice() - 1);
            territoryAttack.setDice(1);
            territoryDefend.setPlayerId(territoryAttack.getPlayerId());

            if (players.getPlayerCurrent().isHuman()) {
                Territories territories = screen.getBoard().getTerritories();
                int countAll = territories.getTerritoryCount(MyGdxGameHelper.UNASSIGNED, false);
                int countPlayer = territories.getTerritoryCount(players.getPlayerCurrent().getId(), false);

                //if we control all but 1
                if (countPlayer >= countAll - 1)
                    Achievements.unlock(screen.getGame().getGameServiceClient(), Achievements.CONTROL_ALL);
            }

        } else {

            if (players.getPlayerCurrent().isHuman()) {
                if (territoryAttack.getDice() - territoryDefend.getDice() >= 2) {
                    Achievements.unlock(screen.getGame().getGameServiceClient(), Achievements.GOLIATH_LOST);
                }
            }


            territoryAttack.setDice(1);
        }

        territoryAttack.setSelected(false);
        territoryDefend.setSelected(false);

        players.getPlayerCurrent().setTerritoryAttack(null);
        players.getPlayerCurrent().setTerritoryDefend(null);

        players.countConsecutive(screen.getBoard());
    }

    private static void setRemainingAreaVisible(Players players, boolean visible) {

        Status status = players.getStatus();

        for (int index = 0; index < status.getDiceRemainingImage().length; index++) {

            if (visible) {
                status.getDiceRemainingImage()[index].setVisible(index < players.getPlayerCurrent().getRemaining());
            } else {
                status.getDiceRemainingImage()[index].setVisible(visible);
            }
        }
    }

    private static void createRemainingArea(GameScreen screen) {
        Status status = screen.getPlayers().getStatus();

        float x, y;
        float scale = 1.1f;
        float w = Sprite.Regions.DICE_WHITE.getWidth() * scale;
        float h = Sprite.Regions.DICE_WHITE.getHeight() * scale;
        float startX = STATUS_BAR_X + (PADDING * 2);
        float startY = STATUS_BAR_Y + STATUS_BAR_HEIGHT - (PADDING * 4) - h;

        for (int index = 0; index < status.getDiceRemainingImage().length; index++) {
            status.getDiceRemainingImage()[index] = new Image(screen.getGame().getAssetManager().getSkin(), MyGdxGameHelper.DRAWABLE_DICE_WHITE);

            if (index < SPAWN_DICE_PER_ROW) {
                x = startX + (w * index) + (PADDING * index);
                y = startY;
            } else {
                x = startX + (w * (index - 20)) + (PADDING * (index - 20));
                y = startY - h - PADDING;
            }
            status.getDiceRemainingImage()[index].setPosition(x, y);
            status.getDiceRemainingImage()[index].setSize(w, h);
            status.getDiceRemainingImage()[index].setVisible(false);
            screen.getStage().addActor(status.getDiceRemainingImage()[index]);
        }
    }

    private static void setChallengeAreaVisible(Players players, boolean visible) {

        Status status = players.getStatus();

        status.getPlayerStatusAttack().setVisible(visible);
        status.getPlayerStatusDefend().setVisible(visible);

        Player playerAttack = players.getPlayerCurrent();
        Territory territoryAttack = playerAttack.getTerritoryAttack();
        Territory territoryDefend = playerAttack.getTerritoryDefend();

        for (int index = 0; index < players.getPlayerCount(); index++) {

            Player player = players.getPlayer(index);

            if (visible) {
                status.getPlayerDiceAttack()[index].setVisible(player.getId() == territoryAttack.getPlayerId());
                status.getPlayerDiceDefend()[index].setVisible(player.getId() == territoryDefend.getPlayerId());
            } else {
                status.getPlayerDiceAttack()[index].setVisible(visible);
                status.getPlayerDiceDefend()[index].setVisible(visible);
            }
        }

        for (int index = 0; index < Territory.DICE_MAX; index++) {

            if (visible) {
                status.getDiceRollAttack()[index].setVisible(index < territoryAttack.getDice());
                status.getDiceRollDefend()[index].setVisible(index < territoryDefend.getDice());
            } else {
                status.getDiceRollAttack()[index].setVisible(visible);
                status.getDiceRollDefend()[index].setVisible(visible);
            }

            status.getDiceRollAttack()[index].setStateTime(MyGdxGameHelper.getRandom().nextFloat() * Sprite.Regions.ROLLING_DICE.getNames().length);
            status.getDiceRollAttack()[index].setPaused(false);
            status.getDiceRollDefend()[index].setStateTime(MyGdxGameHelper.getRandom().nextFloat() * Sprite.Regions.ROLLING_DICE.getNames().length);
            status.getDiceRollDefend()[index].setPaused(false);
        }

        status.getPlayerCountAttackLabel().setVisible(visible);
        status.getPlayerCountDefendLabel().setVisible(visible);
    }

    private static void createChallengeArea(GameScreen screen) {

        Status status = screen.getPlayers().getStatus();

        Image playerStatusAttack = new Image(screen.getSkin().getDrawable(MyGdxGameHelper.DRAWABLE_PLAYER_STATUS));
        playerStatusAttack.setSize(STATUS_PLAYER_WIDTH, STATUS_PLAYER_HEIGHT);
        playerStatusAttack.setPosition((PADDING * 2) + STATUS_BAR_X, STATUS_BAR_Y + STATUS_BAR_HEIGHT - STATUS_PLAYER_HEIGHT - PADDING);
        playerStatusAttack.setVisible(false);
        status.setPlayerStatusAttack(playerStatusAttack);
        screen.getStage().addActor(playerStatusAttack);

        Image playerStatusDefend = new Image(screen.getSkin().getDrawable(MyGdxGameHelper.DRAWABLE_PLAYER_STATUS));
        playerStatusDefend.setSize(STATUS_PLAYER_WIDTH, STATUS_PLAYER_HEIGHT);
        playerStatusDefend.setPosition((PADDING * 2) + STATUS_BAR_X, STATUS_BAR_Y + PADDING);
        playerStatusDefend.setVisible(false);
        status.setPlayerStatusDefend(playerStatusDefend);
        screen.getStage().addActor(playerStatusDefend);

        float w = screen.getGame().getSprites().getSprite(Sprite.Regions.DICE_WHITE).getWidth();
        float h = screen.getGame().getSprites().getSprite(Sprite.Regions.DICE_WHITE).getHeight();
        float x = playerStatusAttack.getX() + STATUS_PLAYER_WIDTH - w - (PADDING * 4);

        //this is the players dice in the status, not the rolling dice
        for (int index = 0; index < screen.getPlayers().getPlayerCount(); index++) {
            status.getPlayerDiceAttack()[index] = createPlayerDice(screen.getGame().getAssetManager().getSkin(), index);
            status.getPlayerDiceAttack()[index].setSize(w, h);
            status.getPlayerDiceAttack()[index].setPosition(x,playerStatusAttack.getY() + (PADDING * 3));
            status.getPlayerDiceAttack()[index].setVisible(false);
            screen.getStage().addActor(status.getPlayerDiceAttack()[index]);

            status.getPlayerDiceDefend()[index] = createPlayerDice(screen.getGame().getAssetManager().getSkin(), index);
            status.getPlayerDiceDefend()[index].setSize(w, h);
            status.getPlayerDiceDefend()[index].setPosition(x,playerStatusDefend.getY() + (PADDING * 3));
            status.getPlayerDiceDefend()[index].setVisible(false);
            screen.getStage().addActor(status.getPlayerDiceDefend()[index]);
        }

        w = Sprite.Regions.ROLLING_DICE.getWidth();
        h = Sprite.Regions.ROLLING_DICE.getHeight();

        Random random = MyGdxGameHelper.getRandom();

        //let's create the animated dice that we roll
        for (int index = 0; index < Territory.DICE_MAX; index++) {
            x = PLAYER_CHALLENGE_ATTACK_X + STATUS_PLAYER_WIDTH + (w * index) + (index * PADDING);

            status.getDiceRollAttack()[index] = new Sprite(Sprite.Regions.ROLLING_DICE, screen.getGame().getAssetManager().getAtlas(Manager.ATLAS_DIR_SPRITESHEET));
            status.getDiceRollAttack()[index].setStateTime(random.nextFloat() * Sprite.Regions.ROLLING_DICE.getNames().length);
            status.getDiceRollAttack()[index].setVisible(false);
            status.getDiceRollAttack()[index].setSize(w, h);
            status.getDiceRollAttack()[index].setPosition(x, STATUS_BAR_Y + STATUS_BAR_HEIGHT - h - (PADDING * 2));
            screen.getStage().addActor(status.getDiceRollAttack()[index]);

            status.getDiceRollDefend()[index] = new Sprite(Sprite.Regions.ROLLING_DICE, screen.getGame().getAssetManager().getAtlas(Manager.ATLAS_DIR_SPRITESHEET));
            status.getDiceRollDefend()[index].setStateTime(random.nextFloat() * Sprite.Regions.ROLLING_DICE.getNames().length);
            status.getDiceRollDefend()[index].setVisible(false);
            status.getDiceRollDefend()[index].setSize(w, h);
            status.getDiceRollDefend()[index].setPosition(x, STATUS_BAR_Y + (PADDING * 3));
            screen.getStage().addActor(status.getDiceRollDefend()[index]);
        }

        Label playerCountAttackLabel = new Label("0", screen.getSkin(), MyGdxGameHelper.FONT_24, MyGdxGameHelper.STYLE_COLOR);
        playerCountAttackLabel.setPosition(playerStatusAttack.getX() + (STATUS_PLAYER_WIDTH / 2) - (PADDING * 3), playerStatusAttack.getY() + (PADDING * 3));
        playerCountAttackLabel.setVisible(false);
        status.setPlayerCountAttackLabel(playerCountAttackLabel);
        screen.getStage().addActor(status.getPlayerCountAttackLabel());

        Label playerCountDefendLabel = new Label("0", screen.getSkin(), MyGdxGameHelper.FONT_24, MyGdxGameHelper.STYLE_COLOR);
        playerCountDefendLabel.setPosition(playerStatusAttack.getX() + (STATUS_PLAYER_WIDTH / 2) - (PADDING * 3), playerStatusDefend.getY() + (PADDING * 3));
        playerCountDefendLabel.setVisible(false);
        status.setPlayerCountDefendLabel(playerCountDefendLabel);
        screen.getStage().addActor(status.getPlayerCountDefendLabel());
    }

    private static void setStatusPlayersAreaVisible(Players players, boolean visible) {

        Status status = players.getStatus();

        for (int index = 0; index < players.getPlayerCount(); index++) {

            if (visible) {
                status.getStatusImage()[index].setVisible(index != players.getCurrentId());
                status.getStatusImageHighlighted()[index].setVisible(index == players.getCurrentId());
            } else {
                status.getStatusImage()[index].setVisible(visible);
                status.getStatusImageHighlighted()[index].setVisible(visible);
            }

            Player player = players.getPlayer(index);

            status.getDiceImage()[index].setVisible(visible);
            status.getCountLabel()[index].setVisible(visible);
            status.getCountLabel()[index].setText(player.getConsecutiveCount());

            //if dead hide UI elements
            if (player.isDead()) {
                status.getStatusImage()[index].setVisible(false);
                status.getStatusImageHighlighted()[index].setVisible(false);
                status.getDiceImage()[index].setVisible(false);
                status.getCountLabel()[index].setVisible(false);
            }
        }
    }

    private static void createStatusPlayersArea(GameScreen screen) {

        float w = screen.getGame().getSprites().getSprite(Sprite.Regions.DICE_WHITE).getWidth();
        float h = screen.getGame().getSprites().getSprite(Sprite.Regions.DICE_WHITE).getHeight();

        //setup status for each player
        for (int index = 0; index < screen.getPlayers().getPlayerCount(); index++) {

            Status status = screen.getPlayers().getStatus();

            float x, y;
            int offset = (index <= 3) ? index : index - 4;
            x = (PADDING * 2) + STATUS_BAR_X + (offset * STATUS_PLAYER_WIDTH) + (offset * (PADDING*2));
            y = (index > 3) ? STATUS_BAR_Y + PADDING : STATUS_BAR_Y + STATUS_BAR_HEIGHT - STATUS_PLAYER_HEIGHT - PADDING;
            status.getPlayerStatusRenderX()[index] = x;
            status.getPlayerStatusRenderY()[index] = y;

            x = status.getPlayerStatusRenderX()[index] + STATUS_PLAYER_WIDTH - w - (PADDING * 4);
            y = status.getPlayerStatusRenderY()[index] + (PADDING * 3);

            status.getPlayerDiceRenderX()[index] = x;
            status.getPlayerDiceRenderY()[index] = y;

            status.getStatusImage()[index] = new Image(screen.getSkin().getDrawable(MyGdxGameHelper.DRAWABLE_PLAYER_STATUS));
            status.getStatusImage()[index].setSize(STATUS_PLAYER_WIDTH, STATUS_PLAYER_HEIGHT);
            status.getStatusImage()[index].setPosition(status.getPlayerStatusRenderX()[index], status.getPlayerStatusRenderY()[index]);

            status.getStatusImageHighlighted()[index] = new Image(screen.getSkin().getDrawable(MyGdxGameHelper.DRAWABLE_PLAYER_STATUS_HIGHLIGHTED));
            status.getStatusImageHighlighted()[index].setSize(STATUS_PLAYER_WIDTH, STATUS_PLAYER_HEIGHT);
            status.getStatusImageHighlighted()[index].setPosition(status.getPlayerStatusRenderX()[index], status.getPlayerStatusRenderY()[index]);

            status.getCountLabel()[index] = new Label("0", screen.getSkin(), MyGdxGameHelper.FONT_24, MyGdxGameHelper.STYLE_COLOR);
            x = status.getPlayerStatusRenderX()[index] + (STATUS_PLAYER_WIDTH / 2) - (PADDING * 3);
            y = status.getPlayerStatusRenderY()[index] + (PADDING * 3);
            status.getPlayerCountRenderX()[index] = x;
            status.getPlayerCountRenderY()[index] = y;
            status.getCountLabel()[index].setPosition(status.getPlayerCountRenderX()[index], status.getPlayerCountRenderY()[index]);

            status.getDiceImage()[index] = createPlayerDice(screen.getGame().getAssetManager().getSkin(), index);
            status.getDiceImage()[index].setSize(w, h);
            status.getDiceImage()[index].setPosition(status.getPlayerDiceRenderX()[index], status.getPlayerDiceRenderY()[index]);

            screen.getStage().addActor(status.getStatusImage()[index]);
            screen.getStage().addActor(status.getStatusImageHighlighted()[index]);
            screen.getStage().addActor(status.getDiceImage()[index]);
            screen.getStage().addActor(status.getCountLabel()[index]);
        }
    }

    private static void createStatusBackground(GameScreen screen) {
        Image statusBar = new Image(screen.getSkin().getDrawable(MyGdxGameHelper.DRAWABLE_STATUS_BAR));
        statusBar.setWidth(STATUS_BAR_WIDTH);
        statusBar.setHeight(STATUS_BAR_HEIGHT);
        statusBar.setY(STATUS_BAR_Y);
        MyGdxGameHelper.centerActorX(statusBar);
        screen.getStage().addActor(statusBar);
    }

    public static float getDiceFrameDuration(int diceValue) {

        switch (diceValue) {
            default:
            case 1:
                return Sprite.FRAME_DURATION * 31;

            case 2:
                return Sprite.FRAME_DURATION * 3;

            case 3:
                return Sprite.FRAME_DURATION * 27;

            case 4:
                return Sprite.FRAME_DURATION * 19;

            case 5:
                return Sprite.FRAME_DURATION * 11;

            case 6:
                return Sprite.FRAME_DURATION * 7;
        }
    }

    public static Image createPlayerDice(Skin skin, int index) {

        String name;

        switch (index) {
            default:
            case 0:
                name = MyGdxGameHelper.DRAWABLE_DICE_BLUE;
                break;

            case 1:
                name = MyGdxGameHelper.DRAWABLE_DICE_DARK_GREEN;
                break;

            case 2:
                name = MyGdxGameHelper.DRAWABLE_DICE_DARK_PURPLE;
                break;

            case 3:
                name = MyGdxGameHelper.DRAWABLE_DICE_LIGHT_GREEN;
                break;

            case 4:
                name = MyGdxGameHelper.DRAWABLE_DICE_LIGHT_PURPLE;
                break;

            case 5:
                name = MyGdxGameHelper.DRAWABLE_DICE_ORANGE;
                break;

            case 6:
                name = MyGdxGameHelper.DRAWABLE_DICE_RED;
                break;

            case 7:
                name = MyGdxGameHelper.DRAWABLE_DICE_YELLOW;
                break;
        }

        return new Image(skin, name);
    }
}