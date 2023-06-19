package com.monstrous.underthesea;

import com.badlogic.gdx.graphics.g3d.Environment;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.math.GridPoint3;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Disposable;
import net.mgsx.gltf.scene3d.scene.Scene;
import net.mgsx.gltf.scene3d.scene.SceneManager;

public class Chunks implements Disposable {

    public static int SIZE = 3;

    private Array<Chunk> chunks;
    private NoiseSettings noiseSettings;


    public Chunks() {
        noiseSettings = new NoiseSettings();

        chunks = new Array<>();
        GridPoint3 coordinate = new GridPoint3();

        for(int y = -1; y <= 0; y++) {
            for (int x = -SIZE; x <= SIZE; x++) {
                for (int z = -SIZE; z <= SIZE; z++) {
                    coordinate.set(x, y, z);
                    Chunk chunk = new Chunk("bla", coordinate, noiseSettings);
                    chunk.buildVolume();
                    chunk.buildMesh();
                    chunks.add(chunk);
                }
            }
        }
    }

    public void render(ModelBatch modelBatch, Environment environment) {
        for(Chunk chunk : chunks )
            modelBatch.render(chunk.modelInstance, environment);
    }


    public void addScene(SceneManager sceneManager) {
        for(Chunk chunk : chunks ) {
            Scene scene = new Scene(chunk.modelInstance);
            sceneManager.addScene(scene);
        }
    }

    @Override
    public void dispose() {
        for(Chunk chunk : chunks )
            chunk.dispose();
    }
}
