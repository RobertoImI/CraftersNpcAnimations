package org.crafterscr.craftersnpcanimations.client;

import net.minecraft.client.model.PlayerModel;
import net.minecraft.client.model.geom.ModelPart;
import org.crafterscr.craftersnpcanimations.animation.emote.NpcEmote;
import org.crafterscr.craftersnpcanimations.animation.emote.NpcEmotePlayback;
import org.crafterscr.craftersnpcanimations.animation.emote.NpcEmoteRegistry;
import org.crafterscr.craftersnpcanimations.animation.engine.NpcAnimationEngine;
import org.crafterscr.craftersnpcanimations.entity.AnimatedNpcEntity;

import org.crafterscr.craftersnpcanimations.animation.bend.NpcBendController;
import org.crafterscr.craftersnpcanimations.animation.bend.NpcBendPose;

public class AnimatedNpcModel
        extends PlayerModel<AnimatedNpcEntity> {

    private NpcBendPose currentBendPose =
            new NpcBendPose();

    public AnimatedNpcModel(
            ModelPart root,
            boolean slim
    ) {

        super(
                root,
                slim
        );
    }

    @Override
    public void setupAnim(
            AnimatedNpcEntity entity,
            float limbSwing,
            float limbSwingAmount,
            float ageInTicks,
            float netHeadYaw,
            float headPitch
    ) {

        /*
         * ==============================
         * RESET
         * ==============================
         */

        resetAnimatedParts();

        /*
         * Vanilla calcula:
         *
         * caminar
         * cabeza
         * brazos
         * piernas
         */
        super.setupAnim(
                entity,
                limbSwing,
                limbSwingAmount,
                ageInTicks,
                netHeadYaw,
                headPitch
        );

        /*
         * Sin emote:
         *
         * solamente Vanilla.
         */
        if (!entity.isAnimationPlaying()) {

            currentBendPose.reset();

            return;
        }

        /*
         * ==============================
         * BUSCAR EMOTE
         * ==============================
         */

        NpcEmote emote =
                NpcEmoteRegistry.get(
                        entity.getAnimationId()
                );

        /*
         * Mantener temporalmente compatibilidad
         * con las animaciones antiguas.
         */
        if (emote == null) {

            currentBendPose.reset();

            return;
        }

        /*
         * ==============================
         * TIEMPO
         * ==============================
         */

        long gameTime =
                entity.level()
                        .getGameTime();

        long startedAt =
                entity.getAnimationStart();

        /*
         * ageInTicks contiene:
         *
         * entity.tickCount + partialTicks
         *
         * Recuperamos la fracción para que
         * no se vea a 20 FPS.
         */
        float partialTick =
                ageInTicks
                        - entity.tickCount;

        float elapsedTicks =
                (gameTime - startedAt)
                        + partialTick;

        if (elapsedTicks < 0.0F) {
            elapsedTicks = 0.0F;
        }

        float animationTick =
                NpcEmotePlayback
                        .calculateAnimationTick(
                                emote,
                                elapsedTicks,
                                entity.isAnimationLooping()
                        );

        currentBendPose =
                NpcBendController.sample(
                        emote,
                        animationTick
                );

        /*
         * ==============================
         * APLICAR EMOTE
         * ==============================
         */

        applyEmote(
                emote,
                animationTick
        );

        /*
         * ==============================
         * SEGUNDA CAPA
         * ==============================
         *
         * La ropa debe seguir al cuerpo.
         */

        hat.copyFrom(
                head
        );

        jacket.copyFrom(
                body
        );

        rightSleeve.copyFrom(
                rightArm
        );

        leftSleeve.copyFrom(
                leftArm
        );

        rightPants.copyFrom(
                rightLeg
        );

        leftPants.copyFrom(
                leftLeg
        );
    }

    private void applyEmote(
            NpcEmote emote,
            float tick
    ) {

        /*
         * HEAD
         */
        NpcAnimationEngine.applyBone(
                emote,
                "head",
                head,
                tick
        );

        /*
         * TORSO
         */
        NpcAnimationEngine.applyBone(
                emote,
                "torso",
                body,
                tick
        );

        /*
         * BRAZOS
         */
        NpcAnimationEngine.applyBone(
                emote,
                "rightArm",
                rightArm,
                tick
        );

        NpcAnimationEngine.applyBone(
                emote,
                "leftArm",
                leftArm,
                tick
        );

        /*
         * PIERNAS
         */
        NpcAnimationEngine.applyBone(
                emote,
                "rightLeg",
                rightLeg,
                tick
        );

        NpcAnimationEngine.applyBone(
                emote,
                "leftLeg",
                leftLeg,
                tick
        );
    }

    /*
     * ==============================
     * RESET
     * ==============================
     */

    private void resetAnimatedParts() {

        head.resetPose();
        hat.resetPose();

        body.resetPose();
        jacket.resetPose();

        rightArm.resetPose();
        rightSleeve.resetPose();

        leftArm.resetPose();
        leftSleeve.resetPose();

        rightLeg.resetPose();
        rightPants.resetPose();

        leftLeg.resetPose();
        leftPants.resetPose();
    }

    public float getRightArmBend() {
        return currentBendPose.rightArm();
    }

    public float getLeftArmBend() {
        return currentBendPose.leftArm();
    }

    public float getRightLegBend() {
        return currentBendPose.rightLeg();
    }

    public float getLeftLegBend() {
        return currentBendPose.leftLeg();
    }
}