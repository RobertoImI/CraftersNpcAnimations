package org.crafterscr.craftersnpcanimations.animation.emote;

import java.util.Locale;

public enum NpcEmoteBone {

    HEAD("head"),
    TORSO("torso"),

    RIGHT_ARM("rightArm"),
    LEFT_ARM("leftArm"),

    RIGHT_LEG("rightLeg"),
    LEFT_LEG("leftLeg");

    private final String jsonName;

    NpcEmoteBone(String jsonName) {
        this.jsonName = jsonName;
    }

    public String jsonName() {
        return jsonName;
    }

    public static NpcEmoteBone fromJsonName(
            String name
    ) {

        if (name == null) {
            return null;
        }

        String normalized =
                name.trim()
                        .toLowerCase(Locale.ROOT);

        for (NpcEmoteBone bone : values()) {

            if (bone.jsonName
                    .toLowerCase(Locale.ROOT)
                    .equals(normalized)) {

                return bone;
            }
        }

        return null;
    }
}