package com.monstrous.underthesea.terrain;



import com.badlogic.gdx.math.GridPoint3;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Disposable;
import com.monstrous.underthesea.World;
import net.mgsx.gltf.scene3d.scene.SceneManager;
import org.ode4j.ode.*;

public class Chunks implements Disposable {

    public static int SIZE = 3;

    private Array<Chunk> chunks;

    public Chunks() {
        NoiseSettings noiseSettings = new NoiseSettings();

        //massInfo = OdeHelper.createMass();

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

    public void addGeoms(DWorld dworld, DSpace space){
        for(Chunk chunk : chunks ) {

                DBody body = OdeHelper.createBody(dworld);
                body.setPosition(Chunk.CHUNK_WIDTH *chunk.cx, Chunk.CHUNK_HEIGHT*chunk.cy, Chunk.CHUNK_WIDTH *chunk.cz);
                body.setKinematic();    // static body with infinite mass

                DTriMesh triMesh = OdeHelper.createTriMesh(space, chunk.triMeshData, null, null, null);
                triMesh.setBody(body);
                triMesh.setCategoryBits(World.CAT_TERRAIN);
                triMesh.setCollideBits(World.CAT_SUBMARINE);
        }
    }

    @Override
    public void dispose() {
        for(Chunk chunk : chunks )
            chunk.dispose();
    }
}
