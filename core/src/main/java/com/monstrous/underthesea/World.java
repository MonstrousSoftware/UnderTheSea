package com.monstrous.underthesea;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.VertexAttributes;
import com.badlogic.gdx.graphics.g3d.*;
import com.badlogic.gdx.graphics.g3d.utils.ModelBuilder;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Disposable;
import com.monstrous.underthesea.gui.GUI;
import net.mgsx.gltf.scene3d.scene.SceneManager;

public class World implements Disposable {

    private Chunks chunks;
    private NoiseSettings noiseSettings;
    private Model modelXYZ;
    private ModelInstance instanceXYZ;
    private SceneManager sceneManager;
    public Submarine submarine;
    public Capsule capsule;
    public SubController subController;
    public ParticleEffects particleEffects;
    public float capsuleDistance;
    public int capsuleCount;
    private GUI gui;
    private float time = 0;
    private int messagesShown = 0;

    public World( Assets assets, SceneManager sceneManager,  SubController subController, Camera cam ) {
        this.sceneManager = sceneManager;
        this.subController = subController;

        ModelBuilder modelBuilder = new ModelBuilder();
        modelXYZ = modelBuilder.createXYZCoordinates(10f, new Material(), VertexAttributes.Usage.Position | VertexAttributes.Usage.ColorUnpacked);
        instanceXYZ = new ModelInstance(modelXYZ, new Vector3(0, 0, 0));

        rebuild();
        submarine = new Submarine(assets, sceneManager, 0, 70, 0);
        capsule = new Capsule(assets, sceneManager, 0,70,20);
        capsuleCount = 1;

        if(Settings.enableParticleEffects) {
            particleEffects = new ParticleEffects(cam);
            particleEffects.addBubbles(submarine.getScrewTransform());
        }
    }

    public void setGUI( GUI gui ){
        this.gui = gui;
        //gui.setMessage( "RADIO BROADCAST: SUBCOM TO GX-25. PROCEED TO PICK UP CAPSULE IN YOUR IMMEDIATE VICINITY."); //Settings.messages[0] );
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
        time += deltaTime;

        subController.update(deltaTime);

        submarine.update(deltaTime, subController);

        // very basic N point collision
        if(chunks.collides(submarine.getTipPosition())) {
            //Gdx.app.log("COLLISION", "OUCH");
            submarine.collide();
        }
        if(chunks.collides(submarine.getTailPosition())) {
            //Gdx.app.log("COLLISION", "OUCH");
            submarine.rearCollide();
        }

        capsuleDistance = capsule.getDistance(submarine.position);
        if(capsuleDistance < Capsule.PICKUP_DISTANCE){
            gui.setMessage(Settings.messages[capsuleCount] );
            if(capsuleCount < Settings.capsuleNumber) {
                dropNewCapsule();
                capsuleCount++;
            }
        }

        if(time > 5 && messagesShown== 0){
            gui.setMessage(Settings.messages[0] );
            messagesShown++;
        }

        if(Settings.enableParticleEffects) {
            particleEffects.setBubblesOrigin(submarine.getTailPosition());
            particleEffects.update(deltaTime);
        }
    }

    public void dropNewCapsule() {
        float y = capsule.getPosition().y;
        capsule.setPosition(0, y+5, 15);
    }


    private Vector3 tmpVec = new Vector3();

    public void render(ModelBatch modelBatch, Environment environment){

        chunks.render(modelBatch, environment);

        modelBatch.render(instanceXYZ, environment);

        if(Settings.enableParticleEffects)
            particleEffects.render(modelBatch);
    }

    @Override
    public void dispose() {
        chunks.dispose();
        modelXYZ.dispose();
        if(particleEffects != null)
        particleEffects.dispose();
    }
}
