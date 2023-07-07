package com.monstrous.underthesea.shaders;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g3d.Renderable;
import com.badlogic.gdx.graphics.g3d.shaders.DefaultShader;

public class TerrainShader extends DefaultShader {

    public static String fileName = "normal";

    public TerrainShader(Renderable renderable, Config config) {

        super(renderable, config);

    }
}
