package com.monstrous.underthesea.gui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Disposable;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.monstrous.underthesea.SubController;
import com.monstrous.underthesea.World;
import com.monstrous.underthesea.screens.Main;
import com.monstrous.underthesea.screens.MenuScreen;

public class GUI implements Disposable {

    private Skin skin;
    public Stage stage;
    private SettingsWindow settingsWindow;
    private Label depthLabel;
    private Label distanceLabel;
    private World world;
    private Image steerGauge;
    private Slider sliderDive;
    private Slider sliderRudder;
    private Slider sliderPower;
    private Image collision;
    private Label message;
    private TextButton confirmButton;
    public boolean exitButtonPressed;
    private Dialog exitDialog;

    public GUI( World world) {
        Gdx.app.log("GUI constructor", "");
        this.world = world;
        skin = new Skin(Gdx.files.internal("Particle Park UI Skin/Particle Park UI.json"));
        stage = new Stage(new ScreenViewport());

        settingsWindow = new SettingsWindow("Settings", skin, world);
        exitButtonPressed = false;
    }

    private void rebuild() {
        stage.clear();

        steerGauge = new Image( new Texture("images/gauge.png"));

        collision = new Image( new Texture("images/collision.png"));


        depthLabel = new Label("Status", skin, "window");
        distanceLabel = new Label("Status", skin, "window");

        message = new Label("TEST", skin, "window");
        message.setVisible(false);

        ImageButton backButton = new ImageButton(new TextureRegionDrawable(new TextureRegion(new Texture(Gdx.files.internal("images/b_exit.png")))));
        backButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                super.clicked(event, x, y);
                exitButtonPressed = true;
            }
        });


        sliderRudder = new Slider(-SubController.MAX_STEER_ANGLE, SubController.MAX_STEER_ANGLE, 1, false, skin);
        sliderRudder.setAnimateDuration(0.1f);
        sliderRudder.setValue((int)world.subController.steerAngle);
        sliderRudder.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                world.subController.steerAngle = sliderRudder.getValue();
            }
        });

        Table t1 = new Table();
        t1.add(new Label("R U D D E R", skin)).row();

        Stack stackHDG = new Stack();
        stackHDG.add(new Image(new Texture("images/gauge.png")));
        stackHDG.add(sliderRudder);
        t1.add(stackHDG);
        t1.pack();


        sliderDive = new Slider(-SubController.MAX_DIVE_ANGLE, SubController.MAX_DIVE_ANGLE, 1, true, skin);
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



        sliderPower = new Slider(-SubController.MAX_POWER, SubController.MAX_POWER, 10, true, skin);
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
        screenTable.add(backButton).colspan(4).bottom().expandY().left().row();
        screenTable.add(t2).left().bottom();
        screenTable.add(t1).center().bottom().expandX();
        screenTable.add(t3).right().bottom().row();
        screenTable.add(depthLabel).bottom();
        screenTable.add(distanceLabel).bottom();
        screenTable.pack();


        Table msg = new Table();
        confirmButton = new TextButton("CONFIRM", skin);
        confirmButton.setDisabled(true);
        confirmButton.setVisible(false);

        confirmButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                message.setVisible(false);
                confirmButton.setDisabled(true);
                confirmButton.setVisible(false);
            }
        });
        msg.add(message).row();
        msg.add(confirmButton).center();
        msg.pack();


        Table screenTable2 = new Table();
        screenTable2.setFillParent(true);

        screenTable2.add(collision).pad(5).top().row();
        screenTable2.add(msg).right().pad(20).center().expand();

        //screenTable2.add(confirmButton).pad(5).top().expand().row();
       // screenTable2.add(statusLabel).pad(20).center();
        screenTable2.pack();

        stage.addActor(screenTable);
        stage.addActor(screenTable2);

        //stage.addActor(settingsWindow);

    }

    public void exitDialog( Main game ) {
        if(exitDialog != null)
            return;
        exitButtonPressed = false;
        exitDialog = new Dialog("Exit Game?", skin){
            protected void result(Object object) {
                Gdx.app.log("exit dialog", "");
                if(object.equals(1)){
                    game.setScreen( new MenuScreen(game, true));
                }
                else {
                    remove();
                    exitDialog = null;
                }
            }
        };
        exitDialog.text("\nAre you sure you want to exit?\n");
        exitDialog.button("Exit", 1);
        exitDialog.button("Cancel", 2);
        exitDialog.show(stage);
    }


    public void setCollision( boolean value ){
        collision.setVisible(value);
    }

    public void setMessage( String text ){
        message.setText(text);
//        if(message.isVisible())
//            return;
        message.setVisible(true);
        confirmButton.setDisabled(false);
        confirmButton.setVisible(true);
    }

    public void render(float deltaTime) {

//        statusLabel.setText(String.format("Elev: %.0f HDG: %.0f PWR: %.0f v=(%s)", world.subController.diveAngle, world.subController.steerAngle, world.subController.power,
//                            world.submarine.velocity.toString()));
         depthLabel.setText("DEPTH: "+(128-(int)world.submarine.position.y));
        distanceLabel.setText(String.format("DISTANCE: %d [%d,%d,%d] T=%d", (int)world.capsuleDistance,
            (int)world.submarine.position.x, (int)world.submarine.position.y, (int)world.submarine.position.z, (int)world.timer));
        //  distanceLabel.setText("DISTANCE: "+ (int)world.capsuleDistance);

        sliderRudder.setValue(world.subController.steerAngle);
        sliderDive.setValue(world.subController.diveAngle);
        sliderPower.setValue(world.subController.power);

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
