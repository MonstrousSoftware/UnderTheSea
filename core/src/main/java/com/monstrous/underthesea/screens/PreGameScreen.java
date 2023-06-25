package com.monstrous.underthesea.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.ScreenUtils;
import com.monstrous.underthesea.terrain.Chunks;

// this screen is called before the game screen and immediately calls the game screen
// this in case the game screen takes time to load, then at least there is something on screen


public class PreGameScreen extends ScreenAdapter {

    private SpriteBatch batch;
    private Main game;
    private Texture texture;
    private float timer;
    private Chunks chunks;
    private BitmapFont font;
    private String status;


    public PreGameScreen(Main game) {
        this.game = game;
    }

    @Override
    public  void show() {

        Gdx.app.debug("PreGameScreen", "show()");
        batch = new SpriteBatch();
        texture = new Texture( Gdx.files.internal("images/generating.png"));
        font = new BitmapFont();
        font.getRegion().getTexture().setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);
        font.getData().setScale(2);
        timer = 0.5f;

        chunks = game.chunks;
        if(chunks == null) {
            chunks = new Chunks();
            game.chunks = chunks;
        }
    }

    @Override
    public  void hide() {
        Gdx.app.debug("PreGameScreen", "hide()");
        dispose();
    }

    @Override
    public void dispose() {
        Gdx.app.debug("PreGameScreen", "dispose()");
        batch.dispose();
        texture.dispose();
    }

    @Override
    public void render( float deltaTime )
    {
        int n = chunks.generate();
        Gdx.app.log("Generating:", "chunk: "+n+"//48");


        timer -= deltaTime;
        if(n < 0 ) {
            game.setScreen(new GameScreen(game));   // load game screen automatically
            return;
        }

        status = ""+n+"/48";
        float x = (Gdx.graphics.getWidth()-texture.getWidth())/2f;

        // put loading texture centred on a black background
        ScreenUtils.clear(Color.BLACK);
        batch.begin();
        batch.draw(texture, x,(Gdx.graphics.getHeight()-texture.getHeight())/2f);
        font.draw(batch, status, x, (Gdx.graphics.getHeight()-texture.getHeight())/4f);
        batch.end();
    }

    @Override
    public void resize(int width, int height) {

        Gdx.app.debug("PreGameScreen", "resize("+width+", "+height+")");
        batch.getProjectionMatrix().setToOrtho2D(0, 0, width, height);
    }

}
