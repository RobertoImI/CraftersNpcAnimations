package org.crafterscr.craftersnpcanimations.client;

import dev.kosmx.playerAnim.core.impl.AnimationProcessor;
import dev.kosmx.playerAnim.core.util.SetableSupplier;
import dev.kosmx.playerAnim.impl.IMutableModel;
import dev.kosmx.playerAnim.impl.animation.AnimationApplier;
import net.minecraft.client.model.PlayerModel;
import net.minecraft.client.model.geom.ModelPart;
import org.crafterscr.craftersnpcanimations.entity.AnimatedNpcEntity;

public class AnimatedNpcModel
        extends PlayerModel<AnimatedNpcEntity> {

    /*
     * PlayerAnimator utiliza este supplier durante el
     * render para aplicar correctamente el bend del body
     * a las partes superiores del modelo.
     */
    private final SetableSupplier<AnimationProcessor>
            npcAnimationSupplier =
            new SetableSupplier<>();

    public AnimatedNpcModel(
            ModelPart root,
            boolean slim
    ) {

        super(
                root,
                slim
        );

        /*
         * HumanoidModel recibe IMutableModel mediante
         * el Mixin de PlayerAnimator.
         *
         * Sustituimos el supplier de jugador por uno
         * controlado por nuestro NPC.
         */
        ((IMutableModel) this)
                .setEmoteSupplier(
                        npcAnimationSupplier
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
         * Primero Vanilla.
         *
         * Esto es importante porque PlayerAnimator utiliza
         * el estado Vanilla como valor base para los canales
         * que el emote no controla.
         */
        super.setupAnim(
                entity,
                limbSwing,
                limbSwingAmount,
                ageInTicks,
                netHeadYaw,
                headPitch
        );

        float partialTick =
                ageInTicks
                        - entity.tickCount;

        AnimationApplier applier =
                NpcPlayerAnimatorBridge
                        .createApplier(
                                entity,
                                partialTick
                        );

        if (applier == null) {

            npcAnimationSupplier.set(
                    null
            );

            return;
        }

        /*
         * Permite que el sistema de render de
         * PlayerAnimator/BendyLib conozca el bend "body".
         */
        npcAnimationSupplier.set(
                applier
        );

        /*
         * ==========================================
         * APLICACIÓN EXACTA DE PLAYERANIMATOR
         * ==========================================
         *
         * NO aplicamos "body" al ModelPart body.
         *
         * "body" es el ROOT global y se aplica desde
         * AnimatedNpcRenderer al PoseStack completo.
         *
         * "torso" sí representa el pecho en formatos
         * modernos.
         *
         * En SPE_Lemonade, al no existir "version",
         * PlayerAnimator lo interpreta como legacy y
         * convierte el antiguo "torso" en "body".
         */

        applier.updatePart(
                "head",
                head
        );

        applier.updatePart(
                "leftArm",
                leftArm
        );

        applier.updatePart(
                "rightArm",
                rightArm
        );

        applier.updatePart(
                "leftLeg",
                leftLeg
        );

        applier.updatePart(
                "rightLeg",
                rightLeg
        );

        applier.updatePart(
                "torso",
                body
        );

        /*
         * La segunda capa se copia DESPUÉS de aplicar
         * la animación, igual que en PlayerModel.
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
}
