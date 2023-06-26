package com.monstrous.underthesea;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.g3d.*;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Disposable;
import com.monstrous.underthesea.entities.BananaMan;
import com.monstrous.underthesea.entities.Canister;
import com.monstrous.underthesea.entities.Submarine;
import com.monstrous.underthesea.gui.GUI;
import com.monstrous.underthesea.screens.Main;
import com.monstrous.underthesea.terrain.Chunks;
import net.mgsx.gltf.scene3d.scene.SceneManager;
import org.ode4j.ode.*;

import static org.ode4j.ode.OdeConstants.*;

public class World implements Disposable {

    public static int CAT_TERRAIN = 1;
    public static int CAT_SUBMARINE = 2;

    private Chunks chunks;
    private SceneManager sceneManager;
    public Submarine submarine;
    public Canister canister;
    private BananaMan bananaMan;
    public SubController subController;
    public ParticleEffects particleEffects;
    public float canisterDistance;
    public int canisterCount;
    private float [][] canisterPositions = { { -22, 73, 50 }  ,{ 37, 57, 67 }, { 69, 69, 64 }, { 36, 51, -48 } };
    private GUI gui;
    public float radioTimer;
    public float playTime;
    private int radioMessagesShown = 0;
    public boolean bananaManTaken = false;
    public boolean gameComplete = false;
    private Sounds sounds;
    private DWorld dworld;
    private DSpace space;
    private DMass massInfo;
    private DJointGroup contactgroup;
    private DBody subBody;

    public World(Main game, Assets assets, SceneManager sceneManager, SubController subController, Camera cam ) {

        OdeHelper.initODE2(0);
        dworld = OdeHelper.createWorld();
        space = OdeHelper.createSapSpace( null, DSapSpace.AXES.XYZ );//change?? todo
        massInfo = OdeHelper.createMass();
        contactgroup = OdeHelper.createJointGroup();


        dworld.setGravity (0,0,0);
        dworld.setCFM (1e-5);
        dworld.setERP (0.8);
        dworld.setQuickStepNumIterations (20);


        this.sceneManager = sceneManager;
        this.subController = subController;

        sounds = new Sounds(assets);

        chunks = game.chunks;           // created in PreGameScreen
        chunks.addScenes(sceneManager);
        chunks.addGeoms(dworld, space);

        //submarine = new Submarine(assets, sceneManager, 0,75,-30);
        submarine = new Submarine(assets, sceneManager, 10,75,10);  // todo
        subBody = OdeHelper.createBody(dworld);
        massInfo.setBox (1, 1, 1, 1);
        massInfo.adjust (1);    // mass
        subBody.setMass(massInfo);
        Vector3 pos = submarine.getPosition();
        subBody.setPosition(pos.x, pos.y, pos.z); //0, 75, -30);

        DCapsule subCapsule = OdeHelper.createCapsule(space, 1, 3);     // radius of caps, length without caps
        subCapsule.setBody(subBody);
        subCapsule.setCategoryBits(CAT_SUBMARINE);
        subCapsule.setCollideBits(CAT_TERRAIN);

        canisterCount = 0;
        canister = new Canister(assets, sceneManager, canisterPositions[canisterCount][0], canisterPositions[canisterCount][1], canisterPositions[canisterCount][2]);
        radioTimer = 10f;    // timer to first radio broadcast
        playTime = 0;

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

    public void rebuild() {}

    public void updatePhysics(){
        Vector3 pos = submarine.getPosition();
        subBody.setPosition(pos.x, pos.y, pos.z);
        space.collide (null,nearCallback);
        dworld.quickStep (0.05);
        contactgroup.empty ();

    }


    public void update( float deltaTime ){
        radioTimer -= Math.min(deltaTime, 0.1f);
        if(!gameComplete)
            playTime += Math.min(deltaTime, 0.1f);

        updatePhysics();

        subController.update(deltaTime);

        submarine.update(deltaTime, subController);

        if(gameComplete) {
            canisterDistance = 0;    // for the GUI
        }
        else {
            canisterDistance = canister.getDistance(submarine.position);
            if (canisterDistance < Canister.PICKUP_DISTANCE) {                      // close enough to pick up?
                gui.setMessage(Settings.capsuleMessages[canisterCount]);        // show the message from the canister
                if (canisterCount < Settings.numberOfCapsules - 1) {
                    canisterCount++;
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

        if(radioTimer < 0 ){
            gui.setMessage(Settings.radioMessages[radioMessagesShown] );
            radioMessagesShown++;
            Sounds.playSound(Sounds.MORSE);
            radioTimer = 9999999f;
        }

        if(Settings.enableParticleEffects) {
            particleEffects.setBubblesOrigin(submarine.getTailPosition());
            particleEffects.update(deltaTime);
        }
    }

    public void positionCapsule() {
        canister.setPosition(canisterPositions[canisterCount][0], canisterPositions[canisterCount][1], canisterPositions[canisterCount][2]);
        if(canisterCount == 1)
            radioTimer = 20; // start timer after first capsule is picked up
    }


    private Vector3 tmpVec = new Vector3();

    public void render(ModelBatch modelBatch, Environment environment){

        if(Settings.enableParticleEffects)
            particleEffects.render(modelBatch);
    }

    @Override
    public void dispose() {
        Sounds.stopSound(Sounds.SONAR_PING);

        //chunks.dispose(); to be disposed in Main
        if(particleEffects != null)
            particleEffects.dispose();
        sounds.dispose();

        contactgroup.destroy();
        space.destroy();
        dworld.destroy();
        OdeHelper.closeODE();
    }


    private DGeom.DNearCallback nearCallback = new DGeom.DNearCallback() {
        @Override
        public void call(Object data, DGeom o1, DGeom o2) {
            nearCallback(data, o1, o2);
        }
    };



    private void nearCallback (Object data, DGeom o1, DGeom o2) {
        final int N = 4;
        DContactBuffer contacts = new DContactBuffer(N);
        int n = OdeHelper.collide (o1,o2,N,contacts.getGeomBuffer());//[0].geom,sizeof(dContact));
        if (n > 0) {
            submarine.collide();
        }
    }

}
