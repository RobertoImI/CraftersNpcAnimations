package org.crafterscr.craftersnpcanimations.client;

import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.HumanoidMobRenderer;
import net.minecraft.resources.ResourceLocation;
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
                                ModelLayers.PLAYER
                        ),
                        false
                ),
                0.5F
        );

        wideModel = getModel();

        slimModel =
                new AnimatedNpcModel(
                        context.bakeLayer(
                                ModelLayers.PLAYER_SLIM
                        ),
                        true
                );
    }

    @Override
    public void render(
            AnimatedNpcEntity entity,
            float entityYaw,
            float partialTicks,
            com.mojang.blaze3d.vertex.PoseStack poseStack,
            net.minecraft.client.renderer.MultiBufferSource buffer,
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

    @Override
    public ResourceLocation getTextureLocation(
            AnimatedNpcEntity entity
    ) {

        return NpcSkinManager.getSkin(
                entity
        );
    }
}