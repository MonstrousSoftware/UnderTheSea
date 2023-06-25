package com.monstrous.underthesea;

import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.g3d.*;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Disposable;
import com.monstrous.underthesea.entities.BananaMan;
import com.monstrous.underthesea.entities.Canister;
import com.monstrous.underthesea.entities.Submarine;
import com.monstrous.underthesea.gui.GUI;
import com.monstrous.underthesea.terrain.Chunks;
import net.mgsx.gltf.scene3d.scene.SceneManager;

public class World implements Disposable {

    private Chunks chunks;
    private SceneManager sceneManager;
    public Submarine submarine;
    public Canister canister;
    private BananaMan bananaMan;
    public SubController subController;
    public ParticleEffects particleEffects;
    public float capsuleDistance;
    public int capsuleCount;
    private GUI gui;
    public float timer;
    private int radioMessagesShown = 0;
    public boolean bananaManTaken = false;
    public boolean gameComplete = false;
    private Sounds sounds;
    private ModelInstance waterInstance;
    private float [][] capsulePositions = { { -22, 73, 50 }  ,{ 37, 57, 67 }, { 69, 69, 64 }, { 36, 51, -48 } };
    public int rockProximity;


    public World( Assets assets, SceneManager sceneManager,  SubController subController, Camera cam ) {
        this.sceneManager = sceneManager;
        this.subController = subController;

        sounds = new Sounds(assets);

        rebuild();
        submarine = new Submarine(assets, sceneManager, 0,75,-30);
        //submarine = new Submarine(assets, sceneManager, 36,51,-43);
        capsuleCount = 0;
        canister = new Canister(assets, sceneManager, capsulePositions[capsuleCount][0],capsulePositions[capsuleCount][1],capsulePositions[capsuleCount][2]);
        timer = 10f;

        bananaMan = new BananaMan(assets, sceneManager, 58, 53, -22);

        if(Settings.enableParticleEffects) {
            particleEffects = new ParticleEffects(cam);
            particleEffects.addBubbles(submarine.getScrewTransform());
        }
        Sounds.playSoundLoop(Sounds.SONAR_PING);


    }

    // need this hack so that World can send radio messages to GUI
    public void setGUI( GUI gui ){
        this.gui = gui;
    }

    public Vector3 getFocus() {
        return submarine.getPosition();
    }

    public void rebuild() {
        if(chunks != null)
            chunks.dispose();
        chunks = new Chunks(sceneManager);

    }

    public void update( float deltaTime ){
        timer -= Math.min(deltaTime, 0.1f);

//        if(Gdx.input.isKeyPressed(Input.Keys.I)) {
//            bananaMan.setPosition(bananaMan.position.x, bananaMan.position.y, bananaMan.position.z+1);
//        }
//        if(Gdx.input.isKeyPressed(Input.Keys.M)) {
//            bananaMan.setPosition(bananaMan.position.x, bananaMan.position.y, bananaMan.position.z-1);
//        }
//        if(Gdx.input.isKeyPressed(Input.Keys.J)) {
//            bananaMan.setPosition(bananaMan.position.x-1, bananaMan.position.y, bananaMan.position.z);
//        }
//        if(Gdx.input.isKeyPressed(Input.Keys.K)) {
//            bananaMan.setPosition(bananaMan.position.x+1, bananaMan.position.y, bananaMan.position.z);
//        }
//        Gdx.app.error("banana", "at "+bananaMan.position.toString());


        subController.update(deltaTime);

        submarine.update(deltaTime, subController);

        //rockProximity = chunks.distanceToRock(submarine.getForwardPosition());

        // very basic N point collision
        if(!Settings.collisionCheat) {
            //if (rockProximity <= Submarine.RADIUS) {
            if (chunks.collides(submarine.getTipPosition())) {
                if(submarine.inCollision()) // ?
                    Sounds.playSound(Sounds.CRASH);
                submarine.collide();
            }
            if (chunks.collides(submarine.getTailPosition())) {
            //if(chunks.distanceToRock(submarine.getAftPosition()) <= Submarine.RADIUS) {
                if(submarine.inCollision())
                    Sounds.playSound(Sounds.CRASH);
                submarine.rearCollide();
           }
        }

        if(gameComplete) {
            capsuleDistance = 0;    // for the GUI
        }
        else {
            capsuleDistance = canister.getDistance(submarine.position);
            if (capsuleDistance < Canister.PICKUP_DISTANCE) {                      // close enough to pick up?
                gui.setMessage(Settings.capsuleMessages[capsuleCount]);        // show the message from the canister
                if (capsuleCount < Settings.numberOfCapsules - 1) {
                    capsuleCount++;
                    positionCapsule();

                } else {
                    // you win!
                    if (!gameComplete) {
                        Sounds.playSound(Sounds.FANFARE);
                        Sounds.playSound(Sounds.CHEER);
                    }
                    gameComplete = true;
                    canister.setPosition(0, -999, 0);     // hide canister
                }
            }
        }

        if(!bananaManTaken) {
            if (bananaMan.getDistance(submarine.position) < BananaMan.PICKUP_DISTANCE) {
                bananaMan.remove();
                bananaManTaken = true;
                Sounds.playSound(Sounds.BONUS);
            }
        }



        if(timer < 0 ){
            gui.setMessage(Settings.radioMessages[radioMessagesShown] );
            radioMessagesShown++;
            Sounds.playSound(Sounds.MORSE);
            timer = 9999999f;
        }

        if(Settings.enableParticleEffects) {
            particleEffects.setBubblesOrigin(submarine.getTailPosition());
            particleEffects.update(deltaTime);
        }
    }

    public void positionCapsule() {
        canister.setPosition(capsulePositions[capsuleCount][0],capsulePositions[capsuleCount][1],capsulePositions[capsuleCount][2]);
        if(capsuleCount == 1)
            timer = 20; // start timer after first capsule is picked up
    }


    private Vector3 tmpVec = new Vector3();

    public void render(ModelBatch modelBatch, Environment environment){

        chunks.render(modelBatch, environment);

        if(Settings.enableParticleEffects)
            particleEffects.render(modelBatch);
    }

    @Override
    public void dispose() {
        Sounds.stopSound(Sounds.SONAR_PING);

        chunks.dispose();
        if(particleEffects != null)
            particleEffects.dispose();
        sounds.dispose();
    }
}
