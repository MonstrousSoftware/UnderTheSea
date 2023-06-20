package com.monstrous.underthesea;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.math.Vector3;




public class CamController extends InputAdapter {

    private PerspectiveCamera cam;
    private Vector3 tmpVec = new Vector3();
    private float distance = 30f;
    private float horizontalAngle = 0;
    private float verticalAngle = 0;
    private Vector3 dist;
    private float startX, startY;

    public CamController(PerspectiveCamera cam) {

        this.cam = cam;
        dist = new Vector3();
    }

    public void update( Vector3 target) {
        dist.set(0,distance/3, -distance);
        dist.rotate(Vector3.X, verticalAngle);
        dist.rotate(Vector3.Y, horizontalAngle);

        tmpVec.set(target);
        tmpVec.add(dist);
        cam.position.set(tmpVec);
        cam.lookAt(target);
        cam.up.set(Vector3.Y);
        cam.update();

    }

    @Override
    public boolean scrolled (float amountX, float amountY) {
        if( distance > 5 || amountY > 0)
            distance += amountY;
        return true;
    }

    @Override
    public boolean touchDown (int screenX, int screenY, int pointer, int button) {
            startX = screenX;
            startY = screenY;
            return true;
    }

    @Override
    public boolean touchDragged (int screenX, int screenY, int pointer) {

        final float deltaX = (screenX - startX) / Gdx.graphics.getWidth();
        final float deltaY = (startY - screenY) / Gdx.graphics.getHeight();
        startX = screenX;
        startY = screenY;
        horizontalAngle -= deltaX*180f;
        verticalAngle -= deltaY*180f;
        return true;
    }

    @Override
    public boolean keyDown (int keycode) {

        if (keycode == Input.Keys.P)
            distance ++;
        else if (keycode == Input.Keys.O && distance > 5 )
            distance --;
        else if (keycode == Input.Keys.K)
            horizontalAngle -=5;
        else if (keycode == Input.Keys.L  )
            horizontalAngle += 5;
        return false;
    }
}
