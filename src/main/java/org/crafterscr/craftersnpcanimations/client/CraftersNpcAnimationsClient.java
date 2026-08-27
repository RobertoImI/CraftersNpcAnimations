package org.crafterscr.craftersnpcanimations.client;

import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;
import org.crafterscr.craftersnpcanimations.CraftersNpcAnimations;
import org.crafterscr.craftersnpcanimations.entity.ModEntities;

@EventBusSubscriber(
        modid = CraftersNpcAnimations.MOD_ID,
        bus = EventBusSubscriber.Bus.MOD
)
public final class CraftersNpcAnimationsClient {

    private CraftersNpcAnimationsClient() {
    }

    @SubscribeEvent
    public static void registerRenderers(
            EntityRenderersEvent.RegisterRenderers event
    ) {

        event.registerEntityRenderer(
                ModEntities.ANIMATED_NPC.get(),
                AnimatedNpcRenderer::new
        );
    }
}