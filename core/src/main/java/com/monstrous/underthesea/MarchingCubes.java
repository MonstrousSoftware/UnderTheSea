package com.monstrous.underthesea;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.VertexAttributes;
import com.badlogic.gdx.graphics.g3d.Material;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.graphics.g3d.attributes.TextureAttribute;
import com.badlogic.gdx.graphics.g3d.utils.MeshPartBuilder;
import com.badlogic.gdx.graphics.g3d.utils.ModelBuilder;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector3;


// very basic voxel cube meshing, no greedy meshing etc.

public class MarchingCubes {

    public static char isoThreshold = 64;

    public static final int[][] triangulationTable = { {}, { 0, 8, 3 }, { 0, 1, 9 }, { 1, 8, 3, 9, 8, 1 }, { 1, 2, 10 }, { 0, 8, 3, 1, 2, 10 }, { 9, 2, 10, 0, 2, 9 },
        { 2, 8, 3, 2, 10, 8, 10, 9, 8 }, { 3, 11, 2 }, { 0, 11, 2, 8, 11, 0 }, { 1, 9, 0, 2, 3, 11 }, { 1, 11, 2, 1, 9, 11, 9, 8, 11 }, { 3, 10, 1, 11, 10, 3 },
        { 0, 10, 1, 0, 8, 10, 8, 11, 10 }, { 3, 9, 0, 3, 11, 9, 11, 10, 9 }, { 9, 8, 10, 10, 8, 11 }, { 4, 7, 8 }, { 4, 3, 0, 7, 3, 4 }, { 0, 1, 9, 8, 4, 7 },
        { 4, 1, 9, 4, 7, 1, 7, 3, 1 }, { 1, 2, 10, 8, 4, 7 }, { 3, 4, 7, 3, 0, 4, 1, 2, 10 }, { 9, 2, 10, 9, 0, 2, 8, 4, 7 }, { 2, 10, 9, 2, 9, 7, 2, 7, 3, 7, 9, 4 },
        { 8, 4, 7, 3, 11, 2 }, { 11, 4, 7, 11, 2, 4, 2, 0, 4 }, { 9, 0, 1, 8, 4, 7, 2, 3, 11 }, { 4, 7, 11, 9, 4, 11, 9, 11, 2, 9, 2, 1 }, { 3, 10, 1, 3, 11, 10, 7, 8, 4 },
        { 1, 11, 10, 1, 4, 11, 1, 0, 4, 7, 11, 4 }, { 4, 7, 8, 9, 0, 11, 9, 11, 10, 11, 0, 3 }, { 4, 7, 11, 4, 11, 9, 9, 11, 10 }, { 9, 5, 4 }, { 9, 5, 4, 0, 8, 3 },
        { 0, 5, 4, 1, 5, 0 }, { 8, 5, 4, 8, 3, 5, 3, 1, 5 }, { 1, 2, 10, 9, 5, 4 }, { 3, 0, 8, 1, 2, 10, 4, 9, 5 }, { 5, 2, 10, 5, 4, 2, 4, 0, 2 },
        { 2, 10, 5, 3, 2, 5, 3, 5, 4, 3, 4, 8 }, { 9, 5, 4, 2, 3, 11 }, { 0, 11, 2, 0, 8, 11, 4, 9, 5 }, { 0, 5, 4, 0, 1, 5, 2, 3, 11 }, { 2, 1, 5, 2, 5, 8, 2, 8, 11, 4, 8, 5 },
        { 10, 3, 11, 10, 1, 3, 9, 5, 4 }, { 4, 9, 5, 0, 8, 1, 8, 10, 1, 8, 11, 10 }, { 5, 4, 0, 5, 0, 11, 5, 11, 10, 11, 0, 3 }, { 5, 4, 8, 5, 8, 10, 10, 8, 11 },
        { 9, 7, 8, 5, 7, 9 }, { 9, 3, 0, 9, 5, 3, 5, 7, 3 }, { 0, 7, 8, 0, 1, 7, 1, 5, 7 }, { 1, 5, 3, 3, 5, 7 }, { 9, 7, 8, 9, 5, 7, 10, 1, 2 },
        { 10, 1, 2, 9, 5, 0, 5, 3, 0, 5, 7, 3 }, { 8, 0, 2, 8, 2, 5, 8, 5, 7, 10, 5, 2 }, { 2, 10, 5, 2, 5, 3, 3, 5, 7 }, { 7, 9, 5, 7, 8, 9, 3, 11, 2 },
        { 9, 5, 7, 9, 7, 2, 9, 2, 0, 2, 7, 11 }, { 2, 3, 11, 0, 1, 8, 1, 7, 8, 1, 5, 7 }, { 11, 2, 1, 11, 1, 7, 7, 1, 5 }, { 9, 5, 8, 8, 5, 7, 10, 1, 3, 10, 3, 11 },
        { 5, 7, 0, 5, 0, 9, 7, 11, 0, 1, 0, 10, 11, 10, 0 }, { 11, 10, 0, 11, 0, 3, 10, 5, 0, 8, 0, 7, 5, 7, 0 }, { 11, 10, 5, 7, 11, 5 }, { 10, 6, 5 }, { 0, 8, 3, 5, 10, 6 },
        { 9, 0, 1, 5, 10, 6 }, { 1, 8, 3, 1, 9, 8, 5, 10, 6 }, { 1, 6, 5, 2, 6, 1 }, { 1, 6, 5, 1, 2, 6, 3, 0, 8 }, { 9, 6, 5, 9, 0, 6, 0, 2, 6 },
        { 5, 9, 8, 5, 8, 2, 5, 2, 6, 3, 2, 8 }, { 2, 3, 11, 10, 6, 5 }, { 11, 0, 8, 11, 2, 0, 10, 6, 5 }, { 0, 1, 9, 2, 3, 11, 5, 10, 6 },
        { 5, 10, 6, 1, 9, 2, 9, 11, 2, 9, 8, 11 }, { 6, 3, 11, 6, 5, 3, 5, 1, 3 }, { 0, 8, 11, 0, 11, 5, 0, 5, 1, 5, 11, 6 }, { 3, 11, 6, 0, 3, 6, 0, 6, 5, 0, 5, 9 },
        { 6, 5, 9, 6, 9, 11, 11, 9, 8 }, { 5, 10, 6, 4, 7, 8 }, { 4, 3, 0, 4, 7, 3, 6, 5, 10 }, { 1, 9, 0, 5, 10, 6, 8, 4, 7 }, { 10, 6, 5, 1, 9, 7, 1, 7, 3, 7, 9, 4 },
        { 6, 1, 2, 6, 5, 1, 4, 7, 8 }, { 1, 2, 5, 5, 2, 6, 3, 0, 4, 3, 4, 7 }, { 8, 4, 7, 9, 0, 5, 0, 6, 5, 0, 2, 6 }, { 7, 3, 9, 7, 9, 4, 3, 2, 9, 5, 9, 6, 2, 6, 9 },
        { 3, 11, 2, 7, 8, 4, 10, 6, 5 }, { 5, 10, 6, 4, 7, 2, 4, 2, 0, 2, 7, 11 }, { 0, 1, 9, 4, 7, 8, 2, 3, 11, 5, 10, 6 }, { 9, 2, 1, 9, 11, 2, 9, 4, 11, 7, 11, 4, 5, 10, 6 },
        { 8, 4, 7, 3, 11, 5, 3, 5, 1, 5, 11, 6 }, { 5, 1, 11, 5, 11, 6, 1, 0, 11, 7, 11, 4, 0, 4, 11 }, { 0, 5, 9, 0, 6, 5, 0, 3, 6, 11, 6, 3, 8, 4, 7 },
        { 6, 5, 9, 6, 9, 11, 4, 7, 9, 7, 11, 9 }, { 10, 4, 9, 6, 4, 10 }, { 4, 10, 6, 4, 9, 10, 0, 8, 3 }, { 10, 0, 1, 10, 6, 0, 6, 4, 0 }, { 8, 3, 1, 8, 1, 6, 8, 6, 4, 6, 1, 10 },
        { 1, 4, 9, 1, 2, 4, 2, 6, 4 }, { 3, 0, 8, 1, 2, 9, 2, 4, 9, 2, 6, 4 }, { 0, 2, 4, 4, 2, 6 }, { 8, 3, 2, 8, 2, 4, 4, 2, 6 }, { 10, 4, 9, 10, 6, 4, 11, 2, 3 },
        { 0, 8, 2, 2, 8, 11, 4, 9, 10, 4, 10, 6 }, { 3, 11, 2, 0, 1, 6, 0, 6, 4, 6, 1, 10 }, { 6, 4, 1, 6, 1, 10, 4, 8, 1, 2, 1, 11, 8, 11, 1 },
        { 9, 6, 4, 9, 3, 6, 9, 1, 3, 11, 6, 3 }, { 8, 11, 1, 8, 1, 0, 11, 6, 1, 9, 1, 4, 6, 4, 1 }, { 3, 11, 6, 3, 6, 0, 0, 6, 4 }, { 6, 4, 8, 11, 6, 8 },
        { 7, 10, 6, 7, 8, 10, 8, 9, 10 }, { 0, 7, 3, 0, 10, 7, 0, 9, 10, 6, 7, 10 }, { 10, 6, 7, 1, 10, 7, 1, 7, 8, 1, 8, 0 }, { 10, 6, 7, 10, 7, 1, 1, 7, 3 },
        { 1, 2, 6, 1, 6, 8, 1, 8, 9, 8, 6, 7 }, { 2, 6, 9, 2, 9, 1, 6, 7, 9, 0, 9, 3, 7, 3, 9 }, { 7, 8, 0, 7, 0, 6, 6, 0, 2 }, { 7, 3, 2, 6, 7, 2 },
        { 2, 3, 11, 10, 6, 8, 10, 8, 9, 8, 6, 7 }, { 2, 0, 7, 2, 7, 11, 0, 9, 7, 6, 7, 10, 9, 10, 7 }, { 1, 8, 0, 1, 7, 8, 1, 10, 7, 6, 7, 10, 2, 3, 11 },
        { 11, 2, 1, 11, 1, 7, 10, 6, 1, 6, 7, 1 }, { 8, 9, 6, 8, 6, 7, 9, 1, 6, 11, 6, 3, 1, 3, 6 }, { 0, 9, 1, 11, 6, 7 }, { 7, 8, 0, 7, 0, 6, 3, 11, 0, 11, 6, 0 }, { 7, 11, 6 },
        { 7, 6, 11 }, { 3, 0, 8, 11, 7, 6 }, { 0, 1, 9, 11, 7, 6 }, { 8, 1, 9, 8, 3, 1, 11, 7, 6 }, { 10, 1, 2, 6, 11, 7 }, { 1, 2, 10, 3, 0, 8, 6, 11, 7 },
        { 2, 9, 0, 2, 10, 9, 6, 11, 7 }, { 6, 11, 7, 2, 10, 3, 10, 8, 3, 10, 9, 8 }, { 7, 2, 3, 6, 2, 7 }, { 7, 0, 8, 7, 6, 0, 6, 2, 0 }, { 2, 7, 6, 2, 3, 7, 0, 1, 9 },
        { 1, 6, 2, 1, 8, 6, 1, 9, 8, 8, 7, 6 }, { 10, 7, 6, 10, 1, 7, 1, 3, 7 }, { 10, 7, 6, 1, 7, 10, 1, 8, 7, 1, 0, 8 }, { 0, 3, 7, 0, 7, 10, 0, 10, 9, 6, 10, 7 },
        { 7, 6, 10, 7, 10, 8, 8, 10, 9 }, { 6, 8, 4, 11, 8, 6 }, { 3, 6, 11, 3, 0, 6, 0, 4, 6 }, { 8, 6, 11, 8, 4, 6, 9, 0, 1 }, { 9, 4, 6, 9, 6, 3, 9, 3, 1, 11, 3, 6 },
        { 6, 8, 4, 6, 11, 8, 2, 10, 1 }, { 1, 2, 10, 3, 0, 11, 0, 6, 11, 0, 4, 6 }, { 4, 11, 8, 4, 6, 11, 0, 2, 9, 2, 10, 9 }, { 10, 9, 3, 10, 3, 2, 9, 4, 3, 11, 3, 6, 4, 6, 3 },
        { 8, 2, 3, 8, 4, 2, 4, 6, 2 }, { 0, 4, 2, 4, 6, 2 }, { 1, 9, 0, 2, 3, 4, 2, 4, 6, 4, 3, 8 }, { 1, 9, 4, 1, 4, 2, 2, 4, 6 }, { 8, 1, 3, 8, 6, 1, 8, 4, 6, 6, 10, 1 },
        { 10, 1, 0, 10, 0, 6, 6, 0, 4 }, { 4, 6, 3, 4, 3, 8, 6, 10, 3, 0, 3, 9, 10, 9, 3 }, { 10, 9, 4, 6, 10, 4 }, { 4, 9, 5, 7, 6, 11 }, { 0, 8, 3, 4, 9, 5, 11, 7, 6 },
        { 5, 0, 1, 5, 4, 0, 7, 6, 11 }, { 11, 7, 6, 8, 3, 4, 3, 5, 4, 3, 1, 5 }, { 9, 5, 4, 10, 1, 2, 7, 6, 11 }, { 6, 11, 7, 1, 2, 10, 0, 8, 3, 4, 9, 5 },
        { 7, 6, 11, 5, 4, 10, 4, 2, 10, 4, 0, 2 }, { 3, 4, 8, 3, 5, 4, 3, 2, 5, 10, 5, 2, 11, 7, 6 }, { 7, 2, 3, 7, 6, 2, 5, 4, 9 }, { 9, 5, 4, 0, 8, 6, 0, 6, 2, 6, 8, 7 },
        { 3, 6, 2, 3, 7, 6, 1, 5, 0, 5, 4, 0 }, { 6, 2, 8, 6, 8, 7, 2, 1, 8, 4, 8, 5, 1, 5, 8 }, { 9, 5, 4, 10, 1, 6, 1, 7, 6, 1, 3, 7 },
        { 1, 6, 10, 1, 7, 6, 1, 0, 7, 8, 7, 0, 9, 5, 4 }, { 4, 0, 10, 4, 10, 5, 0, 3, 10, 6, 10, 7, 3, 7, 10 }, { 7, 6, 10, 7, 10, 8, 5, 4, 10, 4, 8, 10 },
        { 6, 9, 5, 6, 11, 9, 11, 8, 9 }, { 3, 6, 11, 0, 6, 3, 0, 5, 6, 0, 9, 5 }, { 0, 11, 8, 0, 5, 11, 0, 1, 5, 5, 6, 11 }, { 6, 11, 3, 6, 3, 5, 5, 3, 1 },
        { 1, 2, 10, 9, 5, 11, 9, 11, 8, 11, 5, 6 }, { 0, 11, 3, 0, 6, 11, 0, 9, 6, 5, 6, 9, 1, 2, 10 }, { 11, 8, 5, 11, 5, 6, 8, 0, 5, 10, 5, 2, 0, 2, 5 },
        { 6, 11, 3, 6, 3, 5, 2, 10, 3, 10, 5, 3 }, { 5, 8, 9, 5, 2, 8, 5, 6, 2, 3, 8, 2 }, { 9, 5, 6, 9, 6, 0, 0, 6, 2 }, { 1, 5, 8, 1, 8, 0, 5, 6, 8, 3, 8, 2, 6, 2, 8 },
        { 1, 5, 6, 2, 1, 6 }, { 1, 3, 6, 1, 6, 10, 3, 8, 6, 5, 6, 9, 8, 9, 6 }, { 10, 1, 0, 10, 0, 6, 9, 5, 0, 5, 6, 0 }, { 0, 3, 8, 5, 6, 10 }, { 10, 5, 6 },
        { 11, 5, 10, 7, 5, 11 }, { 11, 5, 10, 11, 7, 5, 8, 3, 0 }, { 5, 11, 7, 5, 10, 11, 1, 9, 0 }, { 10, 7, 5, 10, 11, 7, 9, 8, 1, 8, 3, 1 }, { 11, 1, 2, 11, 7, 1, 7, 5, 1 },
        { 0, 8, 3, 1, 2, 7, 1, 7, 5, 7, 2, 11 }, { 9, 7, 5, 9, 2, 7, 9, 0, 2, 2, 11, 7 }, { 7, 5, 2, 7, 2, 11, 5, 9, 2, 3, 2, 8, 9, 8, 2 }, { 2, 5, 10, 2, 3, 5, 3, 7, 5 },
        { 8, 2, 0, 8, 5, 2, 8, 7, 5, 10, 2, 5 }, { 9, 0, 1, 5, 10, 3, 5, 3, 7, 3, 10, 2 }, { 9, 8, 2, 9, 2, 1, 8, 7, 2, 10, 2, 5, 7, 5, 2 }, { 1, 3, 5, 3, 7, 5 },
        { 0, 8, 7, 0, 7, 1, 1, 7, 5 }, { 9, 0, 3, 9, 3, 5, 5, 3, 7 }, { 9, 8, 7, 5, 9, 7 }, { 5, 8, 4, 5, 10, 8, 10, 11, 8 }, { 5, 0, 4, 5, 11, 0, 5, 10, 11, 11, 3, 0 },
        { 0, 1, 9, 8, 4, 10, 8, 10, 11, 10, 4, 5 }, { 10, 11, 4, 10, 4, 5, 11, 3, 4, 9, 4, 1, 3, 1, 4 }, { 2, 5, 1, 2, 8, 5, 2, 11, 8, 4, 5, 8 },
        { 0, 4, 11, 0, 11, 3, 4, 5, 11, 2, 11, 1, 5, 1, 11 }, { 0, 2, 5, 0, 5, 9, 2, 11, 5, 4, 5, 8, 11, 8, 5 }, { 9, 4, 5, 2, 11, 3 }, { 2, 5, 10, 3, 5, 2, 3, 4, 5, 3, 8, 4 },
        { 5, 10, 2, 5, 2, 4, 4, 2, 0 }, { 3, 10, 2, 3, 5, 10, 3, 8, 5, 4, 5, 8, 0, 1, 9 }, { 5, 10, 2, 5, 2, 4, 1, 9, 2, 9, 4, 2 }, { 8, 4, 5, 8, 5, 3, 3, 5, 1 },
        { 0, 4, 5, 1, 0, 5 }, { 8, 4, 5, 8, 5, 3, 9, 0, 5, 0, 3, 5 }, { 9, 4, 5 }, { 4, 11, 7, 4, 9, 11, 9, 10, 11 }, { 0, 8, 3, 4, 9, 7, 9, 11, 7, 9, 10, 11 },
        { 1, 10, 11, 1, 11, 4, 1, 4, 0, 7, 4, 11 }, { 3, 1, 4, 3, 4, 8, 1, 10, 4, 7, 4, 11, 10, 11, 4 }, { 4, 11, 7, 9, 11, 4, 9, 2, 11, 9, 1, 2 },
        { 9, 7, 4, 9, 11, 7, 9, 1, 11, 2, 11, 1, 0, 8, 3 }, { 11, 7, 4, 11, 4, 2, 2, 4, 0 }, { 11, 7, 4, 11, 4, 2, 8, 3, 4, 3, 2, 4 }, { 2, 9, 10, 2, 7, 9, 2, 3, 7, 7, 4, 9 },
        { 9, 10, 7, 9, 7, 4, 10, 2, 7, 8, 7, 0, 2, 0, 7 }, { 3, 7, 10, 3, 10, 2, 7, 4, 10, 1, 10, 0, 4, 0, 10 }, { 1, 10, 2, 8, 7, 4 }, { 4, 9, 1, 4, 1, 7, 7, 1, 3 },
        { 4, 9, 1, 4, 1, 7, 0, 8, 1, 8, 7, 1 }, { 4, 0, 3, 7, 4, 3 }, { 4, 8, 7 }, { 9, 10, 8, 10, 11, 8 }, { 3, 0, 9, 3, 9, 11, 11, 9, 10 }, { 0, 1, 10, 0, 10, 8, 8, 10, 11 },
        { 3, 1, 10, 11, 3, 10 }, { 1, 2, 11, 1, 11, 9, 9, 11, 8 }, { 3, 0, 9, 3, 9, 11, 1, 2, 9, 2, 11, 9 }, { 0, 2, 11, 8, 0, 11 }, { 3, 2, 11 }, { 2, 3, 8, 2, 8, 10, 10, 8, 9 },
        { 9, 10, 2, 0, 9, 2 }, { 2, 3, 8, 2, 8, 10, 0, 1, 8, 1, 10, 8 }, { 1, 10, 2 }, { 1, 3, 8, 9, 1, 8 }, { 0, 9, 1 }, { 0, 3, 8 }, {} };


