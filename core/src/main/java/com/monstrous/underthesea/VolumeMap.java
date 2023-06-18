package com.monstrous.underthesea;

public class VolumeMap {
    public char data[][][];
    public boolean needsRemesh;

    public VolumeMap(char[][][] volumeMap) {
        this.data = volumeMap;
        this.needsRemesh = true;
    }
}
