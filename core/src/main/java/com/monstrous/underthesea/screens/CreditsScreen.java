package com.monstrous.underthesea.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.monstrous.underthesea.screens.Main;
import com.monstrous.underthesea.screens.MenuScreen;

public class CreditsScreen implements Screen {

    static public int TEXT_WIDTH = 400;
    static public int BUTTON_WIDTH = 200;
    static public int BUTTON_PAD = 20;

    private Main game;
    private Stage stage;
    private Skin skin;

    public CreditsScreen(Main game) {
        this.game = game;
    }

    @Override
    public void show() {
        Gdx.app.debug("CreditsScreen", "show()");

        skin = new Skin(Gdx.files.internal("blue-pixel-skin/blue-pixel.json"));
        //skin = new Skin(Gdx.files.internal("sgx.skin/sgx-ui.json"));
        stage = new Stage(new ScreenViewport());
        rebuild();
        Gdx.input.setInputProcessor(stage);
    }

    private void rebuild() {
        String explanation = "This game was made for the " +
                "LibGDX Game Jam of June 2023 with " +
                "the theme: Under Water.\n" +
                "Models were created with Blender 3.5.\n" +
				"GDX-GLTF library by mgsx for model loading and PBR rendering.\n"+
				"GDX-TeaVM library by xpenatan used for for the HTML version.\n"+
                "UI Skin: Particle Park by Raeleus.\n"+
				"Inspiration: Marching Cubes by Sebastian Lague on YouTube.\n"+
                "Sound effects from Pixabay.\n"+
                "Play testing: Jake Snake.\n";


        stage.clear();

        // root table that fills the whole screen
        Table screenTable = new Table();
        stage.addActor(screenTable);
        screenTable.setFillParent(true);        // size to match stage size

        Label labelTitle = new Label("CREDITS", skin, "title");

        Label labelText = new Label(explanation, skin);
        labelText.setWrap(true);

        Table menuTable = new Table();


        TextButton backButton = new TextButton("BACK", skin);


        menuTable.add(labelText).width(TEXT_WIDTH).center().row();
        menuTable.add(backButton).width(BUTTON_WIDTH).pad(BUTTON_PAD).row();


        Pixmap pixmap = new Pixmap(1, 1, Pixmap.Format.RGBA8888);
        pixmap.setColor(Color.LIGHT_GRAY);
        pixmap.fill();
        screenTable.setBackground( new TextureRegionDrawable(new TextureRegion(new Texture(pixmap))) );
        screenTable.add(labelTitle).top().pad(50).row();
        screenTable.add(menuTable);

        screenTable.pack();


        backButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                super.clicked(event, x, y);
                game.setScreen( new MenuScreen(game, true) );
            }
        });
    }


    @Override
    public void render(float delta) {
        stage.act(delta);
        stage.draw();
    }

    @Override
    public void resize(int width, int height) {
        Gdx.app.debug("CreditsScreen", "resize()");
        stage.getViewport().update(width, height, true);
    }

    @Override
    public void pause() {
        Gdx.app.debug("CreditsScreen", "pause()");
    }

    @Override
    public void resume() {
        Gdx.app.debug("CreditsScreen", "resume()");
    }

    @Override
    public void hide() {
        Gdx.app.debug("CreditsScreen", "hide()");
        dispose();
    }

    @Override
    public void dispose() {
        Gdx.app.debug("CreditsScreen", "dispose()");
        stage.dispose();
    }
}
