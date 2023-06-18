package com.monstrous.underthesea;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.math.GridPoint3;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Disposable;

public class Chunk implements Disposable {

    public static final int CHUNK_WIDTH = 32;    // in block units
    public static final int CHUNK_HEIGHT = 32;

    public String key;                      // unique identifier for this chunk
    public int cx, cy, cz;                  // chunk coordinate
    public boolean hasVolume;               // has volume been generated yet?
    public boolean hasMesh;                 // has mesh been generated yet?
    private Model model;
    public ModelInstance modelInstance;
    private VolumeMap volume;
    private NoiseSettings settings;
    private MarchingCubes mcubes;
    private Voxels voxels;

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
        voxels = new Voxels();

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

        //model = voxels.build(volume, CHUNK_WIDTH, CHUNK_HEIGHT, Color.OLIVE);
        Color color = Color.OLIVE;
        if((cx % 2 == 1) ^ (cz % 2 == 1))
            color = Color.GREEN;

        model = mcubes.build(volume, CHUNK_WIDTH, CHUNK_HEIGHT, color);

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

        char [][][] map = noise.makeVolume(CHUNK_WIDTH+1, CHUNK_HEIGHT+1, settings);
        return new VolumeMap(map);
    }

    @Override
    public void dispose() {
        model.dispose();
    }
}
