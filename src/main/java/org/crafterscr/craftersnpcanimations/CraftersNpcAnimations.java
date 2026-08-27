package org.crafterscr.craftersnpcanimations;

import com.mojang.logging.LogUtils;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.RegisterCommandsEvent;
import org.crafterscr.craftersnpcanimations.command.CnpcaCommands;
import org.crafterscr.craftersnpcanimations.entity.ModEntities;
import org.slf4j.Logger;

@Mod(CraftersNpcAnimations.MOD_ID)
public final class CraftersNpcAnimations {

    public static final String MOD_ID = "craftersnpcanimations";

    public static final Logger LOGGER = LogUtils.getLogger();

    public CraftersNpcAnimations(IEventBus modEventBus) {

        ModEntities.ENTITY_TYPES.register(modEventBus);

        NeoForge.EVENT_BUS.addListener(this::registerCommands);

        LOGGER.info("Crafters NPC Animations inicializado.");
    }

    private void registerCommands(RegisterCommandsEvent event) {
        CnpcaCommands.register(event.getDispatcher());
    }
}