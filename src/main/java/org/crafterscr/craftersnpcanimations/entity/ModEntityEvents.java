package org.crafterscr.craftersnpcanimations.entity;

import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.EntityAttributeCreationEvent;
import org.crafterscr.craftersnpcanimations.CraftersNpcAnimations;

@EventBusSubscriber(
        modid = CraftersNpcAnimations.MOD_ID,
        bus = EventBusSubscriber.Bus.MOD
)
public final class ModEntityEvents {

    private ModEntityEvents() {
    }

    @SubscribeEvent
    public static void registerAttributes(
            EntityAttributeCreationEvent event
    ) {

        event.put(
                ModEntities.ANIMATED_NPC.get(),
                AnimatedNpcEntity
                        .createAttributes()
                        .build()
        );
    }
}