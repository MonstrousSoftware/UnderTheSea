package com.monstrous.underthesea.screens;

import com.badlogic.gdx.Application;
import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.monstrous.underthesea.Assets;
import com.monstrous.underthesea.Settings;
import com.monstrous.underthesea.World;
import com.monstrous.underthesea.screens.GameScreen;
import com.monstrous.underthesea.terrain.Chunks;

public class Main extends Game {
    public final boolean RELEASE_BUILD = true;
    public final String VERSION = "Under The Sea - June 2023";

    public Assets assets;
    public Chunks chunks;         // to persist between restarts

    @Override
    public void create() {
        Gdx.app.log("Main create()", "");
        assets = new Assets();
        chunks = null;

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
    }

    public void render() {
        super.render(); // important!
    }

    @Override
    public void dispose() {
        Gdx.app.log("Main dispose()", "");

        super.dispose();    // calls screen.hide()


        Gdx.app.log("assets.dispose()", "");
        assets.dispose();
        if(chunks != null)
            chunks.dispose();
    }
}
