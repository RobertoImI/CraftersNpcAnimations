package org.crafterscr.craftersnpcanimations.animation.emote;

import net.minecraft.util.Mth;

import java.util.List;

public final class NpcEmoteInterpolator {

    private NpcEmoteInterpolator() {
    }

    /**
     * Calcula la pose completa de un hueso
     * para un tick decimal.
     *
     * Ejemplo:
     *
     * 35.0
     * 35.25
     * 35.5
     * 35.75
     */
    public static NpcEmoteTransform sample(
            NpcEmoteBoneTrack track,
            float tick
    ) {

        NpcEmoteTransform result =
                new NpcEmoteTransform();

        if (track == null) {
            return result;
        }

        result.setX(
                sampleChannel(
                        track,
                        NpcEmoteBoneTrack.Channel.X,
                        tick
                )
        );

        result.setY(
                sampleChannel(
                        track,
                        NpcEmoteBoneTrack.Channel.Y,
                        tick
                )
        );

        result.setZ(
                sampleChannel(
                        track,
                        NpcEmoteBoneTrack.Channel.Z,
                        tick
                )
        );

        result.setPitch(
                sampleChannel(
                        track,
                        NpcEmoteBoneTrack.Channel.PITCH,
                        tick
                )
        );

        result.setYaw(
                sampleChannel(
                        track,
                        NpcEmoteBoneTrack.Channel.YAW,
                        tick
                )
        );

        result.setRoll(
                sampleChannel(
                        track,
                        NpcEmoteBoneTrack.Channel.ROLL,
                        tick
                )
        );

        result.setBend(
                sampleChannel(
                        track,
                        NpcEmoteBoneTrack.Channel.BEND,
                        tick
                )
        );

        return result;
    }

    /**
     * Busca el keyframe anterior y siguiente
     * de un canal concreto e interpola.
     */
    public static float sampleChannel(
            NpcEmoteBoneTrack track,
            NpcEmoteBoneTrack.Channel channel,
            float tick
    ) {

        List<NpcEmoteKeyframe> frames =
                track.get(channel);

        if (frames.isEmpty()) {
            return 0.0F;
        }

        /*
         * Si estamos antes del primer keyframe,
         * usamos el primer valor.
         */
        if (tick <= frames.getFirst().tick()) {
            return frames.getFirst().value();
        }

        /*
         * Si estamos después del último,
         * mantenemos el último valor.
         */
        if (tick >= frames.getLast().tick()) {
            return frames.getLast().value();
        }

        NpcEmoteKeyframe previous =
                frames.getFirst();

        NpcEmoteKeyframe next =
                frames.getLast();

        for (int i = 1; i < frames.size(); i++) {

            NpcEmoteKeyframe candidate =
                    frames.get(i);

            if (candidate.tick() >= tick) {

                next = candidate;
                previous = frames.get(i - 1);

                break;
            }
        }

        /*
         * Exactamente en un keyframe.
         */
        if (previous.tick() == next.tick()) {
            return next.value();
        }

        float progress =
                (tick - previous.tick())
                        /
                        (float) (
                                next.tick()
                                        - previous.tick()
                        );

        progress =
                Mth.clamp(
                        progress,
                        0.0F,
                        1.0F
                );

        progress =
                applyEasing(
                        progress,
                        next.easing()
                );

        return Mth.lerp(
                progress,
                previous.value(),
                next.value()
        );
    }

    /*
     * ==============================
     * EASING
     * ==============================
     *
     * Lemonade utiliza LINEAR.
     *
     * Dejamos algunos básicos preparados
     * para otros emotes.
     */
    private static float applyEasing(
            float value,
            String easing
    ) {

        if (easing == null) {
            return value;
        }

        return switch (
                easing.toUpperCase()
                ) {

            case "EASE_IN",
                 "EASEIN" ->
                    value * value;

            case "EASE_OUT",
                 "EASEOUT" -> {

                float inverse =
                        1.0F - value;

                yield 1.0F
                        - inverse * inverse;
            }

            case "EASE_IN_OUT",
                 "EASEINOUT" -> {

                if (value < 0.5F) {

                    yield 2.0F
                            * value
                            * value;

                } else {

                    float inverse =
                            -2.0F * value
                                    + 2.0F;

                    yield 1.0F
                            - (
                            inverse
                                    * inverse
                    ) / 2.0F;
                }
            }

            /*
             * LINEAR
             */
            default -> value;
        };
    }
}