    // vertices per edge, e.g. edge 0 connects vertex 0 and 1
    public static final int edges[][] = {   {0, 1}, { 1, 2}, {2, 3}, {3, 0},
                                            {4, 5}, {5, 6} , {6, 7}, {7, 4},
                                            {0, 4}, {1, 5}, {2, 6}, {3,7} };

    // note: y, x, z
    public static final int vertexOffsets[][] = {   {0,0,1}, { 0,1,1}, { 0, 1, 0}, { 0, 0, 0 }, // vertices 0, 1, 2, 3
                                                    {1,0,1}, { 1,1,1}, { 1, 1, 0}, { 1, 0, 0 } }; // vertices 4, 5, 6, 7



    private MeshPartBuilder meshBuilder;
    private Vector3 nor = new Vector3();

    protected static final MeshPartBuilder.VertexInfo vertTmp0 = new MeshPartBuilder.VertexInfo();
    protected static final MeshPartBuilder.VertexInfo vertTmp1 = new MeshPartBuilder.VertexInfo();
    protected static final MeshPartBuilder.VertexInfo vertTmp2 = new MeshPartBuilder.VertexInfo();
    private int chunkResolution;
    private int chunkHeight;
    private VolumeMap volumeMap;
    public static boolean wireframeMode = false;


