package com.gamesbykevin.riskit.sprites;

import com.gamesbykevin.riskit.assets.Manager;
import com.gamesbykevin.riskit.player.Player;
import com.gamesbykevin.riskit.screen.Intro;
import com.gamesbykevin.riskit.util.IMyGdxGame;

import java.util.HashMap;

public class Sprites implements IMyGdxGame {

    private HashMap<Sprite.Regions, Sprite> spriteList;

    public Sprites(Manager manager) {

        this.spriteList = new HashMap<>();

        for (Sprite.Regions region : Sprite.Regions.values()) {

            switch (region) {
                case ANIMATION_1:
                    getSpriteList().put(region, new Sprite(region, manager.getAtlas(Manager.ATLAS_DIR_ANIMATION_1), Intro.FRAME_DURATION));
                    break;

                default:
                    getSpriteList().put(region, new Sprite(region, manager.getAtlas(Manager.ATLAS_DIR_SPRITESHEET)));
                    break;
            }
        }
    }

    public HashMap<Sprite.Regions, Sprite> getSpriteList() {
        return this.spriteList;
    }

    public Sprite getSprite(Sprite.Regions region) {
        return getSpriteList().get(region);
    }

    public Sprite getSprite(Player player, boolean territory) {
        if (player == null)
            return null;

        switch (player.getColorIndex()) {
            case Player.COLOR_LIGHT_PURPLE:
                if (territory) {
                    return getSprite(Sprite.Regions.TERRITORY_LIGHT_PURPLE);
                } else {
                    return getSprite(Sprite.Regions.DICE_LIGHT_PURPLE);
                }

            case Player.COLOR_LIGHT_GREEN:
                if (territory) {
                    return getSprite(Sprite.Regions.TERRITORY_LIGHT_GREEN);
                } else {
                    return getSprite(Sprite.Regions.DICE_LIGHT_GREEN);
                }
            case Player.COLOR_YELLOW:
                if (territory) {
                    return getSprite(Sprite.Regions.TERRITORY_YELLOW);
                } else {
                    return getSprite(Sprite.Regions.DICE_YELLOW);
                }
            case Player.COLOR_BLUE:
            default:
                if (territory) {
                    return getSprite(Sprite.Regions.TERRITORY_BLUE);
                } else {
                    return getSprite(Sprite.Regions.DICE_BLUE);
                }
            case Player.COLOR_ORANGE:
                if (territory) {
                    return getSprite(Sprite.Regions.TERRITORY_ORANGE);
                } else {
                    return getSprite(Sprite.Regions.DICE_ORANGE);
                }
            case Player.COLOR_DARK_PURPLE:
                if (territory) {
                    return getSprite(Sprite.Regions.TERRITORY_DARK_PURPLE);
                } else {
                    return getSprite(Sprite.Regions.DICE_DARK_PURPLE);
                }
            case Player.COLOR_DARK_GREEN:
                if (territory) {
                    return getSprite(Sprite.Regions.TERRITORY_DARK_GREEN);
                } else {
                    return getSprite(Sprite.Regions.DICE_DARK_GREEN);
                }
            case Player.COLOR_RED:
                if (territory) {
                    return getSprite(Sprite.Regions.TERRITORY_RED);
                } else {
                    return getSprite(Sprite.Regions.DICE_RED);
                }
        }
    }

    @Override
    public void dispose() {

        if (this.spriteList != null) {
            for (Sprite.Regions region : spriteList.keySet()) {

                if (this.spriteList.get(region) != null)
                    this.spriteList.get(region).dispose();

                this.spriteList.put(region, null);
            }
            this.spriteList.clear();
        }

        this.spriteList = null;
    }
}