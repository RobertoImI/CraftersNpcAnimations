package org.crafterscr.craftersnpcanimations.client;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.HumanoidMobRenderer;
import net.minecraft.resources.ResourceLocation;
import org.crafterscr.craftersnpcanimations.client.model.AnimatedNpcModelLayers;
import org.crafterscr.craftersnpcanimations.entity.AnimatedNpcEntity;
import org.crafterscr.craftersnpcanimations.skin.NpcSkinManager;

public class AnimatedNpcRenderer
        extends HumanoidMobRenderer<
        AnimatedNpcEntity,
        AnimatedNpcModel
        > {

    private final AnimatedNpcModel wideModel;
    private final AnimatedNpcModel slimModel;

    public AnimatedNpcRenderer(
            EntityRendererProvider.Context context
    ) {

        super(
                context,
                new AnimatedNpcModel(
                        context.bakeLayer(
                                AnimatedNpcModelLayers.WIDE
                        ),
                        false
                ),
                0.5F
        );

        wideModel =
                getModel();

        slimModel =
                new AnimatedNpcModel(
                        context.bakeLayer(
                                AnimatedNpcModelLayers.SLIM
                        ),
                        true
                );
    }

    @Override
    public void render(
            AnimatedNpcEntity entity,
            float entityYaw,
            float partialTicks,
            PoseStack poseStack,
            MultiBufferSource buffer,
            int packedLight
    ) {

        this.model =
                entity.isSlimModel()
                        ? slimModel
                        : wideModel;

        super.render(
                entity,
                entityYaw,
                partialTicks,
                poseStack,
                buffer,
                packedLight
        );
    }

    /**
     * PlayerAnimator aplica el hueso "body" al render
     * completo del jugador desde PlayerRenderer.
     *
     * Nuestro NPC no pasa por PlayerRenderer, por lo que
     * debemos hacer esa misma operación aquí.
     */
    @Override
    protected void setupRotations(
            AnimatedNpcEntity entity,
            PoseStack poseStack,
            float bob,
            float yBodyRot,
            float partialTick,
            float scale
    ) {

        super.setupRotations(
                entity,
                poseStack,
                bob,
                yBodyRot,
                partialTick,
                scale
        );

        NpcPlayerAnimatorBridge
                .applyBodyTransform(
                        entity,
                        partialTick,
                        poseStack
                );
    }

    @Override
    public ResourceLocation getTextureLocation(
            AnimatedNpcEntity entity
    ) {

        return NpcSkinManager.getSkin(
                entity
        );
    }
}