    public Model build(VolumeMap volumeMap, int chunkResolution, int chunkHeight, Color color) {
        this.volumeMap = volumeMap;
        this.chunkResolution = chunkResolution;
        this.chunkHeight = chunkHeight;
        Material mat = new Material(ColorAttribute.createDiffuse(color));
//        Texture img = new Texture("images/badlogic.jpg");
//        Material mat = new Material(TextureAttribute.createDiffuse(img));

       int primitive = GL20.GL_TRIANGLES;
       if(wireframeMode)
            primitive = GL20.GL_LINES;

        vertTmp0.setUV(0,0);
        vertTmp1.setUV(1,0);
        vertTmp2.setUV(0,1);

        // create model

        ModelBuilder modelBuilder = new ModelBuilder();
        modelBuilder.begin();

        meshBuilder = modelBuilder.part("part1", primitive,
            VertexAttributes.Usage.Position | VertexAttributes.Usage.Normal | VertexAttributes.Usage.TextureCoordinates, mat);

        for(int x = 0; x < chunkResolution; x++) {
            for (int z = 0; z < chunkResolution; z++) {
                for (int y = 0; y < chunkHeight; y++) {

                    marchCube(x, y, z);
                }
            }
        }
        Model model = modelBuilder.end();
        return model;
    }


