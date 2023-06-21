package com.monstrous.underthesea.screens;

import com.badlogic.gdx.*;
import com.badlogic.gdx.graphics.*;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g3d.Environment;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.graphics.g3d.environment.DirectionalLight;
import com.badlogic.gdx.graphics.g3d.environment.DirectionalShadowLight;
import com.badlogic.gdx.graphics.g3d.utils.CameraInputController;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.graphics.g3d.utils.DepthShaderProvider;
import com.badlogic.gdx.graphics.glutils.FrameBuffer;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.GdxRuntimeException;
import com.badlogic.gdx.utils.ScreenUtils;
import com.monstrous.underthesea.*;
import com.monstrous.underthesea.gui.GUI;
import net.mgsx.gltf.loaders.gltf.GLTFLoader;
import net.mgsx.gltf.scene3d.attributes.PBRCubemapAttribute;
import net.mgsx.gltf.scene3d.attributes.PBRFloatAttribute;
import net.mgsx.gltf.scene3d.attributes.PBRTextureAttribute;
import net.mgsx.gltf.scene3d.lights.DirectionalLightEx;
import net.mgsx.gltf.scene3d.scene.Scene;
import net.mgsx.gltf.scene3d.scene.SceneAsset;
import net.mgsx.gltf.scene3d.scene.SceneManager;
import net.mgsx.gltf.scene3d.scene.SceneSkybox;
import net.mgsx.gltf.scene3d.utils.IBLBuilder;

public class GameScreen extends ScreenAdapter {

    private static final int SHADOW_MAP_SIZE = 2048;
    public static int shadowMapSize = 1024;         // resolution of shadow
    public static int shadowViewPortSize = 1024;    // area where shadows are cast


    private Main game;
    private SceneManager sceneManager;
    private SceneAsset sceneAsset;
    private Scene scene;
    private PerspectiveCamera cam;
    private CamController camController;
    private ModelBatch modelBatch;
    private ModelBatch shadowBatch;
    private Environment environment;
    private DirectionalShadowLight shadowLight;
    private World world;
    private GUI gui;
    private Cubemap diffuseCubemap;
    private Cubemap environmentCubemap;
    private Cubemap specularCubemap;
    private Texture brdfLUT;
    private SceneSkybox skybox;
    private DirectionalLightEx light;
    private SpriteBatch batch;
    private ShaderProgram vignetteProgram;
    private FrameBuffer fbo = null;

    public GameScreen(Main game) {
        Gdx.app.log("GameScreen constructor", "");
        this.game = game;
    }

