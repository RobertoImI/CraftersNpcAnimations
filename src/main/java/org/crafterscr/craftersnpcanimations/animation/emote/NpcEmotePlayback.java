package org.crafterscr.craftersnpcanimations.animation.emote;

public final class NpcEmotePlayback {

    private NpcEmotePlayback() {
    }

    /**
     * Convierte cuánto tiempo lleva reproduciéndose
     * el NPC al tick interno del emote.
     */
    public static float calculateAnimationTick(
            NpcEmote emote,
            float elapsedTicks,
            boolean forceLoop
    ) {

        if (emote == null) {
            return 0.0F;
        }

        float begin =
                emote.beginTick();

        float end =
                emote.endTick();

        float returnTick =
                emote.returnTick();

        /*
         * Empezamos desde beginTick.
         */
        float timeline =
                begin + elapsedTicks;

        boolean loop =
                forceLoop || emote.loop();

        /*
         * No-loop.
         */
        if (!loop) {

            if (timeline > end) {
                return end;
            }

            return timeline;
        }

        /*
         * Todavía no hemos llegado al final.
         */
        if (timeline <= end) {
            return timeline;
        }

        /*
         * LOOP
         *
         * Al terminar:
         *
         * endTick
         *    ↓
         * returnTick
         */
        float loopLength =
                end - returnTick;

        if (loopLength <= 0.0F) {
            return returnTick;
        }

        float extra =
                timeline - end;

        return returnTick
                + (
                extra % loopLength
        );
    }

    /**
     * Comprueba si una animación NO loop
     * ya terminó.
     */
    public static boolean hasFinished(
            NpcEmote emote,
            float elapsedTicks,
            boolean forceLoop
    ) {

        if (emote == null) {
            return true;
        }

        if (forceLoop || emote.loop()) {
            return false;
        }

        float timeline =
                emote.beginTick()
                        + elapsedTicks;

        return timeline
                >= emote.stopTick();
    }
}