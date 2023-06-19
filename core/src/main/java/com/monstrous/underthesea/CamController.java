package com.monstrous.underthesea;

import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector3;

public class CamController extends InputAdapter {

    private PerspectiveCamera cam;
    private Vector3 tmpVec = new Vector3();

    public CamController(PerspectiveCamera cam) {
        this.cam = cam;
    }

    public void update( Vector3 target) {
        tmpVec.set(target);
        tmpVec.add(0,10, -20);
        cam.position.set(tmpVec);
        cam.lookAt(target);
        cam.update();

    }
}
