package org.crafterscr.craftersnpcanimations;

import com.mojang.logging.LogUtils;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.RegisterCommandsEvent;
import org.crafterscr.craftersnpcanimations.command.CnpcaCommands;
import org.crafterscr.craftersnpcanimations.entity.ModEntities;
import org.slf4j.Logger;

import net.neoforged.fml.loading.FMLPaths;
import org.crafterscr.craftersnpcanimations.animation.emote.NpcEmoteLoader;

import org.crafterscr.craftersnpcanimations.compat.craftersnpc.CraftersNpcCompatCommands;

import org.crafterscr.craftersnpcanimations.compat.craftersnpc.CraftersNpcRouteEmoteCompat;

@Mod(CraftersNpcAnimations.MOD_ID)
public final class CraftersNpcAnimations {

    public static final String MOD_ID = "craftersnpcanimations";

    public static final Logger LOGGER = LogUtils.getLogger();

    public CraftersNpcAnimations(IEventBus modEventBus) {

        ModEntities.ENTITY_TYPES.register(modEventBus);

        NeoForge.EVENT_BUS.addListener(this::registerCommands);

        LOGGER.info("Crafters NPC Animations inicializado.");

        NpcEmoteLoader.initialize(
                FMLPaths.GAMEDIR.get()
        );

        CraftersNpcRouteEmoteCompat.install();
    }

    private void registerCommands(
            RegisterCommandsEvent event
    ) {

        CnpcaCommands.register(
                event.getDispatcher()
        );

        CraftersNpcCompatCommands.register(
                event.getDispatcher()
        );
    }
}