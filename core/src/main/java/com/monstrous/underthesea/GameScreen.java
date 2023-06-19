package com.monstrous.underthesea;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.g3d.Environment;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.graphics.g3d.environment.DirectionalLight;
import com.badlogic.gdx.graphics.g3d.environment.DirectionalShadowLight;
import com.badlogic.gdx.graphics.g3d.utils.CameraInputController;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.graphics.g3d.utils.DepthShaderProvider;
import com.badlogic.gdx.graphics.glutils.FrameBuffer;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.ScreenUtils;

public class GameScreen implements Screen {


    public static int shadowMapSize = 1024;         // resolution of shadow
    public static int shadowViewPortSize = 1024;    // area where shadows are cast


    private PerspectiveCamera cam;
    private CameraInputController camController;
    private ModelBatch modelBatch;
    private ModelBatch shadowBatch;
    private Environment environment;
    private DirectionalShadowLight shadowLight;
    private World world;

    @Override
    public void show() {

        // create perspective camera
        cam = new PerspectiveCamera(60, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        cam.position.set(0, 20, 20);
        cam.lookAt(0, 0, 0);
        cam.far = Chunks.SIZE * Chunk.CHUNK_WIDTH * 2;          // TMP
        cam.near = 1f;
        cam.update();


        //inputController = new PlayerController(world.getPlayer().transform);

        // add camera controller
        camController = new CameraInputController(cam);



        // input multiplexer
        InputMultiplexer im = new InputMultiplexer();
        Gdx.input.setInputProcessor(im);
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
        world = new World();
    }

    @Override
    public void render(float delta) {
        if(Gdx.input.isKeyPressed(Input.Keys.F1)) {
            MarchingCubes.primitive = GL20.GL_TRIANGLES;
            world.rebuild();
        }
        if(Gdx.input.isKeyPressed(Input.Keys.F2)) {
            MarchingCubes.primitive = GL20.GL_LINES;
            world.rebuild();
        }

        camController.update();

        //create shadow texture
        if(Settings.shadows) {
            shadowLight.begin(cam.position, cam.direction);
            shadowBatch.begin(shadowLight.getCamera());
            world.render(shadowBatch, environment);
            shadowBatch.end();
            shadowLight.end();
        }


        ScreenUtils.clear(Settings.backgroundColour, true);
        modelBatch.begin(cam);
        world.render(modelBatch, environment);
        modelBatch.end();
    }

    @Override
    public void resize(int width, int height) {
        // Resize your screen here. The parameters represent the new window size.
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
        world.dispose();
        modelBatch.dispose();
    }
}
