package com.monstrous.underthesea;

import com.badlogic.gdx.math.Vector3;
import net.mgsx.gltf.scene3d.scene.Scene;
import net.mgsx.gltf.scene3d.scene.SceneAsset;
import net.mgsx.gltf.scene3d.scene.SceneManager;

public class BananaMan {

    public static float PICKUP_DISTANCE = 3f;

    private SceneManager sceneManager;
    private SceneAsset sceneAsset;
    public Scene scene;
    public Vector3 position;


    public BananaMan(Assets assets, SceneManager sceneManager, float x, float y, float z ) {
        this.sceneManager = sceneManager;

        sceneAsset = assets.get("models/AnthroBanana.gltf");

        scene = new Scene(sceneAsset.scene, "banana");

        scene.modelInstance.transform.translate(x, y, z);
        scene.modelInstance.transform.rotate(Vector3.Y, 180);
        position = new Vector3(x,y,z);

        sceneManager.addScene(scene);
    }

    public void remove() {
        sceneManager.removeScene(scene);
    }

    public void setPosition( float x, float y, float z) {
        position.set(x, y, z);
        scene.modelInstance.transform.setToTranslation(x, y, z);
    }

    public float getDistance( Vector3 subposition ){
        return subposition.dst(position);
    }

}
