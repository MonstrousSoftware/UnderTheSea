package com.monstrous.underthesea;

import com.badlogic.gdx.graphics.Color;

public class Settings {

    // lighting
    static public float ambientLightLevel = 0.0f;
    static public Color backgroundColour = new Color(0.4f, 0.9f, 0.9f, 1f);

    static public float titleScreenTime = 2f;
    static public boolean enableParticleEffects = true;
    static public boolean collisionCheat = false;

    static public String preferencesName = "underthesea";

    static public int numberOfCapsules = 4;    // nr of capsules

    static public String[] radioMessages = {
        "RADIO BROADCAST:\n\tSUBCOM TO GX-25.\n\tPROCEED TO PICK UP\n\tCANISTER IN YOUR\n\tIMMEDIATE VICINITY.",
        "RADIO BROADCAST:\n\tHIGH COMMAND TO \n\tTHE YELLOW SUBMARINE.\n\tSURFACE IMMEDIATELY\n\tTO MAKE THE RENDEZ-VOUS\n\tWITH FRIENDLY WARSHIPS.\n\t",
    };

    static public String[] capsuleMessages = {
        "MESSAGE FROM CANISTER #1\n\tSUBCOM TO GX-25.\n\tALL RADIO COMMUNICATIONS\n\tARE COMPROMISED.\n\tMAINTAIN RADIO SILENCE\n\tAND IGNORE FURTHER RADIO MESSAGES.\n\tFURTHER INSTRUCTIONS\n\tEXCLUSIVELY BY\n\tMESSAGE CANISTER.\n\tPROCEED TO NEXT CANISTER.",
        "MESSAGE FROM CANISTER #2\n\tSUBCOM TO GX-25.\n\tPROCEED TO NEXT CANISTER.\n\tMAINTAIN RADIO SILENCE.",
        "MESSAGE FROM CANISTER #3\n\tSUBCOM TO GX-25.\n\tSITUATION IS ESCALATING\n\tLAUNCH ALL NUCLEAR MISSILES\n\tTO DESIGNATED TARGETS.\n\tJUST KIDDING.\n\tPROCEED TO NEXT CANISTER.",
        "MESSAGE FROM CANISTER #4\n\tSUBCOM TO GX-25.\n\tEND OF EXERCISE.\n\tMISSION COMPLETED.\n\tRADIO COMMUNICATIONS\n\tARE FULLY RESTORED.\n\tCONGRATS BANANA MAN.",
    };


}
