package com.monstrous.underthesea.shaders;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g3d.Renderable;
import com.badlogic.gdx.graphics.g3d.Shader;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.graphics.g3d.shaders.DefaultShader;
import net.mgsx.gltf.scene3d.shaders.PBRShaderConfig;
import net.mgsx.gltf.scene3d.shaders.PBRShaderProvider;

public class MyShaderProvider extends PBRShaderProvider {

    public static String fileName = "terrain";

    private DefaultShader.Config terrainConfig;

    public MyShaderProvider() {
        super(new PBRShaderConfig());

        terrainConfig = new DefaultShader.Config(
            Gdx.files.internal("shaders/"+fileName+".vertex.glsl").readString(),
            Gdx.files.internal("shaders/"+fileName+".fragment.glsl").readString());
        terrainConfig.numBones = 14;
    }


    @Override
    protected Shader createShader(Renderable renderable) {
        // create the appropriate shader based on specific attributes
        // otherwise create a default shader
        //
        ColorAttribute colAttr = (ColorAttribute)renderable.material.get(ColorAttribute.Emissive);

        if(colAttr != null && colAttr.color.equals(Color.FIREBRICK))    // special colour
            return new DefaultShader(renderable, terrainConfig);

        return super.createShader(renderable);
    }



}
