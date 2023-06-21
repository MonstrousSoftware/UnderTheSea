package com.monstrous.underthesea;

import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Disposable;
import net.mgsx.gltf.scene3d.scene.Scene;
import net.mgsx.gltf.scene3d.scene.SceneAsset;
import net.mgsx.gltf.scene3d.scene.SceneManager;


// capsule that needs to be picked up by the submarine

public class Capsule {

    public static float PICKUP_DISTANCE = 3f;

    private SceneManager sceneManager;
    private SceneAsset sceneAsset;
    public Scene sceneCapsule;
    public Vector3 position;


    public Capsule(Assets assets, SceneManager sceneManager, float x, float y, float z ) {
        this.sceneManager = sceneManager;
        sceneAsset = assets.get("models/submarine.gltf");
        position = new Vector3();

        sceneCapsule = new Scene(sceneAsset.scene, "capsule");
        setPosition(x, y, z);
        sceneManager.addScene(sceneCapsule);
    }



    public boolean inReach( Vector3 subposition ){
        return subposition.dst(position) < PICKUP_DISTANCE;
    }

    public float getDistance( Vector3 subposition ){
        return subposition.dst(position);
    }


    public Vector3 getPosition() {
        return position;
    }


    public void setPosition( float x, float y, float z) {
        position.set(x, y, z);
        sceneCapsule.modelInstance.transform.setToTranslation(x, y, z);
    }
}
