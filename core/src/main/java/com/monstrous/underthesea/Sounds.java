package com.monstrous.underthesea;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Disposable;

public class Sounds implements Disposable  {

    // use constants to identify sound effects
    public static int SONAR_PING = 0;

    private static Array<Sound> sounds;
    private final Preferences preferences;
    public static float soundVolume;


    public Sounds(Assets assets) {
        sounds = new Array<>();

        // must be in line with constants defined above
        sounds.add( assets.get("sounds/sonar-ping.wav"));

        preferences = Gdx.app.getPreferences(Settings.preferencesName);
        soundVolume = preferences.getFloat("soundVolume", 1.0f);
    }

    public static Sound playSound(int code) {
        Sound s = sounds.get(code);
        s.play(soundVolume);
        return s;
    }

    public static Sound playSoundLoop(int code) {
        Sound s = sounds.get(code);
        s.loop(soundVolume);
        return s;
    }

    public static void stopSound(int code) {
        sounds.get(code).stop();
    }

    public static float getSoundVolume() {
        return soundVolume;
    }

    public static void setSoundVolume(float vol) {
        soundVolume = vol;
    }

    @Override
    public void dispose() {
        sounds.clear();
        // save sound settings for next time
        preferences.putFloat("soundVolume", soundVolume);   // save
        preferences.flush();
    }
}
