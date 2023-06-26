package com.monstrous.underthesea.terrain;


import com.badlogic.gdx.math.GridPoint3;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Disposable;
import net.mgsx.gltf.scene3d.scene.SceneManager;
import org.ode4j.ode.DSpace;
import org.ode4j.ode.OdeHelper;

public class Chunks implements Disposable {

    public static int SIZE = 3;

    private Array<Chunk> chunks;

    public Chunks() {
        NoiseSettings noiseSettings = new NoiseSettings();

        chunks = new Array<>();
        GridPoint3 coordinate = new GridPoint3();

        int y = 0;  // only one layer in up/down direction
        for (int x = -SIZE; x <= SIZE; x++) {
            for (int z = -SIZE; z <= SIZE; z++) {
                coordinate.set(x, y, z);
                Chunk chunk = new Chunk( coordinate, noiseSettings);
                chunks.add(chunk);
            }
        }
    }

    // generate a bit at a time in order not to block the rendering thread.  Call repeatedly until it returns -1.
    // returns -1 when done
    public int generate(){

        for(int i= 0 ; i < chunks.size; i++){
            Chunk chunk = chunks.get(i);
            if(!chunk.hasVolume) {
                chunk.buildVolume();
                return i;
            }
            if(!chunk.hasMesh) {
                chunk.buildMesh();
                return i;
            }
        }
        return -1;
    }

    public void addScenes(SceneManager sceneManager){
        for(Chunk chunk : chunks )
            sceneManager.addScene(chunk.scene);
    }

    public void addGeoms(DSpace space){
        //for(Chunk chunk : chunks )        // todo
        Chunk chunk = chunks.first();
            OdeHelper.createTriMesh(space, chunk.triMeshData, null, null, null);
    }

// TO DELETE
    public boolean collides( Vector3 point ){
        int cx = (int)Math.floor(point.x / Chunk.CHUNK_WIDTH);
        int cz = (int)Math.floor(point.z / Chunk.CHUNK_WIDTH);
        int cy = (int)Math.floor(point.y / Chunk.CHUNK_HEIGHT);

        // todo could be sped up with some lookup map
        for(Chunk chunk : chunks ) {
            if(chunk.cx == cx && chunk.cz == cz && chunk.cy == cy) {
                return chunk.collides(point);
            }
        }
        return false;
    }

    @Override
    public void dispose() {
        for(Chunk chunk : chunks )
            chunk.dispose();
    }
}
