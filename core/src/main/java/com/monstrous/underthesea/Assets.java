package com.monstrous.underthesea;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g3d.particles.ParticleEffect;
import com.badlogic.gdx.graphics.g3d.particles.ParticleEffectLoader;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.utils.Disposable;
import net.mgsx.gltf.loaders.gltf.GLTFAssetLoader;
import net.mgsx.gltf.scene3d.scene.SceneAsset;

public class Assets implements Disposable {

    public AssetManager assets;

    public Assets() {
        Gdx.app.log("Assets constructor", "");
        assets = new AssetManager();

        assets.setLoader(SceneAsset.class, ".gltf", new GLTFAssetLoader());

        assets.load("models/submarine.gltf", SceneAsset.class);
        assets.load("models/AnthroBanana.gltf", SceneAsset.class);

        assets.load("blue-pixel-skin/blue-pixel.json", Skin.class);
        assets.load("Particle Park UI Skin/Particle Park UI.json", Skin.class);

        assets.load("sounds/sonar-ping.wav", Sound.class);
        assets.load("sounds/crash.mp3", Sound.class);
        assets.load("sounds/game-level-complete-143022.mp3", Sound.class);
        assets.load("sounds/success-fanfare-trumpets-6185.mp3", Sound.class);
        assets.load("sounds/crowd-cheer-ii-6263.mp3", Sound.class);
        assets.load("sounds/click-for-game-menu.mp3", Sound.class);
        assets.load("sounds/morse-code.mp3", Sound.class);

        assets.load("images/generating.png", Texture.class);


        //        assets.load(Settings.gameMusic, Music.class);
    }

    public boolean update() {
        return assets.update();
    }

    public <T> T get(String name ) {
        return assets.get(name);
    }


    @Override
    public void dispose() {
        Gdx.app.log("Assets dispose()", "");
        assets.dispose();
        assets = null;
    }
}
