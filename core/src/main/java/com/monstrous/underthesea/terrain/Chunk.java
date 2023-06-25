package com.monstrous.underthesea.terrain;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g3d.Material;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.math.GridPoint3;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Disposable;
import net.mgsx.gltf.scene3d.attributes.PBRColorAttribute;
import net.mgsx.gltf.scene3d.attributes.PBRFloatAttribute;
import net.mgsx.gltf.scene3d.attributes.PBRTextureAttribute;
import net.mgsx.gltf.scene3d.scene.Scene;
import org.ode4j.ode.DTriMesh;

public class Chunk implements Disposable {

    public static final int CHUNK_WIDTH = 31;    // in block units
    public static final int CHUNK_HEIGHT = 127;

    public String key;                      // unique identifier for this chunk
    public int cx, cy, cz;                  // chunk coordinate
    public boolean hasVolume;               // has volume been generated yet?
    public boolean hasMesh;                 // has mesh been generated yet?
    private Model model;
    public ModelInstance modelInstance;
    public Scene scene;
    private VolumeMap volume;
    private DistanceField distanceField;
    private NoiseSettings settings;
    private MarchingCubes mcubes;
    private PBRColorAttribute baseColor;
    private PBRFloatAttribute metallic;
    private PBRFloatAttribute roughness;


    public Chunk(String key, GridPoint3 coordinates, NoiseSettings settings) {
        this.key = key;
        this.cx = coordinates.x;
        this.cy = coordinates.y;
        this.cz = coordinates.z;
        this.settings = settings;
        hasVolume = false;
        hasMesh = false;

        mcubes = new MarchingCubes();
    }

    public void buildVolume( ) {
        if(hasVolume)
            return;

        // create volume using noise generator
        volume = makeVolume3d(settings, cx, cy, cz);

//        distanceField = new DistanceField(volume, CHUNK_WIDTH+1, CHUNK_HEIGHT+1, CHUNK_WIDTH+1);
//        distanceField.save(cx, cy, cz);
//
//        distanceField = new DistanceField(null, CHUNK_WIDTH+1, CHUNK_HEIGHT+1, CHUNK_WIDTH+1);
//        distanceField.load(cx, cy, cz);

        hasVolume = true;
        hasMesh = false;
    }


    public void buildMesh() {

        //model = voxels.build(volume, CHUNK_WIDTH, CHUNK_HEIGHT, Color.OLIVE);
        Color color = Color.OLIVE;
//        if(((Math.abs(cx) + Math.abs(cz))% 2 == 1))
//            color = Color.GREEN;

        model = mcubes.build(volume, CHUNK_WIDTH, CHUNK_HEIGHT, color);

        Vector3 pos = new Vector3(CHUNK_WIDTH *cx, CHUNK_HEIGHT*cy, CHUNK_WIDTH *cz);

        modelInstance =  new ModelInstance(model);
        modelInstance.transform.setTranslation(pos);
        scene = new Scene(modelInstance);
        Material material = modelInstance.materials.first();
        material.set(baseColor = new PBRColorAttribute(PBRColorAttribute.BaseColorFactor, Color.OLIVE));
        material.set(metallic = new PBRFloatAttribute(PBRFloatAttribute.Metallic, 0f));
        material.set(roughness = new PBRFloatAttribute(PBRFloatAttribute.Roughness, 1.0f));
        Texture img = new Texture(Gdx.files.internal("images/coral.jpg"), true);
        material.set(new PBRTextureAttribute(PBRTextureAttribute.BaseColorTexture, img));

        hasMesh = true;
        volume.needsRemesh = false;
    }


    // build a volume using 3d noise, allowing for caves and overhangs, etc.
    private VolumeMap makeVolume3d(NoiseSettings settings, int cx, int cy, int cz) {
        Noise noise = new Noise();

        // if a chunk is N blocks wide, we'll need to generate N+1 vertices
        // where the last vertex is the same as the first of the next chunk.

        settings.xoffset = cx * settings.PerlinScale *(CHUNK_WIDTH)/(float)(CHUNK_WIDTH+1);
        settings.zoffset = cz * settings.PerlinScale *(CHUNK_WIDTH)/(float)(CHUNK_WIDTH+1);
        settings.yoffset = cy * settings.PerlinScale *(CHUNK_HEIGHT)/(float)(CHUNK_HEIGHT+1);

        // need 1 more than CHUNK_WIDTH because we are generating sample points on the corners
        // of the cubes.
        char [][][] map = noise.makeVolume(CHUNK_WIDTH+1, CHUNK_HEIGHT+1, settings);
        return new VolumeMap(map);
    }

    public int distanceToRock( GridPoint3 point ){
        return distanceField.getDistance(point.x,point.y,point.z);
    }


    public boolean collides( Vector3 point ){
        char [][][] data = volume.data;

        int x = (int)(point.x - cx * CHUNK_WIDTH);
        int z = (int)(point.z - cz * CHUNK_WIDTH);
        int y = (int)(point.y - cy * CHUNK_HEIGHT);

        if( x < 0 || y < 0 || z < 0)
            Gdx.app.error("Negative index", "");

        char density = data[y][x][z];
        return density > MarchingCubes.isoThreshold;
    }

    @Override
    public void dispose() {
        model.dispose();
    }
}
