package com.gamesbykevin.riskit;

import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Application;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3ApplicationConfiguration;
import com.gamesbykevin.riskit.util.MyGdxGameHelper;

// Please note that on macOS your application needs to be started with the -XstartOnFirstThread JVM argument
public class DesktopLauncher {
	public static void main (String[] arg) {
		Lwjgl3ApplicationConfiguration config = new Lwjgl3ApplicationConfiguration();
		config.setForegroundFPS(60);
		config.setWindowedMode(MyGdxGameHelper.WIDTH, MyGdxGameHelper.HEIGHT);
		config.setTitle("Risk It");
		new Lwjgl3Application(new MyGdxGame(), config);
	}
}