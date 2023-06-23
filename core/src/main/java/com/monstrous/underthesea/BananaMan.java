package com.monstrous.underthesea;

import net.mgsx.gltf.scene3d.scene.Scene;
import net.mgsx.gltf.scene3d.scene.SceneAsset;
import net.mgsx.gltf.scene3d.scene.SceneManager;

public class BananaMan {
    private SceneManager sceneManager;
    private SceneAsset sceneAsset;
    public Scene scene;


    public BananaMan(Assets assets, SceneManager sceneManager, float x, float y, float z ) {
        this.sceneManager = sceneManager;

        sceneAsset = assets.get("models/AnthroBanana.gltf");

        scene = new Scene(sceneAsset.scene, "banana");
        sceneManager.addScene(scene);
    }



    public void setPosition( float x, float y, float z) {
        scene.modelInstance.transform.setToTranslation(x, y, z);
    }
}
