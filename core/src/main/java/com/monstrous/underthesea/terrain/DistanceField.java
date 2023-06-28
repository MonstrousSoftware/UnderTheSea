package com.monstrous.underthesea.terrain;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;

// OBSOLETE

public class DistanceField {
    private byte[][][] distance;     // distance to rock face, 0 is rock
    private int h, w, d;

    public DistanceField(VolumeMap volume, int w, int h, int d) {
        this.h = h;
        this.d = d;
        this.w = w;
        distance = new byte[h][w][d];

        if(volume == null)
            return;

        // initialize: inside rock is coded as 0, outside as 255
        for (int x = 0; x < w; x++) {
            for (int y = 0; y < h; y++) {
                for (int z = 0; z < d; z++) {
                    char density = volume.data[y][x][z];    // note: y first
                    byte val = (byte) 255;
                    if (density >= MarchingCubes.isoThreshold)
                        val = 0;
                    distance[y][x][z] = val;
                }
            }
        }

        boolean changed;
        do {
            changed = false;
            for (int x = 0; x < w; x++) {
                for (int y = 0; y < h; y++) {
                    for (int z = 0; z < d; z++) {
                        changed |= propagate(x, y, z);
                    }
                }
            }
        } while (changed);
    }

    private int[] dy = { 1, -1, 0, 0, 0, 0 };
    private int[] dx = { 0, 0, 1, -1, 0, 0 };
    private int[] dz = { 0, 0, 0, 0, 1, -1 };


    private boolean propagate(int x, int y, int z){
        // check neighbouring density samples
        byte val = distance[y][x][z];
        if(val == 0)
            return false;

        boolean changed = false;
        for(int nbor = 0; nbor < 6; nbor++) {
            int ny = y + dy[nbor];
            int nx = x + dx[nbor];
            int nz = z + dz[nbor];
            if(ny < 0 || nx < 0 || nz < 0 )
                continue;
            if(ny >= h || nx >= w || nz >= d )
                continue;
            byte nval = distance[ny][nx][nz];
            if(nval+1  < val) {
                distance[y][x][z] = (byte) (nval + 1);
                changed = true;
            }
        }
        //save();
        return changed;
    }

    public String makeFileName(int x, int y, int z){
        char cx = (char) ('m'+x);
        char cy = (char) ('m'+y);
        char cz = (char) ('m'+z);
        return "fields/distance_"+cx+cy+cz+".bin";

    }

    public void save(int cx, int cy, int cz){
        String fileName = makeFileName(cx, cy, cz);
        FileHandle file = Gdx.files.local(fileName);
        for (int x = 0; x < w; x++) {
            for (int y = 0; y < h; y++) {

                file.writeBytes(distance[y][x], x > 0 || y > 0);
            }
        }
    }

    public void load( int cx, int cy, int cz){

        String fileName = makeFileName(cx, cy, cz);
        FileHandle file = Gdx.files.local(fileName);
        byte[] data = new byte[w*h*d];
        file.readBytes(data, 0, data.length);

//        for (int x = 0; x < w; x++) {
//            for (int y = 0; y < h; y++) {
//
//                 file.readBytes(); distance[y][x], y*x*d, d);
//            }
//        }
    }

    public int getDistance(int x, int y, int z){
        return (char)distance[y][x][z];
    }
}
