package com.monstrous.underthesea;

import com.badlogic.gdx.graphics.Color;

public class Settings {

    // lighting
    static public float ambientLightLevel = 0.0f;
    static public Color backgroundColour = new Color(0.4f, 0.9f, 0.9f, 1f);

    static public float titleScreenTime = 2f;
    static public boolean enableParticleEffects = true;

    static public String preferencesName = "underthesea";

    static public int capsuleNumber = 4;    // nr of capsules

    static public String[] radioMessages = {
        "RADIO BROADCAST:\n\tSUBCOM TO GX-25.\n\tPROCEED TO PICK UP CANISTER IN YOUR IMMEDIATE VICINITY.",
        "RADIO BROADCAST:\n\tHIGH COMMAND TO THE YELLOW SUBMARINE.\n\tSURFACE IMMEDIATELY TO MAKE THE RENDEZ-VOUS WITH FRIENDLY WARSHIPS.\n\t",
    };

    static public String[] capsuleMessages = {
        "MESSAGE FROM CANISTER #1\n\tSUBCOM TO GX-25.\n\tALL RADIO COMMUNICATIONS ARE COMPROMISED.\n\tMAINTAIN RADIO SILENCE AND IGNORE FURTHER RADIO MESSAGES.\n\tFURTHER INSTRUCTIONS EXCLUSIVELY BY MESSAGE CANISTER.\n\tPROCEED TO NEXT CANISTER.",
        "MESSAGE FROM CANISTER #2\n\tSUBCOM TO GX-25.\n\tPROCEED TO NEXT CANISTER.\n\tMAINTAIN RADIO SILENCE.",
        "MESSAGE FROM CANISTER #3\n\tSUBCOM TO GX-25.\n\tSITUATION IS ESCALATING\n\tLAUNCH ALL NUCLEAR MISSILES TO DESIGNATED TARGETS.\n\tJUST KIDDING.\n\tPROCEED TO NEXT CANISTER.",
        "MESSAGE FROM CANISTER #4\n\tSUBCOM TO GX-25.\n\tEND OF EXERCISE.\n\tMISSION COMPLETED.\n\tRADIO COMMUNICATIONS ARE FULLY RESTORED.\n\tCONGRATS BANANA MAN.",
    };


}
