package org.crafterscr.craftersnpcanimations.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import org.crafterscr.craftersnpcanimations.entity.AnimatedNpcEntity;
import org.crafterscr.craftersnpcanimations.entity.ModEntities;

import java.util.ArrayList;
import java.util.List;

public final class CnpcaCommands {

    private CnpcaCommands() {
    }

    public static void register(
            CommandDispatcher<CommandSourceStack> dispatcher
    ) {

        dispatcher.register(

                Commands.literal("cnpca")

                        .requires(
                                source ->
                                        source.hasPermission(2)
                        )

                        /*
                         * /cnpca create <id>
                         */
                        .then(
                                Commands.literal("create")
                                        .then(
                                                Commands.argument(
                                                                "id",
                                                                StringArgumentType.word()
                                                        )
                                                        .executes(context ->
                                                                createNpc(
                                                                        context.getSource(),
                                                                        StringArgumentType.getString(
                                                                                context,
                                                                                "id"
                                                                        )
                                                                )
                                                        )
                                        )
                        )

                        /*
                         * /cnpca remove <id>
                         */
                        .then(
                                Commands.literal("remove")
                                        .then(
                                                Commands.argument(
                                                                "id",
                                                                StringArgumentType.word()
                                                        )
                                                        .executes(context ->
                                                                removeNpc(
                                                                        context.getSource(),
                                                                        StringArgumentType.getString(
                                                                                context,
                                                                                "id"
                                                                        )
                                                                )
                                                        )
                                        )
                        )

                        /*
                         * /cnpca list
                         */
                        .then(
                                Commands.literal("list")
                                        .executes(context ->
                                                listNpcs(
                                                        context.getSource()
                                                )
                                        )
                        )

                        /*
                         * /cnpca skin <id> <url>
                         */
                        .then(
                                Commands.literal("skin")
                                        .then(
                                                Commands.argument(
                                                                "id",
                                                                StringArgumentType.word()
                                                        )
                                                        .then(
                                                                Commands.argument(
                                                                                "url",
                                                                                StringArgumentType.greedyString()
                                                                        )
                                                                        .executes(context ->
                                                                                setSkin(
                                                                                        context.getSource(),
                                                                                        StringArgumentType.getString(
                                                                                                context,
                                                                                                "id"
                                                                                        ),
                                                                                        StringArgumentType.getString(
                                                                                                context,
                                                                                                "url"
                                                                                        )
                                                                                )
                                                                        )
                                                        )
                                        )
                        )

                        /*
                         * /cnpca model <id> wide
                         * /cnpca model <id> slim
                         */
                        .then(
                                Commands.literal("model")
                                        .then(
                                                Commands.argument(
                                                                "id",
                                                                StringArgumentType.word()
                                                        )

                                                        .then(
                                                                Commands.literal("wide")
                                                                        .executes(context ->
                                                                                setModel(
                                                                                        context.getSource(),
                                                                                        StringArgumentType.getString(
                                                                                                context,
                                                                                                "id"
                                                                                        ),
                                                                                        false
                                                                                )
                                                                        )
                                                        )

                                                        .then(
                                                                Commands.literal("slim")
                                                                        .executes(context ->
                                                                                setModel(
                                                                                        context.getSource(),
                                                                                        StringArgumentType.getString(
                                                                                                context,
                                                                                                "id"
                                                                                        ),
                                                                                        true
                                                                                )
                                                                        )
                                                        )
                                        )
                        )

                        /*
                         * /cnpca emote <id> stop
                         *
                         * /cnpca emote <id> wave
                         *
                         * /cnpca emote <id> dance loop
                         */
                        .then(
                                Commands.literal("emote")

                                        .then(
                                                Commands.argument(
                                                                "id",
                                                                StringArgumentType.word()
                                                        )

                                                        .then(
                                                                Commands.literal("stop")
                                                                        .executes(context ->
                                                                                stopEmote(
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

                                                                        .executes(context ->
                                                                                playEmote(
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
                                                                                Commands.literal("loop")
                                                                                        .executes(context ->
                                                                                                playEmote(
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
        );
    }

    /*
     * ==============================
     * CREAR
     * ==============================
     */

    private static int createNpc(
            CommandSourceStack source,
            String id
    ) {

        ServerLevel level = source.getLevel();

        if (findNpc(level, id) != null) {

            source.sendFailure(
                    Component.literal(
                            "Ya existe un NPC animado llamado: " + id
                    )
            );

            return 0;
        }

        AnimatedNpcEntity npc =
                ModEntities.ANIMATED_NPC.get().create(level);

        if (npc == null) {
            return 0;
        }

        npc.setNpcId(id);

        npc.moveTo(
                source.getPosition().x,
                source.getPosition().y,
                source.getPosition().z,
                source.getRotation().y,
                0F
        );

        npc.setCustomName(
                Component.literal(id)
        );

        npc.setCustomNameVisible(true);

        level.addFreshEntity(npc);

        source.sendSuccess(
                () -> Component.literal(
                        "NPC animado creado: " + id
                ),
                true
        );

        return 1;
    }

    /*
     * ==============================
     * ELIMINAR
     * ==============================
     */

    private static int removeNpc(
            CommandSourceStack source,
            String id
    ) {

        AnimatedNpcEntity npc =
                findNpc(
                        source.getLevel(),
                        id
                );

        if (npc == null) {

            source.sendFailure(
                    Component.literal(
                            "No existe: " + id
                    )
            );

            return 0;
        }

        npc.discard();

        source.sendSuccess(
                () -> Component.literal(
                        "NPC eliminado: " + id
                ),
                true
        );

        return 1;
    }

    /*
     * ==============================
     * LISTA
     * ==============================
     */

    private static int listNpcs(
            CommandSourceStack source
    ) {

        List<AnimatedNpcEntity> npcs =
                getNpcs(
                        source.getLevel()
                );

        source.sendSuccess(
                () -> Component.literal(
                        "Crafters NPC Animations: "
                                + npcs.size()
                                + " NPC(s)"
                ),
                false
        );

        for (AnimatedNpcEntity npc : npcs) {

            source.sendSuccess(
                    () -> Component.literal(
                            " - "
                                    + npc.getNpcId()
                                    + " | "
                                    + npc.getAnimationId()
                    ),
                    false
            );
        }

        return npcs.size();
    }

    /*
     * ==============================
     * SKIN
     * ==============================
     */

    private static int setSkin(
            CommandSourceStack source,
            String id,
            String url
    ) {

        AnimatedNpcEntity npc =
                findNpc(
                        source.getLevel(),
                        id
                );

        if (npc == null) {

            source.sendFailure(
                    Component.literal(
                            "NPC no encontrado: " + id
                    )
            );

            return 0;
        }

        npc.setSkinUrl(url);

        source.sendSuccess(
                () -> Component.literal(
                        "Skin asignada a " + id
                ),
                true
        );

        return 1;
    }

    /*
     * ==============================
     * MODELO
     * ==============================
     */

    private static int setModel(
            CommandSourceStack source,
            String id,
            boolean slim
    ) {

        AnimatedNpcEntity npc =
                findNpc(
                        source.getLevel(),
                        id
                );

        if (npc == null) {
            return 0;
        }

        npc.setSlimModel(slim);

        source.sendSuccess(
                () -> Component.literal(
                        "Modelo de "
                                + id
                                + ": "
                                + (slim ? "SLIM" : "WIDE")
                ),
                true
        );

        return 1;
    }

    /*
     * ==============================
     * EMOTE
     * ==============================
     */

    private static int playEmote(
            CommandSourceStack source,
            String id,
            String emote,
            boolean loop
    ) {

        AnimatedNpcEntity npc =
                findNpc(
                        source.getLevel(),
                        id
                );

        if (npc == null) {

            source.sendFailure(
                    Component.literal(
                            "NPC no encontrado: " + id
                    )
            );

            return 0;
        }

        npc.playAnimation(
                emote,
                loop
        );

        source.sendSuccess(
                () -> Component.literal(
                        "Emote "
                                + emote
                                + " iniciado en "
                                + id
                                + (loop ? " [LOOP]" : "")
                ),
                true
        );

        return 1;
    }

    private static int stopEmote(
            CommandSourceStack source,
            String id
    ) {

        AnimatedNpcEntity npc =
                findNpc(
                        source.getLevel(),
                        id
                );

        if (npc == null) {
            return 0;
        }

        npc.stopAnimation();

        source.sendSuccess(
                () -> Component.literal(
                        "Animación detenida: " + id
                ),
                true
        );

        return 1;
    }

    /*
     * ==============================
     * UTILIDADES
     * ==============================
     */

    private static AnimatedNpcEntity findNpc(
            ServerLevel level,
            String id
    ) {

        for (Entity entity :
                level.getAllEntities()) {

            if (entity
                    instanceof AnimatedNpcEntity npc
                    && npc.getNpcId()
                    .equalsIgnoreCase(id)) {

                return npc;
            }
        }

        return null;
    }

    private static List<AnimatedNpcEntity> getNpcs(
            ServerLevel level
    ) {

        List<AnimatedNpcEntity> result =
                new ArrayList<>();

        for (Entity entity :
                level.getAllEntities()) {

            if (entity
                    instanceof AnimatedNpcEntity npc) {

                result.add(npc);
            }
        }

        return result;
    }
}