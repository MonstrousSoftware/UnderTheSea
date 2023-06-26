package com.monstrous.underthesea.screens;

import com.badlogic.gdx.*;
import com.badlogic.gdx.graphics.*;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.graphics.glutils.FrameBuffer;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.utils.GdxRuntimeException;
import com.badlogic.gdx.utils.ScreenUtils;
import com.monstrous.underthesea.*;
import com.monstrous.underthesea.gui.GUI;
import com.monstrous.underthesea.terrain.Chunk;
import com.monstrous.underthesea.terrain.Chunks;
import net.mgsx.gltf.scene3d.attributes.PBRCubemapAttribute;
import net.mgsx.gltf.scene3d.attributes.PBRFloatAttribute;
import net.mgsx.gltf.scene3d.attributes.PBRTextureAttribute;
import net.mgsx.gltf.scene3d.lights.DirectionalLightEx;
import net.mgsx.gltf.scene3d.scene.SceneManager;
import net.mgsx.gltf.scene3d.utils.IBLBuilder;

public class GameScreen extends ScreenAdapter {

    private static final int SHADOW_MAP_SIZE = 2048;


    private Main game;
    private SceneManager sceneManager;
    private PerspectiveCamera cam;
    private CamController camController;
    private ModelBatch modelBatch;
    private World world;
    private GUI gui;
    private Cubemap diffuseCubemap;
    private Cubemap environmentCubemap;
    private Cubemap specularCubemap;
    private Texture brdfLUT;
    private DirectionalLightEx light;
    private SpriteBatch batch;
    private ShaderProgram shaderProgram;
    private FrameBuffer fbo = null;
    private int u_time;
    private float time;

    public GameScreen(Main game) {
        Gdx.app.log("GameScreen constructor", "");
        this.game = game;
    }

    @Override
    public void show() {
        sceneManager = new SceneManager();

        SubController subController = new SubController();

        // create perspective camera
        cam = new PerspectiveCamera(60, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        cam.position.set(0, 20, 20);
        cam.lookAt(0, 0, 0);
        cam.far = Chunks.SIZE * Chunk.CHUNK_WIDTH * 2;  // TMP
        cam.near = 1f;
        cam.update();
        sceneManager.setCamera(cam);

        world = new World(game, game.assets, sceneManager, subController, cam);
        gui = new GUI(game.assets, world);
        world.setGUI(gui);


        // add camera controller
        camController = new CamController(cam);

        // input multiplexer
        InputMultiplexer im = new InputMultiplexer();
        Gdx.input.setInputProcessor(im);
        im.addProcessor(gui.stage);
        im.addProcessor(subController);
        im.addProcessor(camController);


        modelBatch = new ModelBatch();


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

        sceneManager.setAmbientLight(Settings.ambientLightLevel);
        sceneManager.environment.set(new PBRTextureAttribute(PBRTextureAttribute.BRDFLUTTexture, brdfLUT));
        sceneManager.environment.set(PBRCubemapAttribute.createSpecularEnv(specularCubemap));
        sceneManager.environment.set(PBRCubemapAttribute.createDiffuseEnv(diffuseCubemap));
        sceneManager.environment.set(new ColorAttribute(ColorAttribute.Fog, Settings.backgroundColour));


        batch = new SpriteBatch();

        // full screen post processing shader
        //ShaderProgram.pedantic = true;
        shaderProgram = new ShaderProgram(
            Gdx.files.internal("shaders\\underwater.vertex.glsl"),
            Gdx.files.internal("shaders\\underwater.fragment.glsl"));
        if (!shaderProgram.isCompiled())
            throw new GdxRuntimeException(shaderProgram.getLog());
        ShaderProgram.pedantic = false;
        u_time = shaderProgram.getUniformLocation("u_time");

    }

    @Override
    public void render(float delta) {
        time += delta;

        if(Gdx.input.isKeyPressed(Input.Keys.ESCAPE) || gui.exitButtonPressed){
            gui.exitDialog(game);
        }

        camController.update( world.getFocus() );
        world.update(delta);

        sceneManager.update(delta);
        sceneManager.renderShadows();
        sceneManager.renderMirror();
        sceneManager.renderTransmission();

        fbo.begin();
        ScreenUtils.clear(Settings.backgroundColour, true);

        sceneManager.renderColors();

        modelBatch.begin(cam);
        world.render(modelBatch, sceneManager.environment);
        modelBatch.end();

        fbo.end();


        // post-processing of game screen content : vignette effect and underwater wavy effect


        Sprite s = new Sprite(fbo.getColorBufferTexture());
        s.flip(false,  true); // coordinate system in buffer differs from screen

        batch.begin();
        batch.setShader(shaderProgram);						// post-processing shader
        shaderProgram.setUniformf(u_time, time);
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
    public void hide() {
        // This method is called when another screen replaces this one.
        dispose();
    }

    @Override
    public void dispose() {
        sceneManager.dispose();
        environmentCubemap.dispose();
        diffuseCubemap.dispose();
        specularCubemap.dispose();
        brdfLUT.dispose();

        world.dispose();
        modelBatch.dispose();
        gui.dispose();
        fbo.dispose();
        batch.dispose();
    }
}
