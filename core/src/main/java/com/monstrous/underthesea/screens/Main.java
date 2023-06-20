package com.monstrous.underthesea.screens;

import com.badlogic.gdx.Application;
import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.monstrous.underthesea.Assets;
import com.monstrous.underthesea.screens.GameScreen;

public class Main extends Game {
    public final boolean RELEASE_BUILD = false;
    public final String VERSION = "Under The Sea - June 2023";

    public Assets assets;

    @Override
    public void create() {
        Gdx.app.log("Main create()", "");
        assets = new Assets();

        if(RELEASE_BUILD)
            Gdx.app.setLogLevel(Application.LOG_ERROR);
        else
            Gdx.app.setLogLevel(Application.LOG_DEBUG);

        Gdx.app.log("Gdx version", com.badlogic.gdx.Version.VERSION);
        Gdx.app.log("OpenGL version", Gdx.gl.glGetString(Gdx.gl.GL_VERSION));


        if(RELEASE_BUILD)
            setScreen(new TitleScreen(this));
        else
            setScreen(new LoadScreen(this));
    }

    // to be called (from LoadingScreen) when asset loading is complete
    public void postLoading() {
//        musicManager = new MusicManager(assets);
//        sounds = new Sounds(assets);
    }

    public void render() {
        super.render(); // important!
    }

    @Override
    public void dispose() {
        Gdx.app.log("Main dispose()", "");

        super.dispose();    // calls screen.hide()

//        musicManager.stopMusic();
//        musicManager.dispose();
//        sounds.dispose();

        Gdx.app.log("assets.dispose()", "");
        assets.dispose();
    }
}
