package com.monstrous.underthesea.terrain;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;

public class Noise {


    Vector2 a = new Vector2();
    Vector2 d1 = new Vector2();
    Vector3 a3 = new Vector3();
    Vector3 d3 = new Vector3();
    int permutation[];

    final static int permutationInit[] = { 151, 160, 137, 91, 90, 15, 131, 13, 201, 95, 96, 53, 194, 233, 7, 225, 140, 36,
            103, 30, 69, 142, 8, 99, 37, 240, 21, 10, 23, 190, 6, 148, 247, 120, 234, 75, 0,
            26, 197, 62, 94, 252, 219, 203, 117, 35, 11, 32, 57, 177, 33, 88, 237, 149, 56,
            87, 174, 20, 125, 136, 171, 168, 68, 175, 74, 165, 71, 134, 139, 48, 27, 166,
            77, 146, 158, 231, 83, 111, 229, 122, 60, 211, 133, 230, 220, 105, 92, 41, 55,
            46, 245, 40, 244, 102, 143, 54, 65, 25, 63, 161, 1, 216, 80, 73, 209, 76, 132,
            187, 208, 89, 18, 169, 200, 196, 135, 130, 116, 188, 159, 86, 164, 100, 109,
            198, 173, 186, 3, 64, 52, 217, 226, 250, 124, 123, 5, 202, 38, 147, 118, 126,
            255, 82, 85, 212, 207, 206, 59, 227, 47, 16, 58, 17, 182, 189, 28, 42, 223, 183,
            170, 213, 119, 248, 152, 2, 44, 154, 163, 70, 221, 153, 101, 155, 167, 43,
            172, 9, 129, 22, 39, 253, 19, 98, 108, 110, 79, 113, 224, 232, 178, 185, 112,
            104, 218, 246, 97, 228, 251, 34, 242, 193, 238, 210, 144, 12, 191, 179, 162,
            241, 81, 51, 145, 235, 249, 14, 239, 107, 49, 192, 214, 31, 181, 199, 106,
            157, 184, 84, 204, 176, 115, 121, 50, 45, 127, 4, 150, 254, 138, 236, 205,
            93, 222, 114, 67, 29, 24, 72, 243, 141, 128, 195, 78, 66, 215, 61, 156, 180 };

    static Vector3 gradients[] = {
            new Vector3(1,1,0), new Vector3(-1,1,0),
            new Vector3(1,-1,0), new Vector3(-1,-1, 0),
            new Vector3(1,0,1), new Vector3(-1,0,1),
            new Vector3(1,0,-1), new Vector3(-1,0,-1),
            new Vector3(0,1,1), new Vector3(0,-1,1),
            new Vector3(0,1,-1), new Vector3(0,-1,-1),

            new Vector3(1,1,0), new Vector3(-1,1,0),
            new Vector3(0,-1,1), new Vector3(0,-1,-1)
    };

    static Vector2 gradients2d[] = {
            new Vector2(1,1),
            new Vector2(-1,1),
            new Vector2(-1,-1),
            new Vector2(1,-1) };

    public Noise() {
        permutation = new int[512];
        for(int i = 0; i < 256; i++) {
            permutation[i] = permutationInit[i];
            permutation[i+256] = permutationInit[i];
        }
    }


    /* Create pseudorandom direction vector
     */
    void randomGradient(int ix, int iy, Vector2 gradient) {
        final float M = 2147483648f;
        final int shift = 16;

        int a = ix;
        int b = iy;
        a *= 348234342;
        b = b ^ ((a >> shift)|(a << shift));
        b *= 933742374;
        a = a^((b >> shift)|(b << shift));
        double rnd = ((float)a/M) * Math.PI;
        gradient.set((float)Math.sin(rnd), (float)Math.cos(rnd));
    }



    float smoothstep(float a, float b, float w) {
        float f = fade(w);
        return lerp(f, a, b);
    }

    float fade(float w) {
        return  w * w * w * (w * (w * 6 - 15) + 10);
    }

