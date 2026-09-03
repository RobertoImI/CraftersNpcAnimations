package org.crafterscr.craftersnpcanimations.client.model;

import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.resources.ResourceLocation;
import org.crafterscr.craftersnpcanimations.CraftersNpcAnimations;

public final class AnimatedNpcModelLayers {

    public static final ModelLayerLocation WIDE =
            new ModelLayerLocation(
                    ResourceLocation.fromNamespaceAndPath(
                            CraftersNpcAnimations.MOD_ID,
                            "animated_npc"
                    ),
                    "wide"
            );

    public static final ModelLayerLocation SLIM =
            new ModelLayerLocation(
                    ResourceLocation.fromNamespaceAndPath(
                            CraftersNpcAnimations.MOD_ID,
                            "animated_npc"
                    ),
                    "slim"
            );

    private AnimatedNpcModelLayers() {
    }
}