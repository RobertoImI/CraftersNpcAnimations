package org.crafterscr.craftersnpcanimations.animation.engine;

import net.minecraft.client.model.geom.ModelPart;
import org.crafterscr.craftersnpcanimations.animation.emote.NpcEmote;
import org.crafterscr.craftersnpcanimations.animation.emote.NpcEmoteBoneTrack;
import org.crafterscr.craftersnpcanimations.animation.emote.NpcEmoteInterpolator;
import org.crafterscr.craftersnpcanimations.animation.emote.NpcEmoteTransform;

public final class NpcAnimationEngine {

    private NpcAnimationEngine() {
    }

    public static void applyBone(
            NpcEmote emote,
            String boneName,
            ModelPart modelPart,
            float animationTick
    ) {

        if (emote == null || modelPart == null) {
            return;
        }

        NpcEmoteBoneTrack track =
                emote.bone(boneName);

        if (track == null) {
            return;
        }

        NpcEmoteTransform transform =
                NpcEmoteInterpolator.sample(
                        track,
                        animationTick
                );

        applyTransform(
                modelPart,
                track,
                transform,
                emote.degrees()
        );
    }

    private static void applyTransform(
            ModelPart part,
            NpcEmoteBoneTrack track,
            NpcEmoteTransform transform,
            boolean degrees
    ) {

        /*
         * ==========================================
         * POSICIÓN
         * ==========================================
         *
         * IMPORTANTE:
         *
         * Los archivos Emotecraft que estamos
         * utilizando ya contienen las posiciones
         * del pivote del PlayerModel.
         *
         * Ejemplos:
         *
         * rightArm.x ≈ -5
         * leftArm.x  ≈  5
         *
         * rightLeg.x ≈ -1.9
         * leftLeg.x  ≈  1.9
         *
         * legs.y     ≈ 12
         *
         * Por eso NO debemos sumar initialPose.
         */

        if (track.has(
                NpcEmoteBoneTrack.Channel.X
        )) {

            part.x =
                    transform.x();
        }

        if (track.has(
                NpcEmoteBoneTrack.Channel.Y
        )) {

            part.y =
                    transform.y();
        }

        if (track.has(
                NpcEmoteBoneTrack.Channel.Z
        )) {

            part.z =
                    transform.z();
        }

        /*
         * ==========================================
         * ROTACIÓN
         * ==========================================
         */

        float pitch =
                transform.pitch();

        float yaw =
                transform.yaw();

        float roll =
                transform.roll();

        /*
         * Algunos archivos pueden almacenar
         * rotaciones en grados.
         *
         * SPE_Lemonade utiliza:
         *
         * degrees = false
         *
         * por lo que ya vienen preparadas.
         */
        if (degrees) {

            pitch =
                    (float) Math.toRadians(
                            pitch
                    );

            yaw =
                    (float) Math.toRadians(
                            yaw
                    );

            roll =
                    (float) Math.toRadians(
                            roll
                    );
        }

        /*
         * Solo modificamos un canal cuando
         * realmente existe en el archivo.
         *
         * Esto será importante para emotes que,
         * por ejemplo, no controlen la cabeza.
         */

        if (track.has(
                NpcEmoteBoneTrack.Channel.PITCH
        )) {

            part.xRot =
                    pitch;
        }

        if (track.has(
                NpcEmoteBoneTrack.Channel.YAW
        )) {

            part.yRot =
                    yaw;
        }

        if (track.has(
                NpcEmoteBoneTrack.Channel.ROLL
        )) {

            part.zRot =
                    roll;
        }

        /*
         * ==========================================
         * BEND
         * ==========================================
         *
         * Ya podemos obtener:
         *
         * transform.bend()
         *
         * pero todavía NO lo aplicamos.
         *
         * Este será nuestro siguiente sistema:
         *
         * brazo → codo → antebrazo
         * pierna → rodilla → pantorrilla
         */
    }
}