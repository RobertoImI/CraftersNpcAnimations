package org.crafterscr.craftersnpcanimations.client;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import dev.kosmx.playerAnim.api.TransformType;
import dev.kosmx.playerAnim.api.layered.KeyframeAnimationPlayer;
import dev.kosmx.playerAnim.core.data.KeyframeAnimation;
import dev.kosmx.playerAnim.core.util.Vec3f;
import dev.kosmx.playerAnim.impl.animation.AnimationApplier;
import net.minecraft.client.model.geom.ModelPart;
import org.crafterscr.craftersnpcanimations.animation.emote.NpcEmote;
import org.crafterscr.craftersnpcanimations.animation.emote.NpcEmoteRegistry;
import org.crafterscr.craftersnpcanimations.entity.AnimatedNpcEntity;

public final class NpcPlayerAnimatorBridge {

    private NpcPlayerAnimatorBridge() {
    }

    /**
     * Crea el estado PlayerAnimator correspondiente al
     * tick actual del NPC.
     *
     * No convertimos al NPC en Player/FakePlayer.
     * Solo reutilizamos el motor de animación original.
     */
    public static AnimationApplier createApplier(
            AnimatedNpcEntity entity,
            float partialTick
    ) {

        if (entity == null
                || !entity.isAnimationPlaying()) {

            return null;
        }

        NpcEmote emote =
                NpcEmoteRegistry.get(
                        entity.getAnimationId()
                );

        if (emote == null
                || emote.playerAnimation() == null) {

            return null;
        }

        KeyframeAnimation animation =
                resolveAnimation(
                        emote,
                        entity.isAnimationLooping()
                );

        long elapsed =
                entity.level()
                        .getGameTime()
                        - entity.getAnimationStart();

        if (elapsed < 0L) {
            elapsed = 0L;
        }

        /*
         * Para animaciones no-loop dejamos de renderizar
         * la pose al alcanzar stopTick, igual que el
         * reproductor original.
         */
        if (!animation.isInfinite
                && elapsed >= animation.stopTick) {

            return null;
        }

        int animationTick =
                (int) Math.min(
                        elapsed,
                        Integer.MAX_VALUE - 1L
                );

        KeyframeAnimationPlayer player =
                new KeyframeAnimationPlayer(
                        animation,
                        animationTick
                );

        AnimationApplier applier =
                new AnimationApplier(
                        player
                );

        applier.setTickDelta(
                clampPartialTick(
                        partialTick
                )
        );

        return applier;
    }

    /**
     * Aplica el hueso "body" al PoseStack COMPLETO.
     *
     * Este es el detalle que nuestro motor anterior no
     * estaba haciendo.
     *
     * En los emotes legacy (version 1/2), "torso" se
     * convierte en "body" y representa el ROOT del
     * personaje, no el ModelPart del pecho.
     *
     * PlayerAnimator lo aplica al render entero y por eso
     * brazos, cabeza y piernas permanecen unidos.
     */
    public static void applyBodyTransform(
            AnimatedNpcEntity entity,
            float partialTick,
            PoseStack poseStack
    ) {

        AnimationApplier applier =
                createApplier(
                        entity,
                        partialTick
                );

        if (applier == null) {
            return;
        }

        Vec3f scale =
                applier.get3DTransform(
                        "body",
                        TransformType.SCALE,
                        new Vec3f(
                                ModelPart.DEFAULT_SCALE,
                                ModelPart.DEFAULT_SCALE,
                                ModelPart.DEFAULT_SCALE
                        )
                );

        poseStack.scale(
                scale.getX(),
                scale.getY(),
                scale.getZ()
        );

        Vec3f position =
                applier.get3DTransform(
                        "body",
                        TransformType.POSITION,
                        Vec3f.ZERO
                );

        /*
         * 0.7 es el mismo pivote utilizado por
         * PlayerAnimator 1.21.1 en PlayerRenderer.
         */
        poseStack.translate(
                position.getX(),
                position.getY() + 0.7D,
                position.getZ()
        );

        Vec3f rotation =
                applier.get3DTransform(
                        "body",
                        TransformType.ROTATION,
                        Vec3f.ZERO
                );

        /*
         * Mismo orden de rotación que PlayerAnimator:
         *
         * Z -> Y -> X
         */
        poseStack.mulPose(
                Axis.ZP.rotation(
                        rotation.getZ()
                )
        );

        poseStack.mulPose(
                Axis.YP.rotation(
                        rotation.getY()
                )
        );

        poseStack.mulPose(
                Axis.XP.rotation(
                        rotation.getX()
                )
        );

        poseStack.translate(
                0.0D,
                -0.7D,
                0.0D
        );
    }

    /**
     * Mantiene el comportamiento del comando:
     *
     * /cnpca emote <npc> <emote> loop
     *
     * aunque el archivo original no sea loop.
     */
    private static KeyframeAnimation resolveAnimation(
            NpcEmote emote,
            boolean forceLoop
    ) {

        KeyframeAnimation base =
                emote.playerAnimation();

        if (!forceLoop
                || base.isInfinite) {

            return base;
        }

        KeyframeAnimation.AnimationBuilder builder =
                base.mutableCopy();

        builder.isLooped = true;

        int returnTick =
                emote.returnTick();

        if (returnTick < 0
                || returnTick > base.endTick) {

            returnTick =
                    Math.max(
                            0,
                            base.beginTick
                    );
        }

        builder.returnTick =
                returnTick;

        return builder.build();
    }

    private static float clampPartialTick(
            float partialTick
    ) {

        if (partialTick < 0.0F) {
            return 0.0F;
        }

        if (partialTick > 1.0F) {
            return 1.0F;
        }

        return partialTick;
    }
}
