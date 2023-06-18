package com.monstrous.underthesea;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.VertexAttributes;
import com.badlogic.gdx.graphics.g3d.Material;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.graphics.g3d.utils.ModelBuilder;
import com.badlogic.gdx.math.GridPoint3;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Disposable;

public class Chunk implements Disposable {

    static final int CHUNK_WIDTH = 32;    // in block units
    static final int CHUNK_HEIGHT = 32;

    public String key;                      // unique identifier for this chunk
    public int cx, cy, cz;                  // chunk coordinate
    public boolean hasVolume;               // has volume been generated yet?
    public boolean hasMesh;                 // has mesh been generated yet?
    private Model model;
    public ModelInstance modelInstance;
    private VolumeMap volume;
    private NoiseSettings settings;
    private MarchingCubes mcubes;

    public Chunk(String key, GridPoint3 coordinates, NoiseSettings settings) {
        this.key = key;
        this.cx = coordinates.x;
        this.cy = coordinates.y;
        this.cz = coordinates.z;
        this.settings = settings;
        hasVolume = false;
        hasMesh = false;
        //needsRemesh = false;
        //renderables = new Array<>();

        mcubes = new MarchingCubes();

    }

    public void buildVolume( ) {
        if(hasVolume)
            return;

        // create volume using noise generator
        volume = makeVolume3d(settings, cx, cy, cz);

        hasVolume = true;
        hasMesh = false;
    }

    public void buildMesh() {

//        GreedyMesher.ChunkMesh chunkMesh  = new GreedyMesher().build(volume, chunkResolution, chunkHeightResolution, blockSize,  neighbours);
        //model  = new GreedyMesher().build(volume, chunkResolution, chunkHeightResolution, blockSize,  neighbours);
        model = mcubes.build(volume, CHUNK_WIDTH, CHUNK_HEIGHT, Color.BLUE);
//        ModelBuilder mb = new ModelBuilder();
//
//        model = mb.createBox(CHUNK_WIDTH, CHUNK_HEIGHT, CHUNK_WIDTH,
//            new Material(ColorAttribute.createDiffuse(Color.BLUE)),
//            VertexAttributes.Usage.Position | VertexAttributes.Usage.Normal);


        Vector3 pos = new Vector3(CHUNK_WIDTH *cx, CHUNK_HEIGHT*cy, CHUNK_WIDTH *cz);

        modelInstance =  new ModelInstance(model);          // still need this?
        modelInstance.transform.setTranslation(pos);
        hasMesh = true;
        volume.needsRemesh = false;
    }


    // build a volume using 3d noise, allowing for caves and overhangs, etc.
    private VolumeMap makeVolume3d(NoiseSettings settings, int cx, int cy, int cz) {
        Noise noise = new Noise();

        settings.xoffset = cx * settings.PerlinScale;
        settings.zoffset = cz * settings.PerlinScale;
        settings.yoffset = cy * settings.PerlinScale;

        char [][][] map = noise.makeVolume(CHUNK_WIDTH, CHUNK_HEIGHT, settings);
        return new VolumeMap(map);
    }

    @Override
    public void dispose() {
        model.dispose();
    }
}
