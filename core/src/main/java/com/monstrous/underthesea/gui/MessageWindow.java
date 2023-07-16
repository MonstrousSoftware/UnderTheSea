package com.monstrous.underthesea.gui;

import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.scenes.scene2d.Action;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.Window;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;

public class MessageWindow extends Window {

    private float windowY;

    public MessageWindow(String title, Skin skin, Stage stage, String text) {
        super(title, skin);

        Label messageText = new Label(text, skin, "window");
        messageText.setWrap(true);


        TextButton okButton = new TextButton("CLOSE", skin);

        add(messageText).width(300).pad(10).row();
        add(okButton).pad(10);
        pack();

        // target position for this window
        float wx = 5;
        windowY = 2*(stage.getHeight() - getHeight())/3;
        setKeepWithinStage(false);  // allow window to go offscreen

        // animate that the window comes in from the left
        setPosition(-getWidth(), windowY);     //  place at top of the screen
        addAction(Actions.moveTo(wx, windowY, .4f, Interpolation.swingOut));


        okButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                close();
            }
        });
    }

    private void close() {
        // swing window off-screen and then remove the window
        // remove is called as part of the action sequence so that the animation can play
        addAction(Actions.sequence(Actions.moveTo(-getWidth(), windowY, .4f, Interpolation.swingIn),
            new Action() {
                @Override
                public boolean act(float delta) {
                    remove();
                    return true;
                }
            }));
    }


}