    @Override
    public void show() {
        // load scene asset
        sceneManager = new SceneManager();

        SubController subController = new SubController();

        // create perspective camera
        cam = new PerspectiveCamera(60, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        cam.position.set(0, 20, 20);
        cam.lookAt(0, 0, 0);
        cam.far = Chunks.SIZE * Chunk.CHUNK_WIDTH * 2;          // TMP
        cam.near = 1f;
        cam.update();
        sceneManager.setCamera(cam);

        world = new World(game.assets, sceneManager, subController, cam);
        gui = new GUI(world);




        //inputController = new PlayerController(world.getPlayer().transform);

        // add camera controller
        camController = new CamController(cam);
        // free up the WASD keys
//        camController.forwardKey = Input.Keys.F3;
//        camController.backwardKey = Input.Keys.F4;
//        camController.rotateRightKey = Input.Keys.F5;
//        camController.rotateLeftKey = Input.Keys.F6;



        // input multiplexer
        InputMultiplexer im = new InputMultiplexer();
        Gdx.input.setInputProcessor(im);
        im.addProcessor(gui.stage);
        im.addProcessor(subController);
        im.addProcessor(camController);

        environment = new Environment();

        // define some lighting
        Vector3 lightVector = new Vector3(-.2f, -.8f, -.4f).nor();

        float al = Settings.ambientLightLevel;
        environment.set(new ColorAttribute(ColorAttribute.AmbientLight, al, al, al, 1f));
        float dl = Settings.directionalLightLevel;
        environment.add(new DirectionalLight().set(new Color(dl, dl, dl, 1), lightVector));
        environment.set(new ColorAttribute(ColorAttribute.Fog, Settings.backgroundColour));			// fog
        if (Settings.shadows) {
            // note that the shadowing system adds light to the scene (except for the shadows)
            float sl = Settings.shadowLightLevel;
            environment.add((shadowLight = new DirectionalShadowLight(shadowMapSize, shadowMapSize,
                shadowViewPortSize, shadowViewPortSize,
                1f, 500f))
                .set(new Color(sl, sl, sl, 1), lightVector));
            environment.shadowMap = shadowLight;
        }


        modelBatch = new ModelBatch();
        shadowBatch = new ModelBatch(new DepthShaderProvider());

        sceneManager.environment.set(new PBRFloatAttribute(PBRFloatAttribute.ShadowBias, 0.001f));

        // setup light
        light = new net.mgsx.gltf.scene3d.lights.DirectionalShadowLight(SHADOW_MAP_SIZE, SHADOW_MAP_SIZE).setViewport(100,100,5,400);

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

        sceneManager.setAmbientLight(0.1f);
        sceneManager.environment.set(new PBRTextureAttribute(PBRTextureAttribute.BRDFLUTTexture, brdfLUT));
        sceneManager.environment.set(PBRCubemapAttribute.createSpecularEnv(specularCubemap));
        sceneManager.environment.set(PBRCubemapAttribute.createDiffuseEnv(diffuseCubemap));

        // setup skybox
        skybox = new SceneSkybox(environmentCubemap);
        sceneManager.setSkyBox(skybox);

        batch = new SpriteBatch();

        // full screen post processing shader
        //ShaderProgram.pedantic = true;
        vignetteProgram = new ShaderProgram(
            Gdx.files.internal("shaders\\vignette.vertex.glsl"),
            Gdx.files.internal("shaders\\vignette.fragment.glsl"));
        if (!vignetteProgram.isCompiled())
            throw new GdxRuntimeException(vignetteProgram.getLog());
        ShaderProgram.pedantic = false;

    }

    @Override
    public void render(float delta) {
        if(Gdx.input.isKeyPressed(Input.Keys.F1)) {
            MarchingCubes.wireframeMode = false;
            world.rebuild();
        }
        if(Gdx.input.isKeyPressed(Input.Keys.F2)) {
            MarchingCubes.wireframeMode = true;
            world.rebuild();
        }

        camController.update( world.getFocus() );
        world.update(delta);

        //create shadow texture
//        if(Settings.shadows) {
//            shadowLight.begin(cam.position, cam.direction);
//            shadowBatch.begin(shadowLight.getCamera());
//            world.render(shadowBatch, environment);
//            shadowBatch.end();
//            shadowLight.end();
//        }


        sceneManager.update(delta);
        sceneManager.renderShadows();
        sceneManager.renderMirror();
        sceneManager.renderTransmission();

        fbo.begin();
        ScreenUtils.clear(Settings.backgroundColour, true);

        sceneManager.renderColors();


        // mixing scene manager and instance rendering.....
        modelBatch.begin(cam);
        world.render(modelBatch, environment);
        modelBatch.end();

        fbo.end();


        // post-processing of game screen content : vignette effect
        Sprite s = new Sprite(fbo.getColorBufferTexture());
        s.flip(false,  true); // coordinate system in buffer differs from screen

        batch.begin();
        batch.setShader(vignetteProgram);						// post-processing shader
        batch.draw(s,  0,  0); 	// draw frame buffer as screen filling texture
        batch.end();
        batch.setShader(null);

        gui.render(delta);
    }

    @Override
    public void resize(int width, int height) {
        // Resize your screen here. The parameters represent the new window size.
        cam.viewportWidth = width;
        cam.viewportHeight = height;
        cam.update();
        batch.getProjectionMatrix().setToOrtho2D(0,0, width, height);   // resize the sprite batch

        sceneManager.updateViewport(width, height);
        gui.resize(width, height);
        if(fbo != null)
            fbo.dispose();
        fbo = new FrameBuffer(Pixmap.Format.RGBA8888, width, height, true);

    }

    @Override
    public void pause() {
        // Invoked when your application is paused.
    }

    @Override
    public void resume() {
        // Invoked when your application is resumed after pause.
    }

    @Override
    public void hide() {
        // This method is called when another screen replaces this one.
    }

    @Override
    public void dispose() {
        sceneManager.dispose();
        sceneAsset.dispose();
        environmentCubemap.dispose();
        diffuseCubemap.dispose();
        specularCubemap.dispose();
        brdfLUT.dispose();
        skybox.dispose();

        world.dispose();
        modelBatch.dispose();
        gui.dispose();
    }
}
