package org.crafterscr.craftersnpcanimations.animation.emote;

/**
 * Un valor concreto de una propiedad en un tick.
 *
 * Ejemplo:
 *
 * tick = 38
 * value = -0.567
 * easing = LINEAR
 * turn = 0
 */
public record NpcEmoteKeyframe(
        int tick,
        float value,
        String easing,
        int turn
) {

    public NpcEmoteKeyframe {

        if (easing == null || easing.isBlank()) {
            easing = "LINEAR";
        }
    }
}