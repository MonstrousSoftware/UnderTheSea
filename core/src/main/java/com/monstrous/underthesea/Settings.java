package com.monstrous.underthesea;

import com.badlogic.gdx.graphics.Color;

public class Settings {

    // lighting
    static public float ambientLightLevel = 0.2f;
    static public Color backgroundColour = new Color(0.4f, 0.9f, 0.9f, 1f);

    static public float titleScreenTime = 2f;
    static public boolean enableParticleEffects = true;
    static public boolean collisionCheat = false;

    static public String preferencesName = "underthesea";

    static public int numberOfCapsules = 4;    // nr of capsules

    static public String[] radioMessages = {
        "SUBCOM TO X-25:\nPROCEED TO PICK UP CANISTER IN YOUR IMMEDIATE VICINITY.",
        "HIGH COMMAND TO THE YELLOW SUBMARINE.\nSURFACE IMMEDIATELY TO MAKE THE RENDEZ-VOUS WITH FRIENDLY WARSHIPS.",
    };

    static public String[] capsuleMessages = {
        "MESSAGE #1\nSUBCOM TO X-25:\nALL RADIO COMMUNICATIONS ARE COMPROMISED. MAINTAIN RADIO SILENCE AND IGNORE FURTHER RADIO MESSAGES. FURTHER INSTRUCTIONS EXCLUSIVELY BY MESSAGE CANISTER.\n\tPROCEED TO NEXT CANISTER.",
        "MESSAGE #2\nSUBCOM TO X-25:\nPROCEED TO NEXT CANISTER.\nMAINTAIN RADIO SILENCE.",
        "MESSAGE #3\nSUBCOM TO X-25:\nSITUATION IS ESCALATING.\nLAUNCH ALL NUCLEAR MISSILES TO DESIGNATED TARGETS.\nJUST KIDDING.\nPROCEED TO NEXT CANISTER.",
        "MESSAGE #4\nSUBCOM TO X-25:\nEND OF EXERCISE.\nMISSION COMPLETED.\nRADIO COMMUNICATIONS ARE FULLY RESTORED.\nCONGRATS BANANA MAN.",
    };


}
