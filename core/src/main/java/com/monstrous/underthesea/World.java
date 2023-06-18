package com.monstrous.underthesea;

import com.badlogic.gdx.graphics.VertexAttributes;
import com.badlogic.gdx.graphics.g3d.*;
import com.badlogic.gdx.graphics.g3d.utils.ModelBuilder;
import com.badlogic.gdx.math.GridPoint3;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Disposable;

public class World implements Disposable {

    private Chunk chunk;
    private NoiseSettings noiseSettings;
    private Model modelXYZ;
    private ModelInstance instanceXYZ;

    public World() {
        noiseSettings = new NoiseSettings();
        GridPoint3 coord = new GridPoint3(0,0,0);
        chunk = new Chunk("root", coord, noiseSettings);
        chunk.buildVolume();
        chunk.buildMesh();

        ModelBuilder modelBuilder = new ModelBuilder();
        modelXYZ = modelBuilder.createXYZCoordinates(10f, new Material(), VertexAttributes.Usage.Position | VertexAttributes.Usage.ColorUnpacked);
        instanceXYZ = new ModelInstance(modelXYZ, new Vector3(0, 0, 0));

    }

    public void render(ModelBatch modelBatch, Environment environment){

        chunks.render(modelBatch, environment);
        modelBatch.render(chunk.modelInstance, environment);
        modelBatch.render(instanceXYZ, environment);
    }

    @Override
    public void dispose() {
        chunk.dispose();
        modelXYZ.dispose();
    }
}
