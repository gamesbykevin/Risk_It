package com.gamesbykevin.riskit;

import com.badlogic.gdx.Application;
import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.gamesbykevin.riskit.assets.Manager;
import com.gamesbykevin.riskit.screen.ParentScreen;
import com.gamesbykevin.riskit.screen.Splash;
import com.gamesbykevin.riskit.sprites.Sprites;
import com.gamesbykevin.riskit.util.MyGdxGameHelper;

import static com.gamesbykevin.riskit.screen.ParentScreen.Screens;

import java.util.HashMap;

import de.golfgl.gdxgamesvcs.IGameServiceClient;
import de.golfgl.gdxgamesvcs.NoGameServiceClient;

public class MyGdxGame extends Game {

	//is this android version for google play?
	public static final boolean GOOGLE_PLAY = true;

	//do we log messages?
	public static final int LOG_LEVEL = Application.LOG_INFO;

	private HashMap<ParentScreen.Screens, ParentScreen> screens;

	private ParentScreen.Screens currentKey;

	//manage games assets from here
	private Manager assetManager;

	//sprites for our game
	private Sprites sprites;

	//client to submit leaderboard scores and unlock achievements
	private IGameServiceClient gameServiceClient;

	public MyGdxGame() {
		this(new NoGameServiceClient());
	}

	public MyGdxGame(IGameServiceClient gameServiceClient) {
		this.gameServiceClient = gameServiceClient;
	}

	@Override
	public void create () {
		Gdx.app.setLogLevel(LOG_LEVEL);
		getAssetManager();
		this.screens = new HashMap<>();
		this.screens.put(Screens.Splash, new Splash(this));
		super.setScreen(getScreen(Screens.Splash));

		if (MyGdxGameHelper.isHtml())
			Gdx.graphics.setFullscreenMode(Gdx.graphics.getDisplayMode());
	}

	public IGameServiceClient getGameServiceClient() {
		return this.gameServiceClient;
	}

	public Sprites getSprites() {

		if (this.sprites == null)
			this.sprites = new Sprites(getAssetManager());

		return this.sprites;
	}

	public Manager getAssetManager() {

		if (this.assetManager == null) {
			this.assetManager = new Manager();
		}

		return this.assetManager;
	}

	public Screens getCurrentKey() {
		return this.currentKey;
	}

	public void addScreen(Screens key, ParentScreen parentScreen) {
		this.screens.put(key, parentScreen);
	}

	public ParentScreen getScreen() {
		return getScreen(getCurrentKey());
	}

	public ParentScreen getScreen(Screens screen) {
		return this.screens.get(screen);
	}

	public void selectScreen(Screens key) {
		this.currentKey = key;
		super.setScreen(getScreen());
	}

	@Override
	public void render () {
		super.render();
	}
	
	@Override
	public void dispose () {
		super.dispose();
	}
}