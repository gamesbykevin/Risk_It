package com.gamesbykevin.riskit.screen;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.gamesbykevin.riskit.MyGdxGame;
import com.gamesbykevin.riskit.sprites.Sprite;
import com.gamesbykevin.riskit.util.IMyGdxGame;
import com.gamesbykevin.riskit.util.MyGdxGameHelper;

public class Intro extends ParentScreen implements IMyGdxGame {

    public static final float FRAME_DURATION = (1f / (float)MyGdxGameHelper.FPS);

    public static final float DELAY = .5f;

    public static final int ANIMATION_WIDTH = 800;
    public static final int ANIMATION_HEIGHT = 419;

    private float duration;

    public Intro(MyGdxGame game) {
        super(game);
        setDuration(0);
        setupUI();
    }

    public float getDuration() {
        return this.duration;
    }

    public void setDuration(float duration) {
        this.duration = duration;
    }

    private Sprite getSprite() {
        return getGame().getSprites().getSprite(Sprite.Regions.ANIMATION_1);
    }

    private void resetSprite(Sprite sprite) {
        sprite.setPosition(0,0);
        sprite.setSize(MyGdxGameHelper.WIDTH, MyGdxGameHelper.HEIGHT);
        sprite.getAnimation().setPlayMode(Animation.PlayMode.NORMAL);
        sprite.setStateTime(0);
        sprite.setPaused(false);
    }

    @Override
    public void setupUI() {

        getStage().clear();

        setDuration(0);
        resetSprite(getSprite());

        getStage().addActor(getBackground());
        getStage().addActor(getSprite());

        getGame().getAssetManager().stopAllSound();
        getGame().getAssetManager().playIntro();
    }

    @Override
    public void show() {
        super.show();
        setupUI();
    }

    @Override
    public void pause() {
        super.pause();
        getSprite().setPaused(true);
    }

    @Override
    public void resume() {
        super.resume();
        setDuration(0);
        getSprite().setPaused(false);

        while (!getGame().getAssetManager().update()){}
    }

    @Override
    public void render(float delta) {

        super.render(delta);

        if (getSprite().isPaused()) {

            if (getDuration() >= DELAY) {
                getGame().getAssetManager().stopAllSound();
                getGame().selectScreen(Screens.MainMenu);
            }

            setDuration(getDuration() + delta);

        } else {

            if (!getSprite().isPaused()) {
                if (getSprite().getAnimation().isAnimationFinished(getSprite().getStateTime() + delta)) {
                    getSprite().setPaused(true);

                    while (getSprite().getAnimation().isAnimationFinished(getSprite().getStateTime())) {
                        getSprite().setStateTime(getSprite().getStateTime() - delta);
                    }
                }
            }

            setDuration(0);
        }
    }

    @Override
    public void dispose() {
        super.dispose();
    }
}