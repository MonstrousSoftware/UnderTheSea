package com.monstrous.underthesea.terrain;

public class VolumeMap {
    public char data[][][];
    public boolean needsRemesh;

    public VolumeMap(char[][][] volumeMap) {
        this.data = volumeMap;
        this.needsRemesh = true;
    }
}
