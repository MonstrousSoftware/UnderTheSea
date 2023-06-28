package com.monstrous.underthesea.entities;


import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g3d.environment.PointLight;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector3;
import com.monstrous.underthesea.Assets;
import com.monstrous.underthesea.Sounds;
import com.monstrous.underthesea.SubController;
import net.mgsx.gltf.scene3d.scene.Scene;
import net.mgsx.gltf.scene3d.scene.SceneAsset;
import net.mgsx.gltf.scene3d.scene.SceneManager;
import org.ode4j.math.DQuaternion;
import org.ode4j.ode.DBody;

public class Submarine {

    private SceneAsset sceneAsset;
    public Scene sceneSub;
    private Scene sceneScrew;
    private Scene sceneFins;
    private Scene sceneRudder;
    public  Vector3 position;
    private Vector3 tip;
    private Vector3 tail;
    private float screwSpeed;     // -100 to 100
    private float screwAngle;
    private float heading;
    private float diveAngle;
    private boolean collided = false;
    private PointLight light;           // should be a spotlight but these are not well-supported


    public Submarine(Assets assets, SceneManager sceneManager, float x, float y, float z ) {

        sceneAsset = assets.get("models/submarine.gltf");

        sceneSub = new Scene(sceneAsset.scene, "submarine");
        sceneSub.modelInstance.transform.translate(x, y, z);
        sceneManager.addScene(sceneSub);
        sceneScrew = new Scene(sceneAsset.scene, "screw");
        sceneScrew.modelInstance.transform.translate(x, y, z);
        sceneManager.addScene(sceneScrew);
        sceneFins = new Scene(sceneAsset.scene, "fins");
        sceneFins.modelInstance.transform.translate(x, y, z);
        sceneManager.addScene(sceneFins);
        sceneRudder = new Scene(sceneAsset.scene, "rudder");
        sceneRudder.modelInstance.transform.translate(x, y, z);
        sceneManager.addScene(sceneRudder);

        position = new Vector3(x, y, z);

        tip = new Vector3();
        tail = new Vector3();

        heading = 0;
        diveAngle = 0;


        light = new PointLight();

        light.set(Color.WHITE, getTipPosition(), 300f);
        sceneManager.environment.add(light);
    }


    public boolean inCollision() {
        return collided;
    }

    public void update(DBody subBody, float deltaTime, SubController subController ){

        screwSpeed = subController.power;
        screwAngle +=  4*screwSpeed*deltaTime;


        // sub reacts with some lag on the inputs to give some inertia
        heading += -subController.steerAngle * deltaTime;
        diveAngle = MathUtils.lerp(diveAngle, subController.diveAngle, deltaTime);

        float FORCE = 0.1f;
        subBody.addRelForce(0,0, FORCE*subController.power);
        subBody.setLinearDamping(0.9f);

        DQuaternion qc = DQuaternion.fromEuler(Math.toRadians(-diveAngle),  Math.toRadians(heading), 0 );  // roll, pitch, yaw
        subBody.setQuaternion(qc);

        sceneScrew.modelInstance.transform.setToRotation(Vector3.Z, screwAngle);
        sceneScrew.modelInstance.transform.mulLeft(sceneSub.modelInstance.transform);

        sceneFins.modelInstance.transform.idt().translate(0,0,-1f);
        sceneFins.modelInstance.transform.rotate(Vector3.X, diveAngle);
        sceneFins.modelInstance.transform.translate(0,0,1f);
        sceneFins.modelInstance.transform.mulLeft(sceneSub.modelInstance.transform);

        sceneRudder.modelInstance.transform.idt().translate(0,0,-1.6f);
        sceneRudder.modelInstance.transform.rotate(Vector3.Y, subController.steerAngle);
        sceneRudder.modelInstance.transform.translate(0,0,1.6f);
        sceneRudder.modelInstance.transform.mulLeft(sceneSub.modelInstance.transform);

        light.setPosition(getLightPosition());
    }

    // without ODE physics
//    public void updateOriginal( float deltaTime, SubController subController ){
//
//
//        if(collided && subController.power < 0)
//            collided = false;
//
//        if(rearCollided && subController.power > 0)
//            rearCollided = false;
//
//        screwSpeed = subController.power;
//        screwAngle +=  4*screwSpeed*deltaTime;
//
//
//        if(!inCollision()) {
//            // sub reacts with some lag on the inputs to give some inertia
//            heading += -subController.steerAngle * deltaTime;
//            diveAngle = MathUtils.lerp(diveAngle, subController.diveAngle, deltaTime);
//
//            targetVelocity.set(0,0,screwSpeed/50f);
//            targetVelocity.rotate(Vector3.X, -diveAngle);
//            targetVelocity.rotate(Vector3.Y, heading);
//
//            // actual movement velocity lags on target velocity
//            if(velocity.dot(targetVelocity) < 0)
//                velocity.lerp(targetVelocity, deltaTime);
//            else
//                velocity.slerp(targetVelocity, deltaTime);
//
//            step.set(velocity).scl(deltaTime);          // x = v * dt
//            position.add(step);
//        }
//
//
//        sceneSub.modelInstance.transform.setToRotation(Vector3.Y, heading);
//        sceneSub.modelInstance.transform.rotate(Vector3.X, -2*diveAngle);
//        sceneSub.modelInstance.transform.setTranslation(position);
//
//        sceneScrew.modelInstance.transform.setToRotation(Vector3.Z, screwAngle);
//        sceneScrew.modelInstance.transform.mulLeft(sceneSub.modelInstance.transform);
//
//        sceneFins.modelInstance.transform.idt().translate(0,0,-1f);
//        sceneFins.modelInstance.transform.rotate(Vector3.X, 4*subController.diveAngle);
//        sceneFins.modelInstance.transform.translate(0,0,1f);
//        sceneFins.modelInstance.transform.mulLeft(sceneSub.modelInstance.transform);
//
//        sceneRudder.modelInstance.transform.idt().translate(0,0,-1.6f);
//        sceneRudder.modelInstance.transform.rotate(Vector3.Y, subController.steerAngle);
//        sceneRudder.modelInstance.transform.translate(0,0,1.6f);
//        sceneRudder.modelInstance.transform.mulLeft(sceneSub.modelInstance.transform);
//
//        light.setPosition(getLightPosition());
//    }

    public void collide() {
        if(!collided) {
            Sounds.playSound(Sounds.CRASH);
            collided = true;
        }
    }

    public void uncollide() {
        collided = false;
    }


    public Vector3 getPosition() {
        position.set(0,0,0);
        position.mul(sceneSub.modelInstance.transform);
        return position;
    }

    public Vector3 getTipPosition() {
        tip.set(0,0, 2.5f);     // front tip of the model, used for collision test
        tip.mul(sceneSub.modelInstance.transform);
        return tip;
    }


    private Vector3 lightPos = new Vector3();

    private Vector3 getLightPosition() {
        lightPos.set(0,0, 4f);     // position just in front of the sub
        lightPos.mul(sceneSub.modelInstance.transform);
        return lightPos;
    }

    public Vector3 getTailPosition() {
        tail.set(0,0, -2.5f);     // tail of the model, used for bubbles particles
        tail.mul(sceneSub.modelInstance.transform);
        return tail;
    }

    public Matrix4 getScrewTransform() {
        return sceneScrew.modelInstance.transform;
    }
}