    float lerp(float fraction, float a, float b)
    {
        return a + fraction * (b-a);
    }



//    private float dotDistanceGradient(int ix, int iy, float fx, float fy){
//        randomGradient(ix, iy, a);
//        d1.set(fx,fy);
//        return a.dot(d1);
//    }

//    private float dotDistanceGradient(int hash, float fx, float fy){
//        Vector2 grad = gradients2d[ hash & 3 ];
//        d1.set(fx,fy);
//        return grad.dot(d1);
//    }
//
//    private float dotDistanceGradient(int hash, float fx, float fy, float fz){
//        Vector3 grad = gradients[ permutation[hash] & 15 ];
//        d3.set(fx, fy, fz);// distance to corner
//        return grad.dot(d3);
//
//    }

    // optimization based on the very specific gradients defined
    private float dotDistanceGradientFast(int hash, float x, float y, float z){
        switch(hash & 15) {
            case  0: return  x + y;  //(1,1,0)
            case  1: return -x + y;  //(-1,1,0)
            case  2: return  x - y;  //(1,-1,0)
            case  3: return -x - y;  //(-1,-1,0)
            case  4: return  x + z;  //(1,0,1)
            case  5: return -x + z;  //(-1,0,1)
            case  6: return  x - z;  //(1,0,-1)
            case  7: return -x - z;  //(-1,0,-1)
            case  8: return  y + z;  //(0,1,1),
            case  9: return -y + z;  //(0,-1,1),
            case 10: return  y - z;  //(0,1,-1),
            case 11: return -y - z;  //(0,-1,-1)
            case 12: return  y + x;  //(1,1,0)
            case 13: return -x + y;  //(-1,1,0)
            case 14: return -y + z;  //(0,-1,1)
            case 15: return -y - z;  //(0,-1,-1)
        }
        return 0;   // never reached
    }


//    public float PerlinNoise(float x, float y) {
//        int ix = (int) Math.floor(x);
//        int iy = (int) Math.floor(y);
//        float fx = x-ix;
//        float fy = y-iy;
//        int X = ix & 255;
//        int Y = iy & 255;
//
//        int A = permutation[permutation[X+1]+Y+1];
//        int B = permutation[permutation[X]+Y+1];
//        int C = permutation[permutation[X+1]+Y];
//        int D = permutation[permutation[X]+Y];
//
//        float f1 = dotDistanceGradient(D, fx, fy);
//        float f2 = dotDistanceGradient(C, fx-1, fy);
//        float f3 = dotDistanceGradient(B, fx, fy-1);
//        float f4 = dotDistanceGradient(A, fx-1, fy-1);
//
//        float u1 = smoothstep(f1, f2, fx);	// interpolate between top corners
//        float u2 = smoothstep(f3, f4, fx);	// between bottom corners
//        float res = smoothstep(u1, u2, fy); // between previous two points
//        return res;
//    }

    public float PerlinNoise3d(float x, float y, float z) {
        // integer part of coordinate
        int ix = (int) Math.floor(x);
        int iy = (int) Math.floor(y);
        int iz = (int) Math.floor(z);
        // fractional parts
        float fx = x-ix;
        float fy = y-iy;
        float fz = z-iz;
        int X = ix & 255;
        int Y = iy & 255;
        int Z = iz & 255;

        // hash coordinates of the 8 cube corners
        int A = permutation[X]+Y;
        int AA = permutation[A]+Z;
        int AB = permutation[A+1]+Z;
        int B = permutation[X+1]+Y;
        int BA = permutation[B]+Z;
        int BB = permutation[B+1]+Z;


        // get dot product of random gradient and distance for each of the cube's corners
        float n000 = dotDistanceGradientFast(permutation[AA], fx, fy, fz);
        float n001 = dotDistanceGradientFast(permutation[BA], fx-1, fy, fz);
        float n010 = dotDistanceGradientFast(permutation[AB], fx, fy-1, fz);
        float n100 = dotDistanceGradientFast(permutation[BB], fx-1, fy-1, fz);
        float n011 = dotDistanceGradientFast(permutation[AA+1], fx, fy, fz-1);
        float n101 = dotDistanceGradientFast(permutation[BA+1], fx-1, fy, fz-1);
        float n110 = dotDistanceGradientFast(permutation[AB+1], fx, fy-1, fz-1);
        float n111 = dotDistanceGradientFast(permutation[BB+1], fx-1, fy-1, fz-1);

        // smoothed value along each axis
        float u = fade(fx);
        float v = fade(fy);
        float w = fade(fz);

        float x1 = lerp(u, n000, n001); // interpolate along edge
        float x2 = lerp(u, n010, n100);
        float x3 = lerp(u, n011, n101);
        float x4 = lerp(u, n110, n111);
        float y1 = lerp(v, x1, x2);
        float y2 = lerp(v, x3, x4);
        float res = lerp(w, y1, y2);
        return res;
    }

//    public float[][] generatePerlinMap (int width, int height, float xoffset, float yoffset, float PerlinScale) {
//        float[][] noise = new float[width][height];
//        float scale = PerlinScale/(float)(width);      // multiplier so that a row corresponds to the same nr of Perlin grid points regardless of LOD scale
//
//        for (int y = 0; y < height; y++) {
//            for (int x = 0; x < width; x++) {
//                noise[x][y] = PerlinNoise(xoffset+(float)x*scale, yoffset+(float)y*scale);
//            }
//        }
//
//        // normalize to [0-1]
//        // use fixed values to avoid seams between chunks
//        float min = 100f;
//        float max =  -100f;
//        for (int y = 0; y < height; y++) {
//            for (int x = 0; x < width; x++) {
//                if(noise[x][y] > max)
//                    max = noise[x][y];
//                if(noise[x][y] < min)
//                    min = noise[x][y];
//                //noise[x][y] = (noise[x][y]-min)/(max - min);
//            }
//        }
////        min = -1f;
////        max = 1f;
//        for (int y = 0; y < height; y++) {
//            for (int x = 0; x < width; x++) {
//
//                noise[x][y] = (noise[x][y]-min)/(max - min);
//            }
//        }
//        return noise;
//    }
//


