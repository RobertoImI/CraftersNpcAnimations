package org.crafterscr.craftersnpcanimations.compat.craftersnpc;

import net.minecraft.world.entity.Entity;

import org.crafterscr.craftersnpcanimations.CraftersNpcAnimations;
import org.crafterscr.craftersnpcanimations.animation.emote.NpcEmoteRegistry;

import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public final class CraftersNpcRouteEmoteCompat {

    private static final String ACTION_REGISTRY_CLASS =
            "org.crafterscr.craftersnpc.behavior.action.NpcActionRegistry";

    private static final String ACTION_INTERFACE_CLASS =
            "org.crafterscr.craftersnpc.behavior.action.NpcAction";

    private static boolean installed;

    private CraftersNpcRouteEmoteCompat() {
    }

    public static void install() {

        if (installed) {
            return;
        }

        if (!CraftersNpcCompat.isAvailable()) {

            CraftersNpcAnimations.LOGGER.info(
                    "CraftersNpc no está disponible. Acción de ruta 'emote' no registrada."
            );

            return;
        }

        try {

            Class<?> registryClass =
                    Class.forName(
                            ACTION_REGISTRY_CLASS
                    );

            Class<?> actionInterface =
                    Class.forName(
                            ACTION_INTERFACE_CLASS
                    );

            Object actionProxy =
                    Proxy.newProxyInstance(
                            actionInterface.getClassLoader(),
                            new Class<?>[]{
                                    actionInterface
                            },
                            (proxy, method, args) ->
                                    handleActionMethod(
                                            proxy,
                                            method,
                                            args
                                    )
                    );

            Method registerMethod =
                    registryClass.getMethod(
                            "register",
                            String.class,
                            actionInterface
                    );

            registerMethod.invoke(
                    null,
                    "emote",
                    actionProxy
            );

            installed = true;

            CraftersNpcAnimations.LOGGER.info(
                    "Acción de ruta 'emote' registrada en CraftersNpc."
            );

        } catch (Exception exception) {

            CraftersNpcAnimations.LOGGER.error(
                    "No se pudo registrar la acción de ruta 'emote' en CraftersNpc.",
                    exception
            );
        }
    }

    private static Object handleActionMethod(
            Object proxy,
            Method method,
            Object[] args
    ) {

        String name =
                method.getName();

        /*
         * ==========================================
         * Object
         * ==========================================
         */
        if (
                method.getDeclaringClass()
                        == Object.class
        ) {

            return switch (name) {

                case "toString" ->
                        "CraftersNpcAnimationsRouteEmoteAction";

                case "hashCode" ->
                        System.identityHashCode(
                                proxy
                        );

                case "equals" ->
                        proxy
                                == (
                                args == null
                                        || args.length == 0
                                        ? null
                                        : args[0]
                        );

                default ->
                        null;
            };
        }

        /*
         * ==========================================
         * DESCRIPCIÓN
         * ==========================================
         */
        if (
                name.equals(
                        "description"
                )
        ) {

            return "Reproduce un emote mientras el NPC espera en el punto";
        }

        /*
         * ==========================================
         * SUGERENCIAS
         * ==========================================
         */
        if (
                name.equals(
                        "parameterSuggestions"
                )
        ) {

            return buildParameterSuggestions();
        }

        /*
         * ==========================================
         * START
         * ==========================================
         *
         * No hacemos nada todavía.
         *
         * El delay se controla desde tick().
         */
        if (
                name.equals(
                        "start"
                )
        ) {

            return null;
        }

        /*
         * ==========================================
         * TICK
         * ==========================================
         */
        if (
                name.equals(
                        "tick"
                )
                        && args != null
                        && args.length >= 3
        ) {

            Object npcObject =
                    args[0];

            if (!(npcObject instanceof Entity npc)) {
                return null;
            }

            @SuppressWarnings("unchecked")
            Map<String, String> parameters =
                    (Map<String, String>) args[1];

            int elapsedTicks =
                    ((Number) args[2])
                            .intValue();

            tickAction(
                    npc,
                    parameters,
                    elapsedTicks
            );

            return null;
        }

        /*
         * ==========================================
         * FINISH
         * ==========================================
         */
        if (
                name.equals(
                        "finish"
                )
                        && args != null
                        && args.length >= 1
        ) {

            Object npcObject =
                    args[0];

            if (
                    npcObject instanceof Entity npc
            ) {

                CraftersNpcCompat.stopAnimation(
                        npc
                );
            }

            return null;
        }

        return null;
    }

    private static void tickAction(
            Entity npc,
            Map<String, String> parameters,
            int elapsedTicks
    ) {

        if (
                parameters == null
                        || parameters.isEmpty()
        ) {

            return;
        }

        String emoteId =
                parameters.getOrDefault(
                        "emote",
                        ""
                );

        if (
                emoteId.isBlank()
        ) {

            return;
        }

        /*
         * delay está expresado en segundos.
         *
         * Ejemplo:
         *
         * delay=3
         *
         * significa esperar 3 segundos después
         * de llegar al punto.
         */
        int delaySeconds =
                parsePositiveInt(
                        parameters.get(
                                "delay"
                        ),
                        0
                );

        int delayTicks =
                delaySeconds
                        * 20;

        if (
                elapsedTicks
                        < delayTicks
        ) {

            return;
        }

        /*
         * Si el JSON no existe, no iniciamos nada.
         */
        if (
                NpcEmoteRegistry.get(
                        emoteId
                )
                        == null
        ) {

            return;
        }

        /*
         * Si ya está reproduciendo este mismo emote,
         * no lo reiniciamos cada tick.
         */
        String currentAnimation =
                CraftersNpcCompat.getAnimationId(
                        npc
                );

        if (
                currentAnimation.equalsIgnoreCase(
                        emoteId
                )
        ) {

            return;
        }

        /*
         * El parámetro loop es opcional.
         *
         * Por defecto usamos false porque el propio JSON
         * puede venir configurado como loop.
         */
        boolean loop =
                parseBoolean(
                        parameters.get(
                                "loop"
                        ),
                        false
                );

        CraftersNpcCompat.playAnimation(
                npc,
                emoteId,
                loop
        );
    }

    private static List<String> buildParameterSuggestions() {

        List<String> suggestions =
                new ArrayList<>();

        /*
         * Una sugerencia por cada emote cargado.
         */
        for (
                String emote :
                NpcEmoteRegistry.ids()
        ) {

            suggestions.add(
                    "emote="
                            + emote
            );

            suggestions.add(
                    "emote="
                            + emote
                            + ",delay=3"
            );

            suggestions.add(
                    "emote="
                            + emote
                            + ",delay=3,loop=true"
            );
        }

        return suggestions;
    }

    private static int parsePositiveInt(
            String value,
            int fallback
    ) {

        if (
                value == null
                        || value.isBlank()
        ) {

            return fallback;
        }

        try {

            return Math.max(
                    0,
                    Integer.parseInt(
                            value.trim()
                    )
            );

        } catch (
                NumberFormatException ignored
        ) {

            return fallback;
        }
    }

    private static boolean parseBoolean(
            String value,
            boolean fallback
    ) {

        if (
                value == null
                        || value.isBlank()
        ) {

            return fallback;
        }

        return switch (
                value
                        .trim()
                        .toLowerCase()
                ) {

            case "true",
                 "yes",
                 "on",
                 "1",
                 "si",
                 "sí" ->
                    true;

            case "false",
                 "no",
                 "off",
                 "0" ->
                    false;

            default ->
                    fallback;
        };
    }
}