package com.monstrous.underthesea.screens;

import com.badlogic.gdx.Application;
import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.utils.Array;
import com.monstrous.underthesea.Assets;
import com.monstrous.underthesea.Settings;
import com.monstrous.underthesea.World;
import com.monstrous.underthesea.leaderboard.GameJolt;
import com.monstrous.underthesea.leaderboard.LeaderBoard;
import com.monstrous.underthesea.leaderboard.LeaderBoardEntry;
import com.monstrous.underthesea.screens.GameScreen;
import com.monstrous.underthesea.terrain.Chunks;

public class Main extends Game {
    public final boolean RELEASE_BUILD = true;
    public final String VERSION = "Version 1.0.1, July 2023";

    public Assets assets;
    public Chunks chunks;         // to persist between restarts
    public GameJolt gameJolt;
    public String userName;
    private Preferences preferences;
    public LeaderBoard leaderBoard;

    @Override
    public void create() {
        Gdx.app.log("Main create()", "");
        assets = new Assets();
        chunks = null;

//        if(RELEASE_BUILD)
//            Gdx.app.setLogLevel(Application.LOG_ERROR);
//        else
            Gdx.app.setLogLevel(Application.LOG_DEBUG);

        Gdx.app.log("Gdx version", com.badlogic.gdx.Version.VERSION);
        Gdx.app.log("OpenGL version", Gdx.gl.glGetString(Gdx.gl.GL_VERSION));

        preferences = Gdx.app.getPreferences(Settings.preferencesName);
        userName = preferences.getString("username", "Anon");

        leaderBoard = new LeaderBoard();


       // if( Gdx.app.getType() != Application.ApplicationType.WebGL) {
            gameJolt = new GameJolt();              // disabled because doesn't work on web (teavm) version
            gameJolt.init(leaderBoard);
      //  }

        if(RELEASE_BUILD)
            setScreen(new TitleScreen(this));
        else
            setScreen(new LoadScreen(this));
    }

    // to be called (from LoadingScreen) when asset loading is complete
    public void postLoading() {
    }

    public void render() {
        super.render(); // important!
    }

    @Override
    public void dispose() {
        Gdx.app.log("Main dispose()", "");

        super.dispose();    // calls screen.hide()

        // save username for next time
        preferences.putString("username", userName);   // save
        preferences.flush();


        Gdx.app.log("assets.dispose()", "");
        assets.dispose();
        if(chunks != null)
            chunks.dispose();
    }
}
