package com.monstrous.underthesea;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Vector3;
import net.mgsx.gltf.loaders.gltf.GLTFLoader;
import net.mgsx.gltf.scene3d.scene.Scene;
import net.mgsx.gltf.scene3d.scene.SceneAsset;
import net.mgsx.gltf.scene3d.scene.SceneManager;

public class Submarine {

    private float screwSpeed = 50f;     // -100 to 100

    private SceneAsset sceneAsset;
    private Scene sceneSub;
    private Scene sceneScrew;
    private Scene sceneFins;
    public  Vector3 position;
    public  Vector3 velocity;
    public  Vector3 acceleration;
    private Vector3 step;
    private float screwAngle;

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
        velocity = new Vector3(0, 0, 1);
        acceleration = new Vector3(0, 0, 0);
        step = new Vector3();
    }

    Vector3 tmpVec = new Vector3();

    public void update( float deltaTime, SubController subController ){

        screwSpeed = subController.power;
        screwAngle +=  4*screwSpeed*deltaTime;

        // todo get controls to work

        //velocity.z = screwSpeed / 100f;

        //velocity.rotate(Vector3.Y, subController.steerAngle );
        //acceleration.x =  subController.steerAngle;
        acceleration.x = 0.02f * subController.steerAngle;
        acceleration.y = 0.02f * subController.diveAngle;

        step.set(acceleration).scl(deltaTime);      // v = a . dt
        velocity.add(step);
        step.set(velocity).scl(deltaTime);          // x = v * dt
        position.add(step);


        sceneSub.modelInstance.transform.setToRotation(Vector3.Y, subController.steerAngle);
        sceneSub.modelInstance.transform.rotate(Vector3.X, -2*subController.diveAngle);
        sceneSub.modelInstance.transform.setTranslation(position);

        sceneScrew.modelInstance.transform.setToRotation(Vector3.Z, screwAngle);
        sceneScrew.modelInstance.transform.mulLeft(sceneSub.modelInstance.transform);

        sceneFins.modelInstance.transform.setToRotation(Vector3.X, 4*subController.diveAngle);
        sceneFins.modelInstance.transform.mulLeft(sceneSub.modelInstance.transform);

    }

    public Vector3 getPosition() {
        sceneSub.modelInstance.transform.getTranslation(tmpVec);
        return tmpVec;
    }
}
