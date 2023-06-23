package com.monstrous.underthesea.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.ScreenUtils;

public class LoadScreen extends ScreenAdapter {

    private Main game;

    private SpriteBatch batch;
    private BitmapFont font;
    private OrthographicCamera cam;
    private int width, height;

    public LoadScreen(Main game) {
        Gdx.app.log("LoadScreen constructor", "");
        this.game = game;
    }


    @Override
    public void show() {
        Gdx.app.log("LoadScreen show()", "");

        batch = new SpriteBatch();
        font = new BitmapFont();
        cam = new OrthographicCamera();
    }


    @Override
    public void render(float deltaTime) {

        // load assets asynchronously
        if(game.assets.update()) {
            // done loading, proceed...
            game.postLoading();
            if(game.RELEASE_BUILD)
                game.setScreen(new MenuScreen(game, false));
            else
                game.setScreen(new GameScreen(game));
            return;
        }

        cam.update();

        ScreenUtils.clear(Color.BLACK);

        batch.setProjectionMatrix(cam.combined);
        batch.begin();
        font.draw(batch, "Loading...", width/2, height/8);
        batch.end();
    }

    @Override
    public void resize(int w, int h) {
        this.width = w;
        this.height = h;
        Gdx.app.log("LoadScreen resize()", "");
        cam.setToOrtho(false, width, height);
    }

    @Override
    public void hide() {
        Gdx.app.log("LoadScreen hide()", "");
        dispose();
    }

    @Override
    public void dispose() {
        Gdx.app.log("LoadScreen dispose()", "");
        batch.dispose();
        font.dispose();
    }

}
