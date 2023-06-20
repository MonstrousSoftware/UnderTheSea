package com.monstrous.underthesea;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector3;
import net.mgsx.gltf.loaders.gltf.GLTFLoader;
import net.mgsx.gltf.scene3d.scene.Scene;
import net.mgsx.gltf.scene3d.scene.SceneAsset;
import net.mgsx.gltf.scene3d.scene.SceneManager;

public class Submarine {

    private SceneAsset sceneAsset;
    private Scene sceneSub;
    private Scene sceneScrew;
    private Scene sceneFins;
    public  Vector3 position;
    public  Vector3 targetVelocity;
    public  Vector3 velocity;
    private Vector3 step;
    private Vector3 tip;
    private Vector3 tail;
    private float screwSpeed;     // -100 to 100
    private float screwAngle;
    private float heading;
    private float diveAngle;
    private boolean collided = false;
    private boolean rearCollided = false;

    public Submarine( SceneManager sceneManager, float x, float y, float z ) {
        sceneAsset = new GLTFLoader().load(Gdx.files.internal("models/submarine.gltf"));

        sceneSub = new Scene(sceneAsset.scene, "submarine");
        sceneSub.modelInstance.transform.translate(x, y, z);
        sceneManager.addScene(sceneSub);
        sceneScrew = new Scene(sceneAsset.scene, "screw");
        sceneScrew.modelInstance.transform.translate(x, y, z);
        sceneManager.addScene(sceneScrew);
        sceneFins = new Scene(sceneAsset.scene, "fins");
        sceneFins.modelInstance.transform.translate(x, y, z);
        sceneManager.addScene(sceneFins);

        position = new Vector3(x, y, z);
        targetVelocity = new Vector3(0, 0, 1);
        velocity = new Vector3(0, 0, 1);
        tip = new Vector3();
        tail = new Vector3();
        heading = 0;
        diveAngle = 0;

        step = new Vector3();
    }

    Vector3 tmpVec = new Vector3();

    public void update( float deltaTime, SubController subController ){
        if(collided && subController.power > 0)
            return; // freeze position, unless power is reversed
        collided = false;

        if(rearCollided && subController.power < 0)
            return; // freeze position, unless power is reversed
        rearCollided = false;

        screwSpeed = subController.power;
        screwAngle +=  4*screwSpeed*deltaTime;


        // sub reacts with some lag on the inputs to give some inertia
        heading += subController.steerAngle * deltaTime;
        diveAngle = MathUtils.lerp(diveAngle, subController.diveAngle, deltaTime);

        targetVelocity.set(0,0,screwSpeed/50f);
        targetVelocity.rotate(Vector3.X, -diveAngle);
        targetVelocity.rotate(Vector3.Y, heading);

        // actual movement velocity lags on target velocity
        velocity.slerp(targetVelocity, deltaTime);

        step.set(velocity).scl(deltaTime);          // x = v * dt
        position.add(step);


        sceneSub.modelInstance.transform.setToRotation(Vector3.Y, heading);
        sceneSub.modelInstance.transform.rotate(Vector3.X, -2*diveAngle);
        sceneSub.modelInstance.transform.setTranslation(position);

        sceneScrew.modelInstance.transform.setToRotation(Vector3.Z, screwAngle);
        sceneScrew.modelInstance.transform.mulLeft(sceneSub.modelInstance.transform);

        sceneFins.modelInstance.transform.setToRotation(Vector3.X, -4*subController.diveAngle);
        sceneFins.modelInstance.transform.mulLeft(sceneSub.modelInstance.transform);

    }

    public void collide() {
        collided = true;
        velocity.set(0,0,0);
    }

    public void rearCollide() {
        rearCollided = true;
        velocity.set(0,0,0);
    }

    public Vector3 getPosition() {
        return position;
    }

    public Vector3 getTipPosition() {
        tip.set(0,0, 2.5f);     // front tip of the model, used for collision test
        tip.mul(sceneSub.modelInstance.transform);
        return tip;
    }

    public Vector3 getTailPosition() {
        tail.set(0,0, -2.5f);     // tail of the model, used for collision test
        tail.mul(sceneSub.modelInstance.transform);
        return tail;
    }
}
