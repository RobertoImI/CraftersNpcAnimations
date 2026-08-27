package org.crafterscr.craftersnpcanimations.skin;

import net.minecraft.resources.ResourceLocation;
import org.crafterscr.craftersnpcanimations.entity.AnimatedNpcEntity;

public final class NpcSkinManager {

    private static final ResourceLocation DEFAULT_SKIN =
            ResourceLocation.withDefaultNamespace(
                    "textures/entity/player/wide/steve.png"
            );

    private NpcSkinManager() {
    }

    public static ResourceLocation getSkin(
            AnimatedNpcEntity npc
    ) {

        /*
         * FASE 2:
         *
         * Aquí agregaremos:
         *
         * URL
         * ↓
         * download async
         * ↓
         * DynamicTexture
         * ↓
         * cache
         */

        return DEFAULT_SKIN;
    }
}