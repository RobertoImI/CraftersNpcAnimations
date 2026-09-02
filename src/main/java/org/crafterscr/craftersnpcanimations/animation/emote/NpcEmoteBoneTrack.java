package org.crafterscr.craftersnpcanimations.animation.emote;

import java.util.ArrayList;
import java.util.Collections;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;

public final class NpcEmoteBoneTrack {

    public enum Channel {

        X("x"),
        Y("y"),
        Z("z"),

        PITCH("pitch"),
        YAW("yaw"),
        ROLL("roll"),

        BEND("bend");

        private final String jsonName;

        Channel(String jsonName) {
            this.jsonName = jsonName;
        }

        public String jsonName() {
            return jsonName;
        }

        public static Channel fromJsonName(
                String name
        ) {

            if (name == null) {
                return null;
            }

            for (Channel channel : values()) {

                if (channel.jsonName.equalsIgnoreCase(name)) {
                    return channel;
                }
            }

            return null;
        }
    }

    private final String boneName;

    private final Map<Channel, List<NpcEmoteKeyframe>> tracks =
            new EnumMap<>(Channel.class);

    public NpcEmoteBoneTrack(
            String boneName
    ) {

        this.boneName = boneName;

        for (Channel channel : Channel.values()) {
            tracks.put(
                    channel,
                    new ArrayList<>()
            );
        }
    }

    public String boneName() {
        return boneName;
    }

    public void add(
            Channel channel,
            NpcEmoteKeyframe keyframe
    ) {

        if (channel == null || keyframe == null) {
            return;
        }

        tracks.get(channel)
                .add(keyframe);
    }

    public List<NpcEmoteKeyframe> get(
            Channel channel
    ) {

        List<NpcEmoteKeyframe> result =
                tracks.get(channel);

        if (result == null) {
            return List.of();
        }

        return Collections.unmodifiableList(
                result
        );
    }

    public boolean has(
            Channel channel
    ) {

        List<NpcEmoteKeyframe> values =
                tracks.get(channel);

        return values != null
                && !values.isEmpty();
    }

    public int getKeyframeCount() {

        int count = 0;

        for (List<NpcEmoteKeyframe> list :
                tracks.values()) {

            count += list.size();
        }

        return count;
    }

    public void sort() {

        for (List<NpcEmoteKeyframe> list :
                tracks.values()) {

            list.sort(
                    (a, b) ->
                            Integer.compare(
                                    a.tick(),
                                    b.tick()
                            )
            );
        }
    }
}