package com.monstrous.underthesea;

import com.badlogic.gdx.graphics.Color;

public class Settings {

    // lighting
    static public boolean   shadows = false;
    static public float     ambientLightLevel = 0.2f;
    static public float     directionalLightLevel = 0.7f;
    static public float     shadowLightLevel = 0.7f;
    static public Color     backgroundColour = new Color(0.4f, 0.9f, 0.9f, 1f);

    static public float     titleScreenTime = 2f;

    static public boolean   renderSkyBox = false;
    static public boolean   chunkDebugColors = false;   // broken
    static public boolean   chunkOutlines = false;
    static public boolean   showFPS = true;
    static public boolean   lineMode = false;
    static public boolean   greedyMeshing = true;
    static public float     eyeHeight = 40f;
    static public boolean   walkOnGround = false; // false for fly mode
    static public boolean   dynamicChunks = true;    // generate chunks to follow view position
    static public boolean   addGrass = true;
    static public boolean   showClouds = true;
    static public boolean   showTerrain = true;
    static public boolean   showWater = true;

    static public boolean   useWaterShader = true;      // disable in case of performance issues, or poor shader support
    static public boolean   showWaterTextures = false; // debug option: shows reflection and refraction textures in sub-window
    static public boolean   showDepthTexture = false;    // debug option
    static public boolean   defaultShaderOnly = false;   // for troubleshooting
    static public boolean   useClipPlane = true;
    static public boolean   glProfiling = true;
    static public float     farPlane = 3200f;

}
