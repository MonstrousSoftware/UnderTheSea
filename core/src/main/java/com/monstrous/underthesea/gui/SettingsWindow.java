package com.monstrous.underthesea.gui;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.monstrous.underthesea.MarchingCubes;
import com.monstrous.underthesea.World;



public class SettingsWindow extends Window {


   // private World world;

    public SettingsWindow(String title, Skin skin, World world) {
        super(title, skin);


        final Label labelISOValue = new Label(String.valueOf((int)MarchingCubes.isoThreshold), skin);
        final Slider sliderISO = new Slider(0, 255, 1, false, skin);
        sliderISO.setAnimateDuration(0.1f);
        sliderISO.setValue((int)MarchingCubes.isoThreshold);
        sliderISO.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                MarchingCubes.isoThreshold = (char)sliderISO.getValue();
                labelISOValue.setText(String.valueOf((int)MarchingCubes.isoThreshold));
                world.rebuild();
            }
        });
        add(new Label("Threshold: ", skin)).pad(5);
        add(sliderISO);
        add(labelISOValue).width(50);
        row();



        CheckBox cb = new CheckBox("Wireframe", skin);
        cb.setChecked(MarchingCubes.wireframeMode);
        cb.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                MarchingCubes.wireframeMode = cb.isChecked();
                world.rebuild();
            }
        });


        add(cb).colspan(3).pad(5);
        row();

//        Button reset = new TextButton("Reset", skin);
//        reset.addListener(new ChangeListener() {
//            @Override
//            public void changed(ChangeEvent event, Actor actor) {
//                world.rebuild();
//            }
//        });
//        add(reset).colspan(3).pad(5);
//        row();

        pack();

    }
}
