package org.crafterscr.craftersnpcanimations.animation;

import java.util.Locale;

public enum NpcAnimationType {

    NONE("none"),
    WAVE("wave"),
    SIT("sit"),
    DANCE("dance");

    private final String id;

    NpcAnimationType(String id) {
        this.id = id;
    }

    public String id() {
        return id;
    }

    public static NpcAnimationType fromId(String value) {

        if (value == null || value.isBlank()) {
            return NONE;
        }

        String normalized = value.toLowerCase(Locale.ROOT);

        for (NpcAnimationType type : values()) {
            if (type.id.equals(normalized)) {
                return type;
            }
        }

        return NONE;
    }
}