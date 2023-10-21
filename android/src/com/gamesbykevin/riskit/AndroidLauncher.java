package com.gamesbykevin.riskit;

import android.os.Bundle;

import com.badlogic.gdx.backends.android.AndroidApplication;
import com.badlogic.gdx.backends.android.AndroidApplicationConfiguration;

import de.golfgl.gdxgamesvcs.GpgsClient;
import de.golfgl.gdxgamesvcs.NoGameServiceClient;

public class AndroidLauncher extends AndroidApplication {

	//client for achievements and leaderboards
	GpgsClient client;

	@Override
	protected void onCreate (Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		AndroidApplicationConfiguration config = new AndroidApplicationConfiguration();
		config.useAccelerometer = false;
		config.useCompass = false;
		config.useGyroscope = false;

		if (MyGdxGame.GOOGLE_PLAY) {

			//create our game services client
			this.client = new GpgsClient().initialize(this, false);
			initialize(new MyGdxGame(this.client), config);

		} else {
			initialize(new MyGdxGame(new NoGameServiceClient()), config);
		}
	}
}
