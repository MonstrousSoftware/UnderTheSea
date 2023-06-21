package com.monstrous.underthesea.gui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.Disposable;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.monstrous.underthesea.MarchingCubes;
import com.monstrous.underthesea.World;

public class GUI implements Disposable {

    private Skin skin;
    public Stage stage;
    private SettingsWindow settingsWindow;
    private Label statusLabel;
    private World world;
    private Image steerGauge;


    public GUI( World world) {
        Gdx.app.log("GUI constructor", "");
        this.world = world;
        skin = new Skin(Gdx.files.internal("Particle Park UI Skin/Particle Park UI.json"));
        stage = new Stage(new ScreenViewport());

        settingsWindow = new SettingsWindow("Settings", skin, world);
    }

    private void rebuild() {
        stage.clear();

        steerGauge = new Image( new Texture("images/gauge.png"));



        statusLabel = new Label("Status", skin, "subtitle");







        final Slider sliderHDG = new Slider(-25, 25, 1, false, skin);
        sliderHDG.setAnimateDuration(0.1f);
        sliderHDG.setValue((int)world.subController.steerAngle);
        sliderHDG.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                world.subController.steerAngle = sliderHDG.getValue();
            }
        });

        Table t1 = new Table();
        t1.add(new Label("R U D D E R", skin)).row();

        Stack stackHDG = new Stack();
        stackHDG.add(new Image(new Texture("images/gauge.png")));
        stackHDG.add(sliderHDG);
        t1.add(stackHDG);
        t1.pack();


        final Slider sliderDive = new Slider(-25, 25, 1, true, skin);
        sliderDive.setAnimateDuration(0.1f);
        sliderDive.setValue((int)world.subController.diveAngle);
        sliderDive.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                world.subController.diveAngle = sliderDive.getValue();
            }
        });
        Stack stackDive = new Stack();
        stackDive.add(new Image(new Texture("images/gaugeVertical.png")));
        stackDive.add(sliderDive);

        Table t2 = new Table();
        t2.add(stackDive);
        t2.add(new Label("I\nN\nC\nL\nI\nN\nE", skin)).pad(4);
        t2.pack();



        final Slider sliderPower = new Slider(-100, 100, 10, true, skin);
        sliderPower.setAnimateDuration(0.1f);
        sliderPower.setValue((int)world.subController.power);
        sliderPower.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                world.subController.power = sliderPower.getValue();
            }
        });

        Stack stackPower = new Stack();
        stackPower.add(new Image(new Texture("images/gaugeVertical.png")));
        stackPower.add(sliderPower);
        Table t3 = new Table();
        t3.add(new Label("P\nO\nW\nE\nR\n", skin));
        t3.add(stackPower);
        t3.pack();


        Table screenTable = new Table();
        screenTable.setFillParent(true);

        screenTable.add(t2).left().bottom();
        screenTable.add(t1).center().bottom().expand();
        screenTable.add(t3).right().bottom();
        screenTable.pack();



        stage.addActor(screenTable);
        //stage.addActor(settingsWindow);

    }



    public void render(float deltaTime) {

//        statusLabel.setText(String.format("Elev: %.0f HDG: %.0f PWR: %.0f v=(%s)", world.subController.diveAngle, world.subController.steerAngle, world.subController.power,
//                            world.submarine.velocity.toString()));

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
