package org.crafterscr.craftersnpcanimations.compat.craftersnpc;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;

import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;

import org.crafterscr.craftersnpcanimations.animation.emote.NpcEmote;
import org.crafterscr.craftersnpcanimations.animation.emote.NpcEmoteRegistry;

import org.crafterscr.craftersnpcanimations.command.CnpcaSuggestions;

public final class CraftersNpcCompatCommands {

    private CraftersNpcCompatCommands() {
    }

    public static void register(
            CommandDispatcher<CommandSourceStack> dispatcher
    ) {

        if (!CraftersNpcCompat.isAvailable()) {
            return;
        }

        dispatcher.register(

                Commands.literal(
                                "cnpca"
                        )

                        .requires(
                                source ->
                                        source.hasPermission(
                                                2
                                        )
                        )

                        .then(

                                Commands.literal(
                                                "cnpc"
                                        )

                                        .then(

                                                Commands.literal(
                                                                "emote"
                                                        )

                                                        .then(

                                                                Commands.argument(
                                                                                "id",
                                                                                StringArgumentType.word()
                                                                        )

                                                                        .suggests(
                                                                                CnpcaSuggestions::suggestCnpcIds
                                                                        )

                                                                        .then(

                                                                                Commands.literal(
                                                                                                "stop"
                                                                                        )

                                                                                        .executes(
                                                                                                context ->
                                                                                                        stop(
                                                                                                                context.getSource(),
                                                                                                                StringArgumentType.getString(
                                                                                                                        context,
                                                                                                                        "id"
                                                                                                                )
                                                                                                        )
                                                                                        )
                                                                        )

                                                                        .then(

                                                                                Commands.argument(
                                                                                                "emote",
                                                                                                StringArgumentType.word()
                                                                                        )

                                                                                        .suggests(
                                                                                                CnpcaSuggestions::suggestEmoteIds
                                                                                        )

                                                                                        .executes(
                                                                                                context ->
                                                                                                        play(
                                                                                                                context.getSource(),
                                                                                                                StringArgumentType.getString(
                                                                                                                        context,
                                                                                                                        "id"
                                                                                                                ),
                                                                                                                StringArgumentType.getString(
                                                                                                                        context,
                                                                                                                        "emote"
                                                                                                                ),
                                                                                                                false
                                                                                                        )
                                                                                        )

                                                                                        .then(

                                                                                                Commands.literal(
                                                                                                                "loop"
                                                                                                        )

                                                                                                        .executes(
                                                                                                                context ->
                                                                                                                        play(
                                                                                                                                context.getSource(),
                                                                                                                                StringArgumentType.getString(
                                                                                                                                        context,
                                                                                                                                        "id"
                                                                                                                                ),
                                                                                                                                StringArgumentType.getString(
                                                                                                                                        context,
                                                                                                                                        "emote"
                                                                                                                                ),
                                                                                                                                true
                                                                                                                        )
                                                                                                        )
                                                                                        )
                                                                        )
                                                        )
                                        )
                        )
        );
    }

    private static int play(
            CommandSourceStack source,
            String npcId,
            String emoteId,
            boolean loop
    ) {

        Entity npc =
                findNpc(
                        source.getLevel(),
                        npcId
                );

        if (npc == null) {

            source.sendFailure(
                    Component.literal(
                            "CraftersNpc no encontrado: "
                                    + npcId
                    )
            );

            return 0;
        }

        NpcEmote emote =
                NpcEmoteRegistry.get(
                        emoteId
                );

        if (emote == null) {

            source.sendFailure(
                    Component.literal(
                            "Emote no encontrado: "
                                    + emoteId
                    )
            );

            return 0;
        }

        if (
                !CraftersNpcCompat.playAnimation(
                        npc,
                        emote.id(),
                        loop
                )
        ) {

            source.sendFailure(
                    Component.literal(
                            "No se pudo iniciar la animación."
                    )
            );

            return 0;
        }

        source.sendSuccess(
                () ->
                        Component.literal(
                                "Emote "
                                        + emote.id()
                                        + " iniciado en CraftersNpc "
                                        + npcId
                                        + (
                                        loop
                                                ? " [LOOP]"
                                                : ""
                                )
                        ),
                true
        );

        return 1;
    }

    private static int stop(
            CommandSourceStack source,
            String npcId
    ) {

        Entity npc =
                findNpc(
                        source.getLevel(),
                        npcId
                );

        if (npc == null) {

            source.sendFailure(
                    Component.literal(
                            "CraftersNpc no encontrado: "
                                    + npcId
                    )
            );

            return 0;
        }

        CraftersNpcCompat.stopAnimation(
                npc
        );

        source.sendSuccess(
                () ->
                        Component.literal(
                                "Animación detenida en "
                                        + npcId
                        ),
                true
        );

        return 1;
    }

    private static Entity findNpc(
            ServerLevel level,
            String id
    ) {

        for (
                Entity entity :
                level.getAllEntities()
        ) {

            if (
                    CraftersNpcCompat.isCnpc(
                            entity
                    )
                            && CraftersNpcCompat
                            .getNpcId(
                                    entity
                            )
                            .equalsIgnoreCase(
                                    id
                            )
            ) {

                return entity;
            }
        }

        return null;
    }
}