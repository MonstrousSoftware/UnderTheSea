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
        "SUBCOM TO X-25.\n\tPROCEED TO PICK UP CANISTER IN YOUR IMMEDIATE VICINITY.",
        "HIGH COMMAND TO THE YELLOW SUBMARINE.\n\tSURFACE IMMEDIATELY TO MAKE THE RENDEZ-VOUS WITH FRIENDLY WARSHIPS.",
    };

    static public String[] capsuleMessages = {
        "MESSAGE #1\n\tSUBCOM TO X-25.\n\tALL RADIO COMMUNICATIONS ARE COMPROMISED. MAINTAIN RADIO SILENCE AND IGNORE FURTHER RADIO MESSAGES. FURTHER INSTRUCTIONS EXCLUSIVELY BY MESSAGE CANISTER.\n\tPROCEED TO NEXT CANISTER.",
        "MESSAGE #2\n\tSUBCOM TO X-25.\n\tPROCEED TO NEXT CANISTER.\n\tMAINTAIN RADIO SILENCE.",
        "MESSAGE #3\n\tSUBCOM TO X-25.\n\tSITUATION IS ESCALATING.\n\tLAUNCH ALL NUCLEAR MISSILES TO DESIGNATED TARGETS.\n\tJUST KIDDING.\n\tPROCEED TO NEXT CANISTER.",
        "MESSAGE #4\n\tSUBCOM TO X-25.\n\tEND OF EXERCISE.\n\tMISSION COMPLETED.\n\tRADIO COMMUNICATIONS ARE FULLY RESTORED.\n\tCONGRATS BANANA MAN.",
    };


}