    public float noise(final float x, final float y, final float z, NoiseSettings settings) {
        float frequency = 1;
        float amplitude = 1.0f;
        float totalAmplitude = 0;
        float noiseLevel = 0;

        for (int octave = 0; octave < settings.octaves; octave++) {

            final float sampleX = x*frequency;
            final float sampleY = y*frequency;
            final float sampleZ = z*frequency;
            float perlinValue = PerlinNoise3d(sampleX, sampleY, sampleZ) * 0.5f + 0.5f;    // scale to [0-1]
            totalAmplitude += amplitude;
            if(settings.takeAbs)
                perlinValue = Math.abs(perlinValue);
            if(settings.complement)
                perlinValue = 1.0f - perlinValue;
            noiseLevel += perlinValue * amplitude;
            amplitude *= settings.gain;
            frequency *= settings.lacunarity;
        }
        noiseLevel /= totalAmplitude;   // to scale back down to [0-1]

        // use exponent to flatten low parts (noiseLevel has to be in [0-1])
        return (float) Math.pow(noiseLevel, settings.exponent);
    }




    // generate a volume from 3d noise
    //
    // width is in x and z dimension
    // height is for y dimension
    // values are 0-255 for density levels
    //
    public char[][][] makeVolume(final int width, final int height, NoiseSettings settings) {
        final float scale = settings.PerlinScale / (float) (width);
        float scaleY = settings.PerlinScale  / (float) (height);
        scaleY *= (float) Chunk.CHUNK_HEIGHT / (float)Chunk.CHUNK_WIDTH;
        float chunkY = height * settings.yoffset / settings.PerlinScale;

        char[][][] volumeMap = new char[height][width][width];
        for (int y = 0; y < height; y++) {      // 0 at the bottom

            // density varies with height so that the top is mostly air and the
            // bottom is mostly solid.
           // float targetDensity = 0.6f*(settings.yoffset + (height-y) *scaleY)/(settings.PerlinScale);          // tweak this
//            float targetDensity = 0.6f*(settings.yoffset + (height-y) *scaleY)/(settings.PerlinScale);          // tweak this
            float targetDensity = 1f*(float)(chunkY + ((float)(height-y)/(float)(height)));


            for (int x = 0; x < width; x++) {
                for (int z = 0; z < width; z++) {

                    float f = noise((float) x * scale + settings.xoffset,
                            (float) y * scaleY + settings.yoffset,
                            (float) z * scale + settings.zoffset, settings);

                    f *= targetDensity;                 // todo
                    f = MathUtils.clamp(f, 0f, 1f);

                    char density = (char)(255*f);

//                    density = 255;
//                    if(y > 16)
//                        density = 0;

                    volumeMap[y][x][z] = density;     // solid vs. air
                }
            }
        }

        return volumeMap;
    }

}
