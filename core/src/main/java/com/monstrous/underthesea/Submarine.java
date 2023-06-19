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

    public Submarine( SceneManager sceneManager ) {
        sceneAsset = new GLTFLoader().load(Gdx.files.internal("models/submarine.gltf"));

        // extract some scenery items and add to scene manager
        sceneSub = new Scene(sceneAsset.scene, "submarine");
        sceneManager.addScene(sceneSub);
        sceneScrew = new Scene(sceneAsset.scene, "screw");
        sceneManager.addScene(sceneScrew);
        sceneFins = new Scene(sceneAsset.scene, "fins");
        sceneManager.addScene(sceneFins);
    }

    Vector3 tmpVec = new Vector3();

    public void update( float deltaTime, SubController subController ){
        screwSpeed = subController.power;

        sceneScrew.modelInstance.transform.rotate(Vector3.Z, 4*screwSpeed*deltaTime);

        sceneFins.modelInstance.transform.getTranslation(tmpVec);
        sceneFins.modelInstance.transform.setToRotation(Vector3.X, 4*subController.diveAngle);
        sceneFins.modelInstance.transform.setTranslation(tmpVec);
    }
}
