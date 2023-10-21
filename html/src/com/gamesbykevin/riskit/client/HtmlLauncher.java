package com.gamesbykevin.riskit.client;

import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.backends.gwt.GwtApplication;
import com.badlogic.gdx.backends.gwt.GwtApplicationConfiguration;
import com.badlogic.gdx.backends.gwt.GwtGraphics;
import com.gamesbykevin.riskit.MyGdxGame;
import com.gamesbykevin.riskit.util.MyGdxGameHelper;

public class HtmlLauncher extends GwtApplication {

        @Override
        public GwtApplicationConfiguration getConfig () {
                GwtApplicationConfiguration config;
                config = new GwtApplicationConfiguration(true);
                config.fullscreenOrientation = GwtGraphics.OrientationLockType.LANDSCAPE;
                return config;
        }

        @Override
        public ApplicationListener createApplicationListener () {
                return new MyGdxGame();
        }
}