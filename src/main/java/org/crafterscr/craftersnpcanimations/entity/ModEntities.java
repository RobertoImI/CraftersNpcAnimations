package org.crafterscr.craftersnpcanimations.entity;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.neoforged.neoforge.registries.DeferredRegister;
import org.crafterscr.craftersnpcanimations.CraftersNpcAnimations;

import java.util.function.Supplier;

public final class ModEntities {

    public static final DeferredRegister<EntityType<?>> ENTITY_TYPES =
            DeferredRegister.create(
                    Registries.ENTITY_TYPE,
                    CraftersNpcAnimations.MOD_ID
            );

    public static final Supplier<EntityType<AnimatedNpcEntity>> ANIMATED_NPC =
            ENTITY_TYPES.register(
                    "animated_npc",
                    () -> EntityType.Builder
                            .of(
                                    AnimatedNpcEntity::new,
                                    MobCategory.MISC
                            )
                            .sized(0.6F, 1.8F)
                            .clientTrackingRange(10)
                            .updateInterval(2)
                            .build("animated_npc")
            );

    private ModEntities() {
    }
}