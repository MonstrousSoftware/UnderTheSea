package com.monstrous.underthesea;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Disposable;

public class Sounds implements Disposable  {

    // use constants to identify sound effects
    public static int SONAR_PING = 0;
    public static int CRASH = 1;
    public static int BONUS = 2;
    public static int FANFARE = 3;
    public static int CHEER = 4;
    public static int MENU_CLICK = 5;
    public static int MORSE = 6;

    private static Array<Sound> sounds;
    private final Preferences preferences;
    public static float soundVolume;


    public Sounds(Assets assets) {
        sounds = new Array<>();

        // must be in line with constants defined above
        sounds.add( assets.get("sounds/sonar-ping.wav"));
        sounds.add( assets.get("sounds/crash.mp3"));
        sounds.add( assets.get("sounds/game-level-complete-143022.mp3"));
        sounds.add( assets.get("sounds/success-fanfare-trumpets-6185.mp3"));
        sounds.add( assets.get("sounds/crowd-cheer-ii-6263.mp3"));
        sounds.add( assets.get("sounds/click-for-game-menu.mp3"));
        sounds.add( assets.get("sounds/morse-code.mp3"));

        preferences = Gdx.app.getPreferences(Settings.preferencesName);
        soundVolume = preferences.getFloat("soundVolume", 1.0f);
    }

    public static Sound playSound(int code) {
        Sound s = sounds.get(code);
        s.play(soundVolume);
        return s;
    }

    public static long playSoundLoop(int code) {
        Sound s = sounds.get(code);
        long id = s.loop(soundVolume);
        return id;
    }

    public static void stopLoop(int code, long id) {
        Sound s = sounds.get(code);
        s.stop(id);
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
        for(Sound sound : sounds)
            sound.stop();
        sounds.clear();
        // save sound settings for next time
        preferences.putFloat("soundVolume", soundVolume);   // save
        preferences.flush();
    }
}