    private void marchCube(int x, int y, int z){

        int code = classifyCube(x, y, z);
        int [] triangulation = triangulationTable[code];
        for(int i = 0; i < triangulation.length; i += 3){
            setVertex( x, y, z, vertTmp0, triangulation[i] );
            setVertex( x, y, z, vertTmp1, triangulation[i+1] );
            setVertex( x, y, z, vertTmp2, triangulation[i+2] );
            makeTriangle( vertTmp0, vertTmp1, vertTmp2);
        }
    }

    private void setVertex( int cx, int cy, int cz, MeshPartBuilder.VertexInfo vertInfo, int edgeIndex ) {
        int v0 = edges[edgeIndex][0];
        int v1 = edges[edgeIndex][1];


        int y = vertexOffsets[v0][0];
        int x = vertexOffsets[v0][1];
        int z = vertexOffsets[v0][2];
        int y2 = vertexOffsets[v1][0];
        int x2 = vertexOffsets[v1][1];
        int z2 = vertexOffsets[v1][2];
        char val0 = volumeMap.data[cy+y][cx+x][cz+z];
        char val1 = volumeMap.data[cy+y2][cx+x2][cz+z2];
        float a = (isoThreshold - val0)/(float)(val1 - val0);

        //float a = 0.5f;
        float yf = MathUtils.lerp(y, y2, a);
        float xf = MathUtils.lerp(x, x2, a);
        float zf = MathUtils.lerp(z, z2, a);

        vertInfo.setPos(cx+xf, cy+yf, cz+zf);
    }


