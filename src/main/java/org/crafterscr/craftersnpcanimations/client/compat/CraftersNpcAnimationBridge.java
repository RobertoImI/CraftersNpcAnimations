package org.crafterscr.craftersnpcanimations.client.compat;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;

import dev.kosmx.playerAnim.api.TransformType;
import dev.kosmx.playerAnim.api.layered.KeyframeAnimationPlayer;
import dev.kosmx.playerAnim.core.data.KeyframeAnimation;
import dev.kosmx.playerAnim.core.impl.AnimationProcessor;
import dev.kosmx.playerAnim.core.util.SetableSupplier;
import dev.kosmx.playerAnim.core.util.Vec3f;
import dev.kosmx.playerAnim.impl.IMutableModel;
import dev.kosmx.playerAnim.impl.animation.AnimationApplier;

import net.minecraft.client.model.PlayerModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.world.entity.Entity;

import org.crafterscr.craftersnpcanimations.animation.emote.NpcEmote;
import org.crafterscr.craftersnpcanimations.animation.emote.NpcEmoteRegistry;
import org.crafterscr.craftersnpcanimations.compat.craftersnpc.CraftersNpcCompat;

public final class CraftersNpcAnimationBridge {

    private CraftersNpcAnimationBridge() {
    }

    public static AnimationApplier createApplier(
            Entity entity,
            float partialTick
    ) {

        if (!CraftersNpcCompat.isCnpc(entity)) {
            return null;
        }

        String animationId =
                CraftersNpcCompat.getAnimationId(
                        entity
                );

        if (animationId.isBlank()) {
            return null;
        }

        NpcEmote emote =
                NpcEmoteRegistry.get(
                        animationId
                );

        if (emote == null
                || emote.playerAnimation() == null) {

            return null;
        }

        KeyframeAnimation animation =
                resolveAnimation(
                        emote,
                        CraftersNpcCompat
                                .isAnimationLooping(
                                        entity
                                )
                );

        long elapsed =
                entity.level()
                        .getGameTime()
                        - CraftersNpcCompat
                        .getAnimationStart(
                                entity
                        );

        if (elapsed < 0L) {
            elapsed = 0L;
        }

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

    public static void applyModel(
            Entity entity,
            PlayerModel<?> model,
            float ageInTicks
    ) {

        float partialTick =
                ageInTicks
                        - entity.tickCount;

        AnimationApplier applier =
                createApplier(
                        entity,
                        partialTick
                );

        /*
         * PlayerAnimator convierte HumanoidModel
         * en IMutableModel mediante Mixin.
         */
        IMutableModel mutableModel =
                (IMutableModel) model;

        SetableSupplier<AnimationProcessor> supplier =
                mutableModel.getEmoteSupplier();

        if (applier == null) {

            supplier.set(
                    null
            );

            return;
        }

        supplier.set(
                applier
        );

        /*
         * Exactamente la misma estrategia que ya
         * funciona con AnimatedNpcEntity.
         */
        applier.updatePart(
                "head",
                model.head
        );

        applier.updatePart(
                "leftArm",
                model.leftArm
        );

        applier.updatePart(
                "rightArm",
                model.rightArm
        );

        applier.updatePart(
                "leftLeg",
                model.leftLeg
        );

        applier.updatePart(
                "rightLeg",
                model.rightLeg
        );

        applier.updatePart(
                "torso",
                model.body
        );

        /*
         * Segunda capa.
         */
        model.hat.copyFrom(
                model.head
        );

        model.jacket.copyFrom(
                model.body
        );

        model.rightSleeve.copyFrom(
                model.rightArm
        );

        model.leftSleeve.copyFrom(
                model.leftArm
        );

        model.rightPants.copyFrom(
                model.rightLeg
        );

        model.leftPants.copyFrom(
                model.leftLeg
        );
    }

    public static void applyBodyTransform(
            Entity entity,
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

        builder.isLooped =
                true;

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
            float value
    ) {

        if (value < 0.0F) {
            return 0.0F;
        }

        if (value > 1.0F) {
            return 1.0F;
        }

        return value;
    }
}