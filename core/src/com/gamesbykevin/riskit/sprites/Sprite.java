package com.gamesbykevin.riskit.sprites;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureAtlas.AtlasRegion;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.gamesbykevin.riskit.screen.Intro;
import com.gamesbykevin.riskit.territory.Territory;
import com.gamesbykevin.riskit.territory.Tile;
import com.gamesbykevin.riskit.util.IMyGdxGame;
import com.gamesbykevin.riskit.util.MyGdxGameHelper;

public class Sprite extends Image implements IMyGdxGame {

    private Animation<TextureRegion> animation;
    private final Regions region;
    private float stateTime = 0;

    private boolean paused;

    public static final int DICE_ANIMATION_WIDTH = 32;
    public static final int DICE_ANIMATION_HEIGHT = 32;

    public static final float FRAME_DURATION = (1 / (float) MyGdxGameHelper.FPS);

    public enum Regions {
        DICE_BLUE("dice_blue", Tile.WIDTH, Tile.HEIGHT),
        DICE_DARK_GREEN("dice_dark_green", Tile.WIDTH, Tile.HEIGHT),
        DICE_DARK_PURPLE("dice_dark_purple", Tile.WIDTH, Tile.HEIGHT),
        DICE_LIGHT_GREEN("dice_light_green", Tile.WIDTH, Tile.HEIGHT),
        DICE_LIGHT_PURPLE("dice_light_purple", Tile.WIDTH, Tile.HEIGHT),
        DICE_ORANGE("dice_orange", Tile.WIDTH, Tile.HEIGHT),
        DICE_RED("dice_red", Tile.WIDTH, Tile.HEIGHT),
        DICE_YELLOW("dice_yellow", Tile.WIDTH, Tile.HEIGHT),
        DICE_WHITE("dice_white", Tile.WIDTH, Tile.HEIGHT),
        TERRITORY_BLUE("territory_blue", Territory.WIDTH, Territory.HEIGHT),
        TERRITORY_DARK_GREEN("territory_dark_green", Territory.WIDTH, Territory.HEIGHT),
        TERRITORY_DARK_PURPLE("territory_purple", Territory.WIDTH, Territory.HEIGHT),
        TERRITORY_LIGHT_GREEN("territory_light_green", Territory.WIDTH, Territory.HEIGHT),
        TERRITORY_LIGHT_PURPLE("territory_light_purple", Territory.WIDTH, Territory.HEIGHT),
        TERRITORY_ORANGE("territory_orange", Territory.WIDTH, Territory.HEIGHT),
        TERRITORY_RED("territory_red", Territory.WIDTH, Territory.HEIGHT),
        TERRITORY_YELLOW("territory_yellow", Territory.WIDTH, Territory.HEIGHT),
        TERRITORY_DARK("territory_dark", Territory.WIDTH, Territory.HEIGHT),
        TERRITORY_OUTLINE_W("outline_w", Territory.WIDTH, Territory.HEIGHT),
        TERRITORY_OUTLINE_E("outline_e", Territory.WIDTH, Territory.HEIGHT),
        TERRITORY_OUTLINE_SW("outline_sw", Territory.WIDTH, Territory.HEIGHT),
        TERRITORY_OUTLINE_SE("outline_se", Territory.WIDTH, Territory.HEIGHT),
        TERRITORY_OUTLINE_NE("outline_ne", Territory.WIDTH, Territory.HEIGHT),
        TERRITORY_OUTLINE_NW("outline_nw", Territory.WIDTH, Territory.HEIGHT),
        TERRITORY_HIGHLIGHT_OUTLINE_W("highlight_outline_w", Territory.WIDTH, Territory.HEIGHT),
        TERRITORY_HIGHLIGHT_OUTLINE_E("highlight_outline_e", Territory.WIDTH, Territory.HEIGHT),
        TERRITORY_HIGHLIGHT_OUTLINE_SW("highlight_outline_sw", Territory.WIDTH, Territory.HEIGHT),
        TERRITORY_HIGHLIGHT_OUTLINE_SE("highlight_outline_se", Territory.WIDTH, Territory.HEIGHT),
        TERRITORY_HIGHLIGHT_OUTLINE_NE("highlight_outline_ne", Territory.WIDTH, Territory.HEIGHT),
        TERRITORY_HIGHLIGHT_OUTLINE_NW("highlight_outline_nw", Territory.WIDTH, Territory.HEIGHT),
        ROLLING_DICE(new String[]{
            "dice0", "dice1", "dice2", "dice3", "dice4", "dice5", "dice6", "dice7",
            "dice8", "dice9", "dice10", "dice11", "dice12", "dice13", "dice14", "dice15",
            "dice16", "dice17", "dice18", "dice19", "dice20", "dice21", "dice22", "dice23",
            "dice24", "dice25", "dice26", "dice27", "dice28", "dice29", "dice30", "dice31"
        }, DICE_ANIMATION_WIDTH, DICE_ANIMATION_HEIGHT),
        DICE_1("dice15", DICE_ANIMATION_WIDTH, DICE_ANIMATION_HEIGHT),
        DICE_2("dice3", DICE_ANIMATION_WIDTH, DICE_ANIMATION_HEIGHT),
        DICE_3("dice27", DICE_ANIMATION_WIDTH, DICE_ANIMATION_HEIGHT),
        DICE_4("dice19", DICE_ANIMATION_WIDTH, DICE_ANIMATION_HEIGHT),
        DICE_5("dice11", DICE_ANIMATION_WIDTH, DICE_ANIMATION_HEIGHT),
        DICE_6("dice7", DICE_ANIMATION_WIDTH, DICE_ANIMATION_HEIGHT),
        ANIMATION_1(Intro.ANIMATION_WIDTH, Intro.ANIMATION_HEIGHT);

