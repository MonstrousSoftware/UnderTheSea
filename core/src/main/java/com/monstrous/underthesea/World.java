package com.monstrous.underthesea;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.VertexAttributes;
import com.badlogic.gdx.graphics.g3d.*;
import com.badlogic.gdx.graphics.g3d.utils.ModelBuilder;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Disposable;
import net.mgsx.gltf.scene3d.scene.SceneManager;

public class World implements Disposable {

    private Chunks chunks;
    private NoiseSettings noiseSettings;
    private Model modelXYZ;
    private ModelInstance instanceXYZ;
    private SceneManager sceneManager;
    public Submarine submarine;
    public SubController subController;
    public ParticleEffects particleEffects;

    public World( Assets assets, SceneManager sceneManager,  SubController subController, Camera cam ) {
        this.sceneManager = sceneManager;
        this.subController = subController;

        ModelBuilder modelBuilder = new ModelBuilder();
        modelXYZ = modelBuilder.createXYZCoordinates(10f, new Material(), VertexAttributes.Usage.Position | VertexAttributes.Usage.ColorUnpacked);
        instanceXYZ = new ModelInstance(modelXYZ, new Vector3(0, 0, 0));

        rebuild();
        submarine = new Submarine(assets, sceneManager, 0, 70, 0);
        particleEffects = new ParticleEffects(cam);
        particleEffects.addBubbles( submarine.getScrewTransform() );

    }

    public Vector3 getFocus() {
        return submarine.getPosition();
    }

    public void rebuild() {
        if(chunks != null)
            chunks.dispose();
        chunks = new Chunks();
//        chunks.addScene(sceneManager);
    }

    public void update( float deltaTime ){
        subController.update(deltaTime);

        submarine.update(deltaTime, subController);

        // very basic N point collision
        if(chunks.collides(submarine.getTipPosition())) {
            Gdx.app.log("COLLISION", "OUCH");
            submarine.collide();
        }
        if(chunks.collides(submarine.getTailPosition())) {
            Gdx.app.log("COLLISION", "OUCH");
            submarine.rearCollide();
        }
        particleEffects.setBubblesOrigin( submarine.getTailPosition() );
        particleEffects.update(deltaTime);

    }

    private Vector3 tmpVec = new Vector3();

    public void render(ModelBatch modelBatch, Environment environment){

        chunks.render(modelBatch, environment);

        modelBatch.render(instanceXYZ, environment);

        particleEffects.render(modelBatch);
    }

    @Override
    public void dispose() {
        chunks.dispose();
        modelXYZ.dispose();
        particleEffects.dispose();
    }
}
