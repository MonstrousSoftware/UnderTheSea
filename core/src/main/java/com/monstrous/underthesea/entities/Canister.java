package com.monstrous.underthesea.entities;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g3d.environment.PointLight;
import com.badlogic.gdx.math.Vector3;
import com.monstrous.underthesea.Assets;
import net.mgsx.gltf.scene3d.scene.Scene;
import net.mgsx.gltf.scene3d.scene.SceneAsset;
import net.mgsx.gltf.scene3d.scene.SceneManager;


// capsule that needs to be picked up by the submarine

public class Canister {

    public static float PICKUP_DISTANCE = 4f;

    private SceneManager sceneManager;
    private SceneAsset sceneAsset;
    public Scene sceneCapsule;
    public Vector3 position;
    private PointLight light;


    public Canister(Assets assets, SceneManager sceneManager, float x, float y, float z ) {
        this.sceneManager = sceneManager;
        sceneAsset = assets.get("models/submarine.gltf");
        position = new Vector3();

        sceneCapsule = new Scene(sceneAsset.scene, "capsule");

        light = new PointLight();
        light.set(Color.RED, x, y, z, 100f);
        sceneManager.environment.add(light);

        setPosition(x, y, z);
        sceneManager.addScene(sceneCapsule);
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
        light.setPosition(x, y, z);
    }
}
