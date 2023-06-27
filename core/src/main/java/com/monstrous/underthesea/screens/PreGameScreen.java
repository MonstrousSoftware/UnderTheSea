package com.monstrous.underthesea.screens;


import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.ProgressBar;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.monstrous.underthesea.terrain.Chunks;

// This screen is called before the game screen
// It does the chunk generation because this takes time.
// It shows a progress bar.
// The chunks are under Main so that we don't need to regenerate if the user flips back to the menu screen and game screen.


public class PreGameScreen extends StdScreenAdapter {

    private Main game;
    private Chunks chunks;
    private Stage stage;
    private Skin skin;
    private ProgressBar progressBar;
    private Texture texture;


    public PreGameScreen(Main game) {
        this.game = game;
    }

    @Override
    public  void show() {
        skin = game.assets.get("Particle Park UI Skin/Particle Park UI.json");
        stage = new Stage(new ScreenViewport());

        progressBar = new ProgressBar(0, 48, 1, false, skin);
        progressBar.setSize(300, 50);
        progressBar.setValue(5);

        texture =  game.assets.get("images/generating.png");
        Image generating = new Image( new TextureRegion(texture));

        Table screenTable = new Table();
        screenTable.setFillParent(true);
        screenTable.add(generating).row();
        screenTable.add(progressBar).top().pad(50);
        screenTable.pack();

        stage.addActor(screenTable);

        chunks = game.chunks;
        if(chunks == null) {
            chunks = new Chunks();
            game.chunks = chunks;
        }
    }

    @Override
    public  void hide() {
        dispose();
    }

    @Override
    public void dispose() {
        texture.dispose();
        stage.dispose();
    }

    @Override
    public void render( float deltaTime )
    {
        super.render(deltaTime);

        int n = chunks.generate();
        if(n < 0 ) {
            game.setScreen(new GameScreen(game));   // load game screen automatically
            return;
        }
        progressBar.setValue(n);

        ScreenUtils.clear(Color.BLACK);

        stage.act(deltaTime);
        stage.draw();
    }

    @Override
    public void resize(int width, int height) {
        stage.getViewport().update(width, height, true);
    }

}
