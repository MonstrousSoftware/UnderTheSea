package com.monstrous.underthesea.terrain;

public class DistanceField {
    private char distance[][][];     // distance to rock face, 0 is rock
    private int h, w, d;

    public DistanceField(VolumeMap volume, int w, int h, int d) {
        this.h = h;
        this.d = d;
        this.w = w;
        distance = new char[h][w][d];

        // initialize: inside rock is coded as 0, outside as 255
        for (int x = 0; x < w; x++) {
            for (int y = 0; y < h; y++) {
                for (int z = 0; z < d; z++) {
                    char density = volume.data[y][x][z];    // note: y first
                    char val = 255;
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

    private int dy[] = { 1, -1, 0, 0, 0, 0 };
    private int dx[] = { 0, 0, 1, -1, 0, 0 };
    private int dz[] = { 0, 0, 0, 0, 1, -1 };


    private boolean propagate(int x, int y, int z){
        // check neighbouring density samples
        char val = distance[y][x][z];
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
            char nval = distance[ny][nx][nz];
            if(nval+1  < val) {
                distance[y][x][z] = (char) (nval + 1);
                changed = true;
            }
        }
        return changed;
    }

    public int getDistance(int x, int y, int z){
        return distance[y][x][z];
    }
}
