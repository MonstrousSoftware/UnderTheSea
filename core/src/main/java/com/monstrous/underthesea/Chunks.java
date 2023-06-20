package com.monstrous.underthesea;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g3d.Environment;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.math.GridPoint3;
import com.badlogic.gdx.math.Vector3;
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

    public boolean collides( Vector3 point ){
        int cx = (int)Math.floor(point.x / Chunk.CHUNK_WIDTH);
        int cz = (int)Math.floor(point.z / Chunk.CHUNK_WIDTH);
        int cy = (int)Math.floor(point.y / Chunk.CHUNK_HEIGHT);

        // todo could be sped up with some lookup map
        for(Chunk chunk : chunks ) {
            if(chunk.cx == cx && chunk.cz == cz && chunk.cy == cy) {
                Gdx.app.log("check chunk", "cx: "+cx + " cy: "+ cy + "cz: "+ cz);
                return chunk.collides(point);
            }
        }
        return false;
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
