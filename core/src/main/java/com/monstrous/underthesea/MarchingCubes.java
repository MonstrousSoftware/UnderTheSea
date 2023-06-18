package com.monstrous.underthesea;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.VertexAttributes;
import com.badlogic.gdx.graphics.g3d.Material;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.graphics.g3d.utils.MeshPartBuilder;
import com.badlogic.gdx.graphics.g3d.utils.ModelBuilder;
import com.badlogic.gdx.math.Vector3;

public class MarchingCubes {

    static final char AIR = 0;

    private MeshPartBuilder meshBuilder;
    private Vector3 nor = new Vector3();
    private float size = 1f;
    protected static final MeshPartBuilder.VertexInfo vertTmp0 = new MeshPartBuilder.VertexInfo();
    protected static final MeshPartBuilder.VertexInfo vertTmp1 = new MeshPartBuilder.VertexInfo();
    protected static final MeshPartBuilder.VertexInfo vertTmp2 = new MeshPartBuilder.VertexInfo();
    protected static final MeshPartBuilder.VertexInfo vertTmp3 = new MeshPartBuilder.VertexInfo();

    public Model build(VolumeMap volumeMap, int chunkResolution, int chunkHeight, Color color) {
        Material mat = new Material(ColorAttribute.createDiffuse(color));

        int primitive = GL20.GL_TRIANGLES;
        //    primitive = GL20.GL_LINES;



        // create model

        ModelBuilder modelBuilder = new ModelBuilder();
        modelBuilder.begin();

        meshBuilder = modelBuilder.part("part1", primitive, VertexAttributes.Usage.Position | VertexAttributes.Usage.Normal| VertexAttributes.Usage.TextureCoordinates, mat);

        for(int x = 0; x < chunkResolution; x++) {
            for(int z = 0; z < chunkResolution; z++) {
                for(int y = 0; y < chunkHeight; y++) {

                    if(volumeMap.data[y][x][z]==AIR)     // skip air cells
                        continue;

                    // centre the chunk on the origin
                    float fy = y - chunkHeight/2f;
                    float fx = x - chunkResolution/2f;
                    float fz = z - chunkResolution/2f;

                    if(y == chunkHeight-1 || volumeMap.data[y+1][x][z]==AIR)
                        makeTop(fx, fy, fz);
                    if(y == 0 || volumeMap.data[y-1][x][z]==AIR)
                        makeBottom(fx, fy, fz);
                    if(z == chunkResolution -1 || volumeMap.data[y][x][z+1]==AIR)
                        makeFront(fx, fy, fz);
                    if(z == 0 || volumeMap.data[y][x][z-1]==AIR)
                        makeBack(fx, fy, fz);
                    if(x == chunkResolution -1 || volumeMap.data[y][x+1][z]== AIR)
                        makeRight(fx, fy, fz);
                    if(x == 0 || volumeMap.data[y][x-1][z]==AIR)
                        makeLeft(fx, fy, fz);
                }
            }
        }

        Model model = modelBuilder.end();
        return model;
    }


    private void makeTop(float fx, float ht, float fz) {
        nor.set(0,1,0);

        vertTmp0.setPos(fx, ht+size, fz);
        vertTmp1.setPos(fx, ht+size, fz + size);
        vertTmp2.setPos(fx + size, ht+size, fz + size);
        vertTmp3.setPos(fx + size, ht+size, fz);

//        vertTmp0.setCol(0,0,0,1);
//        vertTmp1.setCol(0.5f,0.5f,0.5f,1);
//        vertTmp3.setCol(0.5f,0.5f,0.5f,1);
//        vertTmp2.setCol(1,1,1,1);

        makeSide(nor, vertTmp0, vertTmp1, vertTmp2, vertTmp3);
    }

    private void makeBottom(float fx, float ht, float fz) {
        nor.set(0,-1,0);

        vertTmp0.setPos(fx, ht, fz);
        vertTmp1.setPos(fx+size, ht, fz );
        vertTmp2.setPos(fx + size, ht, fz + size);
        vertTmp3.setPos(fx , ht, fz+size);

        makeSide(nor, vertTmp0, vertTmp1, vertTmp2, vertTmp3);
    }

    private void makeBack(float fx, float ht, float fz) {
        nor.set(0,0,-1);

        vertTmp0.setPos(fx, ht, fz);
        vertTmp1.setPos(fx, ht+size, fz );
        vertTmp2.setPos(fx + size, ht+size, fz);
        vertTmp3.setPos(fx + size, ht, fz);

        makeSide(nor, vertTmp0, vertTmp1, vertTmp2, vertTmp3);
    }

    private void makeFront(float fx, float ht, float fz) {
        nor.set(0,0,1);

        vertTmp0.setPos(fx, ht, fz+size);
        vertTmp1.setPos(fx + size, ht, fz+size);
        vertTmp2.setPos(fx + size, ht+size, fz+size);
        vertTmp3.setPos(fx, ht+size, fz+size );

        makeSide(nor, vertTmp0, vertTmp1, vertTmp2, vertTmp3);
    }


    private void makeRight(float fx, float ht, float fz) {
        nor.set(1,0,0);

        vertTmp0.setPos(fx+size, ht+size, fz);
        vertTmp1.setPos(fx+size, ht+size, fz + size);
        vertTmp2.setPos(fx+size, ht, fz + size);
        vertTmp3.setPos(fx+size, ht, fz);

        makeSide(nor, vertTmp0, vertTmp1, vertTmp2, vertTmp3);
    }

    private void makeLeft(float fx, float ht, float fz) {
        nor.set(-1,0,0);

        vertTmp0.setPos(fx, ht, fz);
        vertTmp1.setPos(fx, ht, fz + size);
        vertTmp2.setPos(fx, ht+size , fz + size);
        vertTmp3.setPos(fx, ht+size , fz);

        makeSide(nor, vertTmp0, vertTmp1, vertTmp2, vertTmp3);
    }

    private void makeSide(Vector3 nor, MeshPartBuilder.VertexInfo v1, MeshPartBuilder.VertexInfo v2, MeshPartBuilder.VertexInfo v3, MeshPartBuilder.VertexInfo v4 ) {
        v1.setNor(nor);
        v2.setNor(nor);
        v3.setNor(nor);
        v4.setNor(nor);
        v1.setUV(0,0);
        v2.setUV(1,0);
        v3.setUV(1,1);
        v4.setUV(0,1);

        meshBuilder.ensureVertices(4);
        final short i000 = meshBuilder.vertex(v1);
        final short i100 = meshBuilder.vertex(v2);
        final short i110 = meshBuilder.vertex(v3);
        final short i010 = meshBuilder.vertex(v4);

        meshBuilder.ensureRectangleIndices(1);
        meshBuilder.rect(i000, i100, i110, i010);
    }

}
