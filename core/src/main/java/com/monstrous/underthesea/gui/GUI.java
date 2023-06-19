package com.monstrous.underthesea.gui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.utils.Disposable;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.monstrous.underthesea.World;

public class GUI implements Disposable {

    private Skin skin;
    public Stage stage;
    private SettingsWindow settingsWindow;
    private Label statusLabel;
    private World world;


    public GUI( World world) {
        Gdx.app.log("GUI constructor", "");
        this.world = world;
        skin = new Skin(Gdx.files.internal("Particle Park UI Skin/Particle Park UI.json"));
        stage = new Stage(new ScreenViewport());

        settingsWindow = new SettingsWindow("Settings", skin, world);
    }

    private void rebuild() {
        stage.clear();

        statusLabel = new Label("Status", skin, "subtitle");


        Table screenTable = new Table();
        screenTable.setFillParent(true);
        screenTable.add(settingsWindow).top().right().expand();
        screenTable.row();
        screenTable.add(statusLabel).bottom().left();

        stage.addActor(screenTable);
        //stage.addActor(settingsWindow);

    }



    public void render(float deltaTime) {

        statusLabel.setText(String.format("Elev: %.0f HDG: %.0f PWR: %.0f", world.subController.diveAngle, world.subController.steerAngle, world.subController.power ));

        stage.act(deltaTime);
        stage.draw();
    }

    public void resize(int width, int height) {
        Gdx.app.log("GUI resize", "gui " + width + " x " + height);
        stage.getViewport().update(width, height, true);
        rebuild();
    }


    @Override
    public void dispose () {
        Gdx.app.log("GUI dispose()", "");
        stage.dispose();
        skin.dispose();
    }

}
