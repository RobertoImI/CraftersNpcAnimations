package org.crafterscr.craftersnpcanimations.animation.emote;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.crafterscr.craftersnpcanimations.CraftersNpcAnimations;

import java.io.IOException;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.LinkedHashMap;
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
             * ==============================
             * INFORMACIÓN GENERAL
             * ==============================
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

            /*
             * ==============================
             * EMOTE
             * ==============================
             */

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

            boolean loop =
                    getFlexibleBoolean(
                            emoteObject,
                            "isLoop",
                            false
                    );

            int returnTick =
                    getInt(
                            emoteObject,
                            "returnTick",
                            0
                    );

            int beginTick =
                    getInt(
                            emoteObject,
                            "beginTick",
                            0
                    );

            int endTick =
                    getInt(
                            emoteObject,
                            "endTick",
                            0
                    );

            int stopTick =
                    getInt(
                            emoteObject,
                            "stopTick",
                            endTick
                    );

            boolean degrees =
                    getFlexibleBoolean(
                            emoteObject,
                            "degrees",
                            false
                    );

            /*
             * ==============================
             * TRACKS
             * ==============================
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

            /*
             * Ordenar todos los keyframes.
             */
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
                            bones
                    );

            CraftersNpcAnimations.LOGGER.info(
                    "Emote parseado: {} | bones={} | keyframes={} | loop={}",
                    id,
                    bones.size(),
                    emote.totalKeyframes(),
                    loop
            );

            return emote;
        }
    }

    /*
     * ==============================
     * MOVIMIENTO
     * ==============================
     */

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

        /*
         * Todo lo que NO sea metadata del keyframe
         * lo tratamos como posible hueso.
         */
        for (Map.Entry<String, JsonElement> entry :
                move.entrySet()) {

            String key =
                    entry.getKey();

            if (key.equals("tick")
                    || key.equals("easing")
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

    /*
     * ==============================
     * HUESO
     * ==============================
     */

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

            /*
             * Propiedad que todavía no conocemos.
             *
             * No rompemos el emote.
             * Simplemente la ignoramos.
             */
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

    /*
     * ==============================
     * UTILIDADES JSON
     * ==============================
     */

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

    /*
     * El archivo que me pasaste tiene por ejemplo:
     *
     * "isLoop": "true"
     *
     * como STRING.
     *
     * Pero otros emotes pueden usar:
     *
     * "isLoop": true
     *
     * Esta función acepta ambos.
     */
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