    private int classifyCube(int x, int y, int z) {

        int value = 0;
        if(volumeMap.data[y+1][x][z] > isoThreshold )
            value++;
        value <<= 1;
        if(volumeMap.data[y+1][x+1][z] > isoThreshold )
            value++;
        value <<= 1;
        if(volumeMap.data[y+1][x+1][z+1] > isoThreshold )
            value++;
        value <<= 1;
        if(volumeMap.data[y+1][x][z+1] > isoThreshold )
            value++;
        value <<= 1;
        if(volumeMap.data[y][x][z] > isoThreshold )
            value++;
        value <<= 1;
        if(volumeMap.data[y][x+1][z] > isoThreshold )
            value++;
        value <<= 1;
        if(volumeMap.data[y][x+1][z+1] > isoThreshold )
            value++;
        value <<= 1;
        if(volumeMap.data[y][x][z+1] > isoThreshold )
            value++;
        return value;
    }



    private Vector3 du = new Vector3();
    private Vector3 dv = new Vector3();
    private Vector3 nv = new Vector3();

    private void makeTriangle( MeshPartBuilder.VertexInfo v1, MeshPartBuilder.VertexInfo v2, MeshPartBuilder.VertexInfo v3 ){
        meshBuilder.ensureVertices(3);

        // calculate normal vector from triangle
        du.set(v2.position).sub(v1.position).nor();
        dv.set(v3.position).sub(v1.position).nor();
        nor.set(dv).crs(du).nor();

        v1.setNor(nor);
        v2.setNor(nor);
        v3.setNor(nor);

        final short i1 = meshBuilder.vertex(v1);
        final short i2 = meshBuilder.vertex(v2);
        final short i3 = meshBuilder.vertex(v3);

        meshBuilder.ensureTriangleIndices(1);
        meshBuilder.triangle(i2, i1, i3);         // make sure the winding goes the right way

        if(wireframeMode) {
            meshBuilder.ensureVertices(2);
            v1.position.add(v2.position).add(v3.position).scl(0.333f);
            final short c = meshBuilder.vertex(v1);
            v2.position.set(v1.position).add(nor);
            final short n = meshBuilder.vertex(v2);
            meshBuilder.line(c, n);
        }
    }


}