        private final String[] names;
        private final int width, height;

        Regions(int width, int height) {
            this(new String[] {}, width, height);
        }

        Regions (String name, int width, int height) {
            this(new String[] {name}, width, height);
        }

        Regions (String[] names, int width, int height) {
            this.names = names;
            this.width = width;
            this.height = height;
        }

        public int getHeight() {
            return this.height;
        }

        public int getWidth() {
            return this.width;
        }

        public String[] getNames() {
            return this.names;
        }
    }

    public Sprite(Regions region, TextureAtlas atlas) {
        this(region, atlas, FRAME_DURATION);
    }

    public Sprite(Regions region, TextureAtlas atlas, float duration) {
        this.region = region;

        if (getRegion().getNames().length > 0) {

            AtlasRegion[] frames = new AtlasRegion[getRegion().getNames().length];

            for (int i = 0; i < frames.length; i++) {
                frames[i] = atlas.findRegion(getRegion().getNames()[i]);
            }

            this.animation = new Animation<>(duration, frames);

            if (frames.length <= 1) {
                getAnimation().setPlayMode(Animation.PlayMode.NORMAL);
            } else {
                getAnimation().setPlayMode(Animation.PlayMode.LOOP);
            }
        } else {
            this.animation = new Animation<>(duration, atlas.getRegions());
            getAnimation().setPlayMode(Animation.PlayMode.NORMAL);
        }

        super.setWidth(getRegion().getWidth());
        super.setHeight(getRegion().getHeight());
        setPaused(true);
    }

    public void setPaused(boolean paused) {
        this.paused = paused;
    }

    public boolean isPaused() {
        return this.paused;
    }

    public void setStateTime(float stateTime) {
        this.stateTime = stateTime;
    }

    public float getStateTime() {
        return this.stateTime;
    }

    public Animation<TextureRegion> getAnimation() {
        return this.animation;
    }

    public Regions getRegion() {
        return this.region;
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {

        if (!isPaused())
            setStateTime(getStateTime() + Gdx.graphics.getDeltaTime());

        batch.draw(getAnimation().getKeyFrame(getStateTime(), true), getX(), getY(), getWidth(), getHeight());
    }

    @Override
    public void dispose() {
        this.animation = null;
    }
}