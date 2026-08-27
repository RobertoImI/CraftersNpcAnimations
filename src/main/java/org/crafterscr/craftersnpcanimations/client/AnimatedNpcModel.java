package org.crafterscr.craftersnpcanimations.client;

import net.minecraft.client.model.PlayerModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.util.Mth;
import org.crafterscr.craftersnpcanimations.entity.AnimatedNpcEntity;

public class AnimatedNpcModel
        extends PlayerModel<AnimatedNpcEntity> {

    public AnimatedNpcModel(
            ModelPart root,
            boolean slim
    ) {
        super(root, slim);
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
         * MUY IMPORTANTE:
         *
         * Limpiamos cualquier transformación dejada
         * por el emote del frame anterior.
         *
         * Después Vanilla vuelve a calcular caminar,
         * mirar, brazos, piernas, etc.
         */
        resetAnimatedParts();

        super.setupAnim(
                entity,
                limbSwing,
                limbSwingAmount,
                ageInTicks,
                netHeadYaw,
                headPitch
        );

        /*
         * Si no hay emote activo dejamos solamente
         * la animación Vanilla calculada arriba.
         */
        if (!entity.isAnimationPlaying()) {
            return;
        }

        String animation =
                entity.getAnimationId();

        switch (animation) {

            case "wave" ->
                    applyWave(ageInTicks);

            case "sit" ->
                    applySit();

            case "dance" ->
                    applyDance(ageInTicks);

            default -> {
                // Emote desconocido.
                // Simplemente conserva Vanilla.
            }
        }
    }

    /*
     * ==============================
     * RESET
     * ==============================
     *
     * Evita que una transformación de un emote
     * permanezca después de detenerlo.
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

    /*
     * ==============================
     * WAVE
     * ==============================
     */

    private void applyWave(float age) {

        float wave =
                Mth.sin(age * 0.35F) * 0.35F;

        rightArm.xRot =
                -2.7F;

        rightArm.zRot =
                0.35F + wave;

        rightSleeve.copyFrom(
                rightArm
        );
    }

    /*
     * ==============================
     * SIT
     * ==============================
     */

    private void applySit() {

        body.xRot = 0.0F;
        body.yRot = 0.0F;
        body.zRot = 0.0F;

        rightLeg.xRot =
                -1.45F;

        leftLeg.xRot =
                -1.45F;

        rightLeg.yRot =
                0.15F;

        leftLeg.yRot =
                -0.15F;

        rightPants.copyFrom(
                rightLeg
        );

        leftPants.copyFrom(
                leftLeg
        );

        rightArm.xRot =
                -0.25F;

        leftArm.xRot =
                -0.25F;

        rightSleeve.copyFrom(
                rightArm
        );

        leftSleeve.copyFrom(
                leftArm
        );

        jacket.copyFrom(
                body
        );
    }

    /*
     * ==============================
     * DANCE
     * ==============================
     */

    private void applyDance(float age) {

        float movement =
                Mth.sin(age * 0.25F);

        float opposite =
                Mth.cos(age * 0.25F);

        body.zRot =
                movement * 0.15F;

        rightArm.zRot =
                1.3F
                        + movement * 0.4F;

        leftArm.zRot =
                -1.3F
                        - movement * 0.4F;

        rightLeg.xRot =
                opposite * 0.4F;

        leftLeg.xRot =
                -opposite * 0.4F;

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

        jacket.copyFrom(
                body
        );
    }
}