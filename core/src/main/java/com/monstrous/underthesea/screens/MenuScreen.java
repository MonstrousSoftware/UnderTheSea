package com.monstrous.underthesea.screens;

import com.badlogic.gdx.Application;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Cubemap;
import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.monstrous.underthesea.Sounds;
import net.mgsx.gltf.scene3d.attributes.PBRCubemapAttribute;
import net.mgsx.gltf.scene3d.attributes.PBRTextureAttribute;
import net.mgsx.gltf.scene3d.lights.DirectionalLightEx;
import net.mgsx.gltf.scene3d.scene.Scene;
import net.mgsx.gltf.scene3d.scene.SceneAsset;
import net.mgsx.gltf.scene3d.scene.SceneManager;
import net.mgsx.gltf.scene3d.utils.IBLBuilder;


// screen to showcase the hero model

public class MenuScreen extends StdScreenAdapter {

    private final Main game;
    private SceneManager sceneManager;
    private PerspectiveCamera camera;
    private Cubemap diffuseCubemap;
    private Cubemap environmentCubemap;
    private Cubemap specularCubemap;
    private Texture brdfLUT;
    private DirectionalLightEx light;
    private Stage stage;
    private Skin skin;
    private float angle = 0;
    private boolean banana;
    private Vector3 cameraOffset;
    private Sound click;
    //private boolean isWindowed = true;

    public MenuScreen(Main game, boolean banana) {

        this.game = game;
        this.banana = banana;
        click = game.assets.get("sounds/click-for-game-menu.mp3");
    }

    @Override
    public void show() {

        sceneManager = new SceneManager();

        // create scene
        if(banana) {
            SceneAsset sceneAsset = game.assets.get("models/AnthroBanana.gltf");

            Scene sceneSub = new Scene(sceneAsset.scene, "banana");
            sceneManager.addScene(sceneSub);
            cameraOffset = new Vector3(1.2f, 3.3f, 12.2f);
        }
        else {
            SceneAsset sceneAsset = game.assets.get("models/submarine.gltf");

            Scene sceneSub = new Scene(sceneAsset.scene, "submarine");
            sceneManager.addScene(sceneSub);
            Scene sceneScrew = new Scene(sceneAsset.scene, "screw");
            sceneManager.addScene(sceneScrew);
            Scene sceneFins = new Scene(sceneAsset.scene, "fins");
            sceneManager.addScene(sceneFins);
            cameraOffset = new Vector3(1.2f, 3.3f, 8.2f);
        }

//        scene = new Scene(sceneAsset.scene, "backdrop");
//        sceneManager.addScene(scene);

      // setup camera
        camera = new PerspectiveCamera(60f, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        camera.position.set(1.2f, 3.3f, 8.2f);
        camera.lookAt(0,1.0f,0);
        camera.near = .1f;
        camera.far = 16f;
        camera.update();
        sceneManager.setCamera(camera);


        // setup light
        light = new DirectionalLightEx();
        light.direction.set(1, -3, 1).nor();
        light.color.set(Color.WHITE);
        sceneManager.environment.add(light);

        // setup quick IBL (image based lighting)
        IBLBuilder iblBuilder = IBLBuilder.createOutdoor(light);
        environmentCubemap = iblBuilder.buildEnvMap(1024);
        diffuseCubemap = iblBuilder.buildIrradianceMap(256);
        specularCubemap = iblBuilder.buildRadianceMap(10);
        iblBuilder.dispose();

        // This texture is provided by the library, no need to have it in your assets.
        brdfLUT = new Texture(Gdx.files.classpath("net/mgsx/gltf/shaders/brdfLUT.png"));

        sceneManager.setAmbientLight(1f);
        sceneManager.environment.set(new PBRTextureAttribute(PBRTextureAttribute.BRDFLUTTexture, brdfLUT));
        sceneManager.environment.set(PBRCubemapAttribute.createSpecularEnv(specularCubemap));
        sceneManager.environment.set(PBRCubemapAttribute.createDiffuseEnv(diffuseCubemap));

        skin = game.assets.get("blue-pixel-skin/blue-pixel.json");
        stage = new Stage(new ScreenViewport());
        rebuild();
        Gdx.input.setInputProcessor(stage);

    }


    private void rebuild() {
        stage.clear();
        Label label = new Label(game.VERSION, skin, "small");
        label.setColor(Color.BLACK);

        TextButton startButton = new TextButton("START", skin, "big" );
        TextButton instructionsButton = new TextButton("INSTRUCTIONS", skin, "big" );
        TextButton creditsButton = new TextButton("CREDITS", skin, "big" );
        TextButton exitButton = new TextButton("EXIT", skin, "big" );


        float ht = 50f;
        float pd = 20f;
        Table menuTable = new Table();
        menuTable.add(startButton).height(ht).bottom().center().pad(pd).row();
        menuTable.add(instructionsButton).height(ht).bottom().center().pad(pd).row();
        menuTable.add(creditsButton).height(ht).bottom().center().pad(pd).row();
        if(Gdx.app.getType() != Application.ApplicationType.WebGL) {
            menuTable.add(exitButton).height(ht).bottom().center().pad(pd).row();
        }
        menuTable.pack();

        // root table that fills the whole screen
        Table screenTable = new Table();
        screenTable.setFillParent(true);        // size to match stage size
        //screenTable.add(label).colspan(2).top().left().expandX().row();
        screenTable.add(menuTable).center().pad(pd);
        screenTable.add().expandX().row();
        screenTable.pack();

        stage.addActor(screenTable);

        startButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                super.clicked(event, x, y);
                click.play();
                game.setScreen(new PreGameScreen( game ));
            }
        });


        instructionsButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                super.clicked(event, x, y);
                click.play();
                game.setScreen(new InstructionsScreen( game ));
            }
        });

        creditsButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                super.clicked(event, x, y);
                click.play();
                game.setScreen(new CreditsScreen( game ));
            }
        });

        exitButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                super.clicked(event, x, y);
                click.play();
                Gdx.app.exit();
            }
        });
    }

    @Override
    public void resize(int width, int height) {
        sceneManager.updateViewport(width, height);
        stage.getViewport().update(width, height, true);
    }

    private Vector3 camDist = new Vector3();

    @Override
    public void render( float deltaTime ) {
        super.render(deltaTime);

        // on key press go to game screen
        if(Gdx.input.isKeyPressed(Input.Keys.SPACE)) {
            banana = true;
            game.setScreen(new GameScreen(game));
            return;
        }
        // rotate model

        angle += 20f*deltaTime;
        camDist.set(cameraOffset);
        camDist.rotate(Vector3.Y, angle);

        camera.position.set(camDist);
        camera.lookAt(0,0,0);
        camera.up.set(Vector3.Y);
        camera.update();
        //scene.modelInstance.transform.rotate(Vector3.Y, 20f*deltaTime);

        // render
        //Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);
        ScreenUtils.clear(45f/255f, 98f/255f, 200f/255f, 1, true);
        sceneManager.update(deltaTime);
        sceneManager.render();

        stage.act(Gdx.graphics.getDeltaTime());
        stage.draw();
    }

    @Override
    public void dispose() {
        sceneManager.dispose();
        //sceneAsset.dispose();
        environmentCubemap.dispose();
        diffuseCubemap.dispose();
        specularCubemap.dispose();
        brdfLUT.dispose();
        stage.dispose();
    }

}
