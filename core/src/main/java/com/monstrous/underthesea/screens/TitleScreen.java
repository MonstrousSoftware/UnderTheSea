package com.monstrous.underthesea.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.monstrous.underthesea.Settings;


// Title Screen
// note this screen does not rely on Assets being loaded.
// keeps the image in the screen regardless of resize and aspect ratio (may have letterbox bars where needed)
//
public class TitleScreen extends StdScreenAdapter {

    private Main game;
    private SpriteBatch batch;
    private FitViewport viewport;
    private Texture image;
    private int originalWidth, originalHeight;
    private float timer;

    public TitleScreen(Main game) {
        Gdx.app.log("TitleScreen constructor", "");
        this.game = game;
    }

    @Override
    public void show() {
        Gdx.app.log("TitleScreen show()", "");

        batch = new SpriteBatch();
        originalWidth = Gdx.graphics.getWidth();
        originalHeight = Gdx.graphics.getHeight();

        image = new Texture("images/title.png");
        viewport = new FitViewport(image.getWidth(), image.getHeight());    // we want the full picture in the view port
        timer = Settings.titleScreenTime;     // time to show title screen (seconds)
    }


    @Override
    public void render(float deltaTime) {
        super.render(deltaTime);

        game.assets.update();   // load assets asynchronously in the meantime

        // show this screen for fixed amount of time and then go to next screen
        timer -= deltaTime;
        if(timer <= 0){
            game.setScreen(new LoadScreen(game));
            return;
        }

        viewport.apply();
        ScreenUtils.clear(53f/255f, 42f/255f, 140f/255f, 1);

        batch.begin();
        batch.draw(image, 0,0,originalWidth, originalHeight);
        batch.end();
    }

    @Override
    public void resize(int w, int h) {
        Gdx.app.log("TitleScreen resize()", "");
        viewport.update(w,h);
    }

    @Override
    public void hide() {
        Gdx.app.log("TitleScreen hide()", "");
        dispose();
    }

    @Override
    public void dispose() {
        Gdx.app.log("TitleScreen dispose()", "");
        batch.dispose();
        image.dispose();
    }

}
