package org.crafterscr.craftersnpcanimations.animation.emote;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import dev.kosmx.playerAnim.core.data.KeyframeAnimation;
import dev.kosmx.playerAnim.core.data.gson.AnimationJson;
import org.crafterscr.craftersnpcanimations.CraftersNpcAnimations;

import java.io.IOException;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public final class NpcEmoteParser {

    private NpcEmoteParser() {
    }

    public static NpcEmote parse(
            Path file
    ) throws IOException {

        try (Reader reader =
                     Files.newBufferedReader(
                             file,
                             StandardCharsets.UTF_8
                     )) {

            JsonElement rootElement =
                    JsonParser.parseReader(
                            reader
                    );

            if (!rootElement.isJsonObject()) {

                throw new IOException(
                        "El archivo no contiene un objeto JSON."
                );
            }

            JsonObject root =
                    rootElement.getAsJsonObject();

            /*
             * ==========================================
             * ID / METADATA
             * ==========================================
             */

            String fileName =
                    file.getFileName()
                            .toString();

            int extension =
                    fileName.lastIndexOf('.');

            String id =
                    extension > 0
                            ? fileName.substring(
                            0,
                            extension
                    )
                            : fileName;

            String displayName =
                    getString(
                            root,
                            "name",
                            id
                    );

            String author =
                    getString(
                            root,
                            "author",
                            ""
                    );

            String description =
                    getString(
                            root,
                            "description",
                            ""
                    );

            if (!root.has("emote")
                    || !root.get("emote").isJsonObject()) {

                throw new IOException(
                        "El archivo no contiene el objeto 'emote'."
                );
            }

            JsonObject emoteObject =
                    root.getAsJsonObject(
                            "emote"
                    );

            /*
             * ==========================================
             * PLAYERANIMATOR NATIVO
             * ==========================================
             *
             * Este es el cambio importante.
             *
             * Ya NO intentamos reconstruir a mano la
             * semántica de Emotecraft para renderizar.
             *
             * AnimationJson es el parser que utiliza
             * PlayerAnimator para el formato legacy.
             *
             * Por ejemplo:
             *
             * version ausente -> versión legacy 1
             * torso -> body/root
             * beginTick / endTick / stopTick
             * returnTick
             * easing
             * bend + axis
             *
             * se interpretan igual que en PlayerAnimator.
             */

            KeyframeAnimation playerAnimation =
                    parsePlayerAnimation(
                            rootElement
                    );

            /*
             * Los tiempos se toman de la animación ya
             * interpretada por PlayerAnimator.
             */
            boolean loop =
                    playerAnimation.isInfinite;

            int returnTick =
                    playerAnimation.returnToTick;

            int beginTick =
                    playerAnimation.beginTick;

            int endTick =
                    playerAnimation.endTick;

            int stopTick =
                    playerAnimation.stopTick;

            boolean degrees =
                    getFlexibleBoolean(
                            emoteObject,
                            "degrees",
                            true
                    );

            /*
             * ==========================================
             * TRACKS DE DIAGNÓSTICO
             * ==========================================
             *
             * Los mantenemos para que no se rompan
             * /cnpca emotes info y debug.
             *
             * El render ya NO utiliza estos tracks.
             */

            Map<String, NpcEmoteBoneTrack> bones =
                    new LinkedHashMap<>();

            if (emoteObject.has("moves")
                    && emoteObject.get("moves").isJsonArray()) {

                for (JsonElement moveElement :
                        emoteObject
                                .getAsJsonArray("moves")) {

                    if (!moveElement.isJsonObject()) {
                        continue;
                    }

                    parseMove(
                            moveElement.getAsJsonObject(),
                            bones
                    );
                }
            }

            for (NpcEmoteBoneTrack track :
                    bones.values()) {

                track.sort();
            }

            NpcEmote emote =
                    new NpcEmote(
                            id,
                            displayName,
                            author,
                            description,
                            file,
                            loop,
                            returnTick,
                            beginTick,
                            endTick,
                            stopTick,
                            degrees,
                            bones,
                            playerAnimation
                    );

            CraftersNpcAnimations.LOGGER.info(
                    "Emote PlayerAnimator cargado: {} | bones={} | keyframes={} | loop={} | begin={} | end={} | stop={}",
                    id,
                    bones.size(),
                    emote.totalKeyframes(),
                    loop,
                    beginTick,
                    endTick,
                    stopTick
            );

            return emote;
        }
    }

    @SuppressWarnings("deprecation")
    private static KeyframeAnimation parsePlayerAnimation(
            JsonElement rootElement
    ) throws IOException {

        try {

            List<KeyframeAnimation> animations =
                    AnimationJson.GSON.fromJson(
                            rootElement,
                            AnimationJson.getListedTypeToken()
                    );

            if (animations == null
                    || animations.isEmpty()
                    || animations.getFirst() == null) {

                throw new IOException(
                        "PlayerAnimator no devolvió ninguna animación."
                );
            }

            return animations.getFirst();

        } catch (IOException exception) {

            throw exception;

        } catch (Exception exception) {

            throw new IOException(
                    "PlayerAnimator no pudo interpretar el JSON de Emotecraft.",
                    exception
            );
        }
    }

    private static void parseMove(
            JsonObject move,
            Map<String, NpcEmoteBoneTrack> bones
    ) {

        int tick =
                getInt(
                        move,
                        "tick",
                        0
                );

        String easing =
                getString(
                        move,
                        "easing",
                        "LINEAR"
                );

        int turn =
                getInt(
                        move,
                        "turn",
                        0
                );

        for (Map.Entry<String, JsonElement> entry :
                move.entrySet()) {

            String key =
                    entry.getKey();

            if (key.equals("tick")
                    || key.equals("comment")
                    || key.equals("easing")
                    || key.equals("easingArg")
                    || key.equals("turn")) {

                continue;
            }

            JsonElement boneElement =
                    entry.getValue();

            if (!boneElement.isJsonObject()) {
                continue;
            }

            parseBoneProperties(
                    key,
                    boneElement.getAsJsonObject(),
                    tick,
                    easing,
                    turn,
                    bones
            );
        }
    }

    private static void parseBoneProperties(
            String boneName,
            JsonObject properties,
            int tick,
            String easing,
            int turn,
            Map<String, NpcEmoteBoneTrack> bones
    ) {

        NpcEmoteBoneTrack track =
                bones.computeIfAbsent(
                        boneName,
                        NpcEmoteBoneTrack::new
                );

        for (Map.Entry<String, JsonElement> property :
                properties.entrySet()) {

            NpcEmoteBoneTrack.Channel channel =
                    NpcEmoteBoneTrack.Channel
                            .fromJsonName(
                                    property.getKey()
                            );

            if (channel == null) {
                continue;
            }

            JsonElement valueElement =
                    property.getValue();

            if (!valueElement.isJsonPrimitive()
                    || !valueElement
                    .getAsJsonPrimitive()
                    .isNumber()) {

                continue;
            }

            float value =
                    valueElement.getAsFloat();

            track.add(
                    channel,
                    new NpcEmoteKeyframe(
                            tick,
                            value,
                            easing,
                            turn
                    )
            );
        }
    }

    private static String getString(
            JsonObject object,
            String key,
            String defaultValue
    ) {

        if (!object.has(key)) {
            return defaultValue;
        }

        try {

            return object
                    .get(key)
                    .getAsString();

        } catch (Exception ignored) {

            return defaultValue;
        }
    }

    private static int getInt(
            JsonObject object,
            String key,
            int defaultValue
    ) {

        if (!object.has(key)) {
            return defaultValue;
        }

        try {

            return object
                    .get(key)
                    .getAsInt();

        } catch (Exception ignored) {

            return defaultValue;
        }
    }

    private static boolean getFlexibleBoolean(
            JsonObject object,
            String key,
            boolean defaultValue
    ) {

        if (!object.has(key)) {
            return defaultValue;
        }

        JsonElement element =
                object.get(key);

        try {

            if (element.isJsonPrimitive()) {

                if (element
                        .getAsJsonPrimitive()
                        .isBoolean()) {

                    return element.getAsBoolean();
                }

                String value =
                        element
                                .getAsString()
                                .trim();

                if (value.equalsIgnoreCase("true")) {
                    return true;
                }

                if (value.equalsIgnoreCase("false")) {
                    return false;
                }
            }

        } catch (Exception ignored) {
        }

        return defaultValue;
    }
}
