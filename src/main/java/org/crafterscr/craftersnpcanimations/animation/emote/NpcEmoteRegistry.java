package org.crafterscr.craftersnpcanimations.animation.emote;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public final class NpcEmoteRegistry {

    private static final Map<String, NpcEmote> EMOTES =
            new LinkedHashMap<>();

    private NpcEmoteRegistry() {
    }

    public static void clear() {
        EMOTES.clear();
    }

    public static void register(
            NpcEmote emote
    ) {

        if (emote == null) {
            return;
        }

        String id =
                normalize(
                        emote.id()
                );

        if (id.isBlank()) {
            return;
        }

        EMOTES.put(
                id,
                emote
        );
    }

    public static NpcEmote get(
            String id
    ) {

        if (id == null) {
            return null;
        }

        return EMOTES.get(
                normalize(id)
        );
    }

    public static boolean contains(
            String id
    ) {

        return get(id) != null;
    }

    public static Collection<NpcEmote> values() {
        return Collections.unmodifiableCollection(
                EMOTES.values()
        );
    }

    public static List<String> ids() {

        List<String> ids =
                new ArrayList<>(
                        EMOTES.keySet()
                );

        Collections.sort(ids);

        return ids;
    }

    public static int size() {
        return EMOTES.size();
    }

    private static String normalize(
            String value
    ) {

        if (value == null) {
            return "";
        }

        return value
                .trim()
                .toLowerCase(
                        Locale.ROOT
                );
    }
}