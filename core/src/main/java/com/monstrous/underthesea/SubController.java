package com.monstrous.underthesea;

//  captures key presses and updates control variables for submarine

import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputAdapter;

public class SubController extends InputAdapter {

    public static float MAX_DIVE_ANGLE =  45;        // degrees
    public static float MAX_STEER_ANGLE =  15;        // degrees
    public static float MAX_POWER = 100f;


    public float diveAngle = 0;
    public float steerAngle = 0;            // negative to steer left
    public float power = 50f;

    private boolean leftPressed;
    private boolean rightPressed;
    private boolean forwardPressed;
    private boolean backwardPressed;
    private boolean upPressed;
    private boolean downPressed;

    public SubController() {
        leftPressed = false;
        rightPressed = false;
        forwardPressed = false;
        backwardPressed = false;
        upPressed = false;
        downPressed = false;
    }

    public void update(float deltaTime) {
        if(forwardPressed && diveAngle > -MAX_DIVE_ANGLE)
            diveAngle -= deltaTime * 20f;
        if(backwardPressed && diveAngle < MAX_DIVE_ANGLE)
            diveAngle += deltaTime * 20f;

        if(leftPressed && steerAngle > -MAX_STEER_ANGLE)
            steerAngle -= deltaTime * 20f;
        if(rightPressed && steerAngle < MAX_STEER_ANGLE)
            steerAngle += deltaTime * 20f;

        if(upPressed && power < MAX_POWER)
            power += deltaTime* 40f;
        if(downPressed && power > -MAX_POWER)
            power -= deltaTime* 40f;

    }

    @Override
    public boolean keyDown(int keycode) {
        return setKeyState(keycode, true);
    }

    @Override
    public boolean keyUp(int keycode) {
        return setKeyState(keycode, false);
    }


    private boolean setKeyState(int keycode, boolean state) {

        boolean handled = true;
        switch (keycode) {
            case Input.Keys.W:
                forwardPressed = state;
                break;
            case Input.Keys.A:
                leftPressed = state;
                break;
            case Input.Keys.S:
                backwardPressed = state;
                break;
            case Input.Keys.D:
                rightPressed = state;
                break;
            case Input.Keys.UP:
            case Input.Keys.E:
                upPressed = state;
                break;
            case Input.Keys.DOWN:
            case Input.Keys.Q:
                downPressed = state;
                break;
            default:
                handled = false;    // if none of the above cases, the key press is not handled
                break;
        }
        return handled;    // did we process the key event?
    }
}
