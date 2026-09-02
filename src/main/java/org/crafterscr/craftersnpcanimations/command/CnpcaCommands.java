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

import org.crafterscr.craftersnpcanimations.animation.emote.NpcEmoteLoader;
import org.crafterscr.craftersnpcanimations.animation.emote.NpcEmoteRegistry;

import org.crafterscr.craftersnpcanimations.animation.emote.NpcEmote;
import org.crafterscr.craftersnpcanimations.animation.emote.NpcEmoteBoneTrack;

import org.crafterscr.craftersnpcanimations.animation.bend.NpcBendController;
import org.crafterscr.craftersnpcanimations.animation.bend.NpcBendPose;
import org.crafterscr.craftersnpcanimations.animation.emote.NpcEmotePlayback;

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

                        .then(
                                Commands.literal("emotes")

                                        .then(
                                                Commands.literal("list")
                                                        .executes(
                                                                context ->
                                                                        listEmotes(
                                                                                context.getSource()
                                                                        )
                                                        )
                                        )

                                        .then(
                                                Commands.literal("reload")
                                                        .executes(
                                                                context ->
                                                                        reloadEmotes(
                                                                                context.getSource()
                                                                        )
                                                        )
                                        )

                                        .then(
                                                Commands.literal("info")
                                                        .then(
                                                                Commands.argument(
                                                                                "emote",
                                                                                StringArgumentType.word()
                                                                        )
                                                                        .executes(
                                                                                context ->
                                                                                        showEmoteInfo(
                                                                                                context.getSource(),
                                                                                                StringArgumentType.getString(
                                                                                                        context,
                                                                                                        "emote"
                                                                                                )
                                                                                        )
                                                                        )
                                                        )
                                        )
                        )

                        .then(
                                Commands.literal("debug")

                                        .then(
                                                Commands.literal("bend")

                                                        .then(
                                                                Commands.argument(
                                                                                "id",
                                                                                StringArgumentType.word()
                                                                        )

                                                                        .executes(context ->
                                                                                debugBend(
                                                                                        context.getSource(),
                                                                                        StringArgumentType.getString(
                                                                                                context,
                                                                                                "id"
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
                            "NPC no encontrado: "
                                    + id
                    )
            );

            return 0;
        }

        /*
         * Comprobar biblioteca.
         */
        NpcEmote loadedEmote =
                NpcEmoteRegistry.get(
                        emote
                );

        if (loadedEmote == null) {

            source.sendFailure(
                    Component.literal(
                            "Emote no encontrado: "
                                    + emote
                                    + ". Usa /cnpca emotes list"
                    )
            );

            return 0;
        }

        npc.playAnimation(
                loadedEmote.id(),
                loop
        );

        source.sendSuccess(
                () -> Component.literal(
                        "Emote "
                                + loadedEmote.id()
                                + " iniciado en "
                                + id
                                + (
                                loop
                                        ? " [LOOP FORZADO]"
                                        : ""
                        )
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

    private static int listEmotes(
            CommandSourceStack source
    ) {

        List<String> emotes =
                NpcEmoteRegistry.ids();

        source.sendSuccess(
                () -> Component.literal(
                        "Emotes cargados: "
                                + emotes.size()
                ),
                false
        );

        if (emotes.isEmpty()) {

            source.sendSuccess(
                    () -> Component.literal(
                            "No hay archivos .json dentro de /emotes"
                    ),
                    false
            );

            return 0;
        }

        for (String emote : emotes) {

            source.sendSuccess(
                    () -> Component.literal(
                            " - " + emote
                    ),
                    false
            );
        }

        return emotes.size();
    }

    private static int reloadEmotes(
            CommandSourceStack source
    ) {

        NpcEmoteLoader.reload();

        int count =
                NpcEmoteRegistry.size();

        source.sendSuccess(
                () -> Component.literal(
                        "Biblioteca de emotes recargada. "
                                + count
                                + " emote(s) encontrados."
                ),
                true
        );

        return count;
    }

    private static int showEmoteInfo(
            CommandSourceStack source,
            String id
    ) {

        NpcEmote emote =
                NpcEmoteRegistry.get(id);

        if (emote == null) {

            source.sendFailure(
                    Component.literal(
                            "Emote no encontrado: " + id
                    )
            );

            return 0;
        }

        source.sendSuccess(
                () -> Component.literal(
                        "===== EMOTE ====="
                ),
                false
        );

        source.sendSuccess(
                () -> Component.literal(
                        "ID: " + emote.id()
                ),
                false
        );

        source.sendSuccess(
                () -> Component.literal(
                        "Nombre: "
                                + emote.displayName()
                ),
                false
        );

        source.sendSuccess(
                () -> Component.literal(
                        "Autor: "
                                + emote.author()
                ),
                false
        );

        source.sendSuccess(
                () -> Component.literal(
                        "Loop: "
                                + (
                                emote.loop()
                                        ? "Sí"
                                        : "No"
                        )
                ),
                false
        );

        source.sendSuccess(
                () -> Component.literal(
                        "BeginTick: "
                                + emote.beginTick()
                ),
                false
        );

        source.sendSuccess(
                () -> Component.literal(
                        "ReturnTick: "
                                + emote.returnTick()
                ),
                false
        );

        source.sendSuccess(
                () -> Component.literal(
                        "EndTick: "
                                + emote.endTick()
                ),
                false
        );

        source.sendSuccess(
                () -> Component.literal(
                        "StopTick: "
                                + emote.stopTick()
                ),
                false
        );

        source.sendSuccess(
                () -> Component.literal(
                        "Huesos: "
                                + emote.bones().size()
                ),
                false
        );

        source.sendSuccess(
                () -> Component.literal(
                        "Keyframes: "
                                + emote.totalKeyframes()
                ),
                false
        );

        for (NpcEmoteBoneTrack bone :
                emote.bones().values()) {

            source.sendSuccess(
                    () -> Component.literal(
                            " - "
                                    + bone.boneName()
                                    + ": "
                                    + bone.getKeyframeCount()
                                    + " keyframes"
                    ),
                    false
            );
        }

        return 1;
    }

    private static int debugBend(
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
                            "NPC no encontrado: " + id
                    )
            );

            return 0;
        }

        if (!npc.isAnimationPlaying()) {

            source.sendFailure(
                    Component.literal(
                            "El NPC no está reproduciendo ningún emote."
                    )
            );

            return 0;
        }

        NpcEmote emote =
                NpcEmoteRegistry.get(
                        npc.getAnimationId()
                );

        if (emote == null) {

            source.sendFailure(
                    Component.literal(
                            "El emote activo no está cargado."
                    )
            );

            return 0;
        }

        float elapsed =
                source.getLevel()
                        .getGameTime()
                        - npc.getAnimationStart();

        float tick =
                NpcEmotePlayback.calculateAnimationTick(
                        emote,
                        elapsed,
                        npc.isAnimationLooping()
                );

        NpcBendPose bend =
                NpcBendController.sample(
                        emote,
                        tick
                );

        source.sendSuccess(
                () -> Component.literal(
                        "===== BEND ====="
                ),
                false
        );

        source.sendSuccess(
                () -> Component.literal(
                        "Tick: " + tick
                ),
                false
        );

        source.sendSuccess(
                () -> Component.literal(
                        "RightArm: " + bend.rightArm()
                ),
                false
        );

        source.sendSuccess(
                () -> Component.literal(
                        "LeftArm: " + bend.leftArm()
                ),
                false
        );

        source.sendSuccess(
                () -> Component.literal(
                        "RightLeg: " + bend.rightLeg()
                ),
                false
        );

        source.sendSuccess(
                () -> Component.literal(
                        "LeftLeg: " + bend.leftLeg()
                ),
                false
        );

        return 1;
    }
}