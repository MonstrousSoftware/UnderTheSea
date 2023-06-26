package com.monstrous.underthesea.terrain;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Mesh;
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
import org.ode4j.ode.DTriMeshData;
import org.ode4j.ode.OdeHelper;

public class Chunk implements Disposable {

    public static final int CHUNK_WIDTH = 31;    // in block units
    public static final int CHUNK_HEIGHT = 127;

    public int cx, cy, cz;                  // chunk coordinate
    public boolean hasVolume;               // has volume been generated yet?
    public boolean hasMesh;                 // has mesh been generated yet?
    private Model model;
    public ModelInstance modelInstance;
    public Scene scene;
    private VolumeMap volume;
    private NoiseSettings settings;
    private MarchingCubes mcubes;
    private PBRColorAttribute baseColor;
    private PBRFloatAttribute metallic;
    private PBRFloatAttribute roughness;
    public DTriMeshData triMeshData;


    public Chunk(GridPoint3 coordinates, NoiseSettings settings) {
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

        hasVolume = true;
        hasMesh = false;
    }


    public void buildTriMesh() {

        Vector3 v = new Vector3();
        //Vector3 offset = new Vector3(cx*CHUNK_WIDTH,cy*CHUNK_HEIGHT, cz*CHUNK_WIDTH);
        Mesh mesh = model.meshes.first();
        int nv = mesh.getNumVertices();
        int stride = mesh.getVertexSize() / 4;  // size of vertex in number of floats
        float [] vertices = new float[stride*nv];
        mesh.getVertices(vertices);

        int ni = mesh.getNumIndices();
        short [] shortIndices = new short[ni];
        mesh.getIndices(shortIndices);

        float[] verts = new float[3*nv];        // vertex data with only positions (3 floats/vertex), no UV, normals etc.
        int[] indices = new int[ni];            // integers instead of shorts

        for(int i = 0 ; i < nv; i++) {
            v.set(vertices[i * stride], vertices[i * stride + 1], vertices[i * stride + 2]);
            //v.add(offset);
            verts[3 * i] = v.x;
            verts[3 * i + 1] = v.y;
            verts[3 * i + 2] = v.z;
        }
        for(int i = 0 ; i < ni; i++) {
            indices[i] = shortIndices[i];
        }

        triMeshData = OdeHelper.createTriMeshData();
        triMeshData.build(verts, indices);
        triMeshData.preprocess();
    }

    public void buildMesh() {

        Color color = Color.OLIVE;
//        if(Math.abs(cx)%2 == 0 || Math.abs(cz)%2 == 0)
//            color = Color.GREEN;
//        if(cx == 0 && cz == 0)
//            color = Color.RED;


        model = mcubes.build(volume, CHUNK_WIDTH, CHUNK_HEIGHT, color);

        Vector3 pos = new Vector3(CHUNK_WIDTH *cx, CHUNK_HEIGHT*cy, CHUNK_WIDTH *cz);

        modelInstance =  new ModelInstance(model);
        modelInstance.transform.setTranslation(pos);
        scene = new Scene(modelInstance);
        Material material = modelInstance.materials.first();
        material.set(baseColor = new PBRColorAttribute(PBRColorAttribute.BaseColorFactor, color));
        material.set(metallic = new PBRFloatAttribute(PBRFloatAttribute.Metallic, 0f));
        material.set(roughness = new PBRFloatAttribute(PBRFloatAttribute.Roughness, 1.0f));
        Texture img = new Texture(Gdx.files.internal("images/coral.jpg"), true);
        img.setWrap(Texture.TextureWrap.MirroredRepeat, Texture.TextureWrap.MirroredRepeat);
        material.set(new PBRTextureAttribute(PBRTextureAttribute.BaseColorTexture, img));

        hasMesh = true;
        volume.needsRemesh = false;

        buildTriMesh();
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


    @Override
    public void dispose() {
        model.dispose();
    }
}
