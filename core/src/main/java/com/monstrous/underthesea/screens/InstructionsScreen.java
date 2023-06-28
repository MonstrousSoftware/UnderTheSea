package com.monstrous.underthesea.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.audio.Sound;
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


public class InstructionsScreen extends StdScreenAdapter {

    static int NUM_PAGES = 2;
    static public int TEXT_WIDTH = 400;
    static public int BUTTON_WIDTH = 200;
    static public int BUTTON_PAD = 20;

    private Main game;
    private Stage stage;
    private Skin skin;
    private String[] texts;
    private int pageIndex = 0;
    private Sound click;

    public InstructionsScreen(Main game) {
        this.game = game;
    }

    @Override
    public void show() {
        Gdx.app.debug("InstructionsScreen", "show()");

        skin = new Skin(Gdx.files.internal("blue-pixel-skin/blue-pixel.json"));
        stage = new Stage(new ScreenViewport());
        rebuild();
        Gdx.input.setInputProcessor(stage);
        click = game.assets.get("sounds/click-for-game-menu.mp3");
    }

    private void rebuild() {
		texts = new String[NUM_PAGES];

        texts[0] = "When radio communications are compromised, submarine command (SUBCOM) may resort to sending messages to submarines by dropping a canister in the ocean. " +
                "It takes a skilled submariner to retrieve these canisters from the ocean floor\n\n" +
                "Luckily you are the experienced commander of the nuclear submarine X-25, nicknamed the Banana Man.\n\n" +
                "Your skills are about to be put to the test.";

        texts[1] = "Use the rudder (keys A, D) and the hydroplanes (keys W, S) to navigate.\n\n" +
                "Use Q and E keys (or Up and Down arrow) to control engine power.\n\n"+
				"You can also use the sliders on the gauges instead of the keyboard.\n\n"+
                "Locate a canister by monitoring the distance indicator. Pick up a canister by moving very close to it.\n\n " +
				"In case of collision, reverse the engine power.\n\n"+
				"Drag with the left mouse button to turn the camera. Scroll wheel to zoom.\n\n"+
                "Press ESC to exit the game and return to menu.";


        stage.clear();

        // root table that fills the whole screen
        Table screenTable = new Table();
        screenTable.setColor(Color.YELLOW);
        stage.addActor(screenTable);
        screenTable.setFillParent(true);        // size to match stage size

        Label labelTitle = new Label("INSTRUCTIONS", skin, "title");

        final Label labelText = new Label(texts[pageIndex], skin);
        labelText.setWrap(true);

        final Table menuTable = new Table();


        TextButton backButton = new TextButton("DONE", skin);
        final TextButton prevButton = new TextButton("<<", skin);
        final TextButton nextButton = new TextButton(">>", skin);

        Table buttons = new Table();
        buttons.add(backButton).width(BUTTON_WIDTH).pad(BUTTON_PAD);
        buttons.add(prevButton).width(BUTTON_WIDTH).pad(BUTTON_PAD);
        buttons.add(nextButton).width(BUTTON_WIDTH).pad(BUTTON_PAD);
        buttons.pack();


        Pixmap pixmap = new Pixmap(1, 1, Pixmap.Format.RGBA8888);
        pixmap.setColor(Color.YELLOW);
        pixmap.fill();
        screenTable.setBackground( new TextureRegionDrawable(new TextureRegion(new Texture(pixmap))) );

        screenTable.add(labelTitle).top().pad(50).row();                    // header
        screenTable.add(labelText).width(TEXT_WIDTH).center().top().expandY().row();    // text
        screenTable.add(buttons);       // buttons
        screenTable.pack();

        prevButton.setVisible(false);   // disable Prev button on first page

        backButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                super.clicked(event, x, y);
                click.play();
                game.setScreen( new MenuScreen(game, false) );
            }
        });
        prevButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                super.clicked(event, x, y);
                if(pageIndex > 0)
                    pageIndex--;
                labelText.setText(texts[pageIndex]);
                prevButton.setVisible(pageIndex > 0);   // hide Prev and Next button as needed
                nextButton.setVisible(true);
                click.play();
            }
        });
        nextButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                super.clicked(event, x, y);
                if(pageIndex < NUM_PAGES-1)
                    pageIndex++;
                labelText.setText(texts[pageIndex]);
                menuTable.pack();
                prevButton.setVisible(true);
                nextButton.setVisible(pageIndex < NUM_PAGES-1);
                click.play();
            }
        });
    }



    @Override
    public void render(float delta) {
        super.render(delta);
        stage.act(delta);
        stage.draw();
    }

    @Override
    public void resize(int width, int height) {
        Gdx.app.debug("InstructionsScreen", "resize()");
        stage.getViewport().update(width, height, true);
    }

    @Override
    public void pause() {
        Gdx.app.debug("InstructionsScreen", "pause()");
    }

    @Override
    public void resume() {
        Gdx.app.debug("InstructionsScreen", "resume()");
    }

    @Override
    public void hide() {
        Gdx.app.debug("InstructionsScreen", "hide()");
        dispose();
    }

    @Override
    public void dispose() {
        Gdx.app.debug("InstructionsScreen", "dispose()");
        stage.dispose();
    }
}
