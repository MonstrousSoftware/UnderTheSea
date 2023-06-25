package com.monstrous.underthesea;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.VertexAttributes;
import com.badlogic.gdx.graphics.g3d.*;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.graphics.g3d.utils.ModelBuilder;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;
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
    public float playTime;
    private int radioMessagesShown = 0;
    public boolean bananaManTaken = false;
    public boolean gameComplete = false;
    private Sounds sounds;
    //private ModelInstance waterInstance;
    private float [][] capsulePositions = { { -22, 73, 50 }  ,{ 37, 57, 67 }, { 69, 69, 64 }, { 36, 51, -48 } };
    public int rockProximity;
    private Array<DBody> bodies;
    private Array<DGeom> geoms;
    private DWorld dworld;
    private DSpace space;
    private DMass massInfo;
    private DJointGroup contactgroup;
    private ModelInstance boxInstance;
    private DBody subBody;

    public World(Main game, Assets assets, SceneManager sceneManager, SubController subController, Camera cam ) {
        geoms = new Array<>();

        OdeHelper.initODE2(0);
        dworld = OdeHelper.createWorld();
        space = OdeHelper.createSapSpace( null, DSapSpace.AXES.XZY );//change??
        massInfo = OdeHelper.createMass();
        contactgroup = OdeHelper.createJointGroup();


        dworld.setGravity (0,0,0);
        dworld.setCFM (1e-5);
        dworld.setERP (0.8);
        dworld.setQuickStepNumIterations (20);


        this.sceneManager = sceneManager;
        this.subController = subController;

        sounds = new Sounds(assets);

        chunks = game.chunks;
        if(chunks == null) {
            chunks = new Chunks();
            game.chunks = chunks;
        }
        chunks.addScenes(sceneManager);
        chunks.addGeoms(space);

        submarine = new Submarine(assets, sceneManager, 0,75,-30);
        subBody = OdeHelper.createBody(dworld);
        massInfo.setBox (1, 1, 1, 1);
        massInfo.adjust (1);    // mass
        subBody.setMass(massInfo);
        Vector3 pos = submarine.getPosition();
        subBody.setPosition(pos.x, pos.y, pos.z); //0, 75, -30);

        DCapsule subCapsule = OdeHelper.createCapsule(space, 1, 3);
        subCapsule.setBody(subBody);

//        float sz = 3;
//        DBody boxBody = OdeHelper.createBody(dworld);
//        boxBody.setPosition(pos.x, pos.y, pos.z+15);
//        DBox box = OdeHelper.createBox (space, sz, sz, sz);
//        //box.setPosition(pos.x, pos.y, pos.z+15);
//        box.setBody(boxBody);
//        //space.add(box);
//
//        ModelBuilder modelBuilder = new ModelBuilder();
//        Model modelBox = modelBuilder.createBox(sz, sz, sz,
//            new Material(ColorAttribute.createDiffuse(Color.BLUE)),
//            VertexAttributes.Usage.Position | VertexAttributes.Usage.Normal | VertexAttributes.Usage.TextureCoordinates );
//
//        boxInstance = new ModelInstance(modelBox, pos.x, pos.y, pos.z+15);




        capsuleCount = 0;
        canister = new Canister(assets, sceneManager, capsulePositions[capsuleCount][0],capsulePositions[capsuleCount][1],capsulePositions[capsuleCount][2]);
        timer = 10f;
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
        timer -= Math.min(deltaTime, 0.1f);
        if(!gameComplete)
            playTime += Math.min(deltaTime, 0.1f);

        updatePhysics();

        subController.update(deltaTime);

        submarine.update(deltaTime, subController);

        //rockProximity = chunks.distanceToRock(submarine.getForwardPosition());

        // very basic N point collision
        if(!Settings.collisionCheat) {
            //if (rockProximity <= Submarine.RADIUS) {
            if (chunks.collides(submarine.getTipPosition())) {
                if(!submarine.inCollision())
                    Sounds.playSound(Sounds.CRASH);
                submarine.collide();
            }
            if (chunks.collides(submarine.getTailPosition())) {
            //if(chunks.distanceToRock(submarine.getAftPosition()) <= Submarine.RADIUS) {
                if(!submarine.inCollision())
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
        //modelBatch.render(boxInstance);

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
        int i,n;

        DBody b1 = o1.getBody();
        DBody b2 = o2.getBody();
   //     Gdx.app.log("nearCallBack", "");

//        if(o1 != o2)
//            Gdx.app.log("collide diff", "");

//        if (b1!=null && b2!=null && OdeHelper.areConnected(b1, b2))
//            return;

        final int N = 4;
        DContactBuffer contacts = new DContactBuffer(N);
        n = OdeHelper.collide (o1,o2,N,contacts.getGeomBuffer());//[0].geom,sizeof(dContact));
        if (n > 0) {
            Gdx.app.log("collide", "");
            submarine.collide();
//            for (i=0; i<n; i++) {
//                DContact contact = contacts.get(i);
//                contact.surface.mode = dContactSlip1 | dContactSlip2 | dContactSoftERP | dContactSoftCFM | dContactApprox1;
//                if ( o1 instanceof DSphere || o2 instanceof DSphere )
//                    contact.surface.mu = 20;
//                else
//                    contact.surface.mu = 0.5;
//
//                contact.surface.slip1 = 0.0;
//                contact.surface.slip2 = 0.0;
//                contact.surface.soft_erp = 0.8;
//                contact.surface.soft_cfm = 0.01;
//                DJoint c = OdeHelper.createContactJoint(dworld,contactgroup,contact);
//                c.attach (o1.getBody(), o2.getBody());
//            }
        }
    }

}
