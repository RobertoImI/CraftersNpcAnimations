package org.crafterscr.craftersnpcanimations.animation.emote;

import dev.kosmx.playerAnim.core.data.KeyframeAnimation;

import java.nio.file.Path;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

public final class NpcEmote {

    private final String id;
    private final String displayName;
    private final String author;
    private final String description;

    private final Path file;

    private final boolean loop;
    private final int returnTick;
    private final int beginTick;
    private final int endTick;
    private final int stopTick;
    private final boolean degrees;

    /*
     * Tracks propios de CraftersNpcAnimations.
     *
     * Los conservamos por ahora porque /cnpca emotes info
     * y los comandos de diagnóstico ya los utilizan.
     *
     * El render NO dependerá de ellos.
     */
    private final Map<String, NpcEmoteBoneTrack> bones;

    /*
     * Animación interpretada por el parser ORIGINAL
     * de PlayerAnimator.
     *
     * Esta es la que se utilizará realmente para renderizar.
     */
    private final KeyframeAnimation playerAnimation;

    public NpcEmote(
            String id,
            String displayName,
            String author,
            String description,
            Path file,
            boolean loop,
            int returnTick,
            int beginTick,
            int endTick,
            int stopTick,
            boolean degrees,
            Map<String, NpcEmoteBoneTrack> bones,
            KeyframeAnimation playerAnimation
    ) {

        this.id = id;

        this.displayName =
                displayName == null
                        ? id
                        : displayName;

        this.author =
                author == null
                        ? ""
                        : author;

        this.description =
                description == null
                        ? ""
                        : description;

        this.file = file;

        this.loop = loop;
        this.returnTick = returnTick;
        this.beginTick = beginTick;
        this.endTick = endTick;
        this.stopTick = stopTick;
        this.degrees = degrees;

        this.bones =
                new LinkedHashMap<>(
                        bones
                );

        this.playerAnimation = playerAnimation;
    }

    public String id() {
        return id;
    }

    public String displayName() {
        return displayName;
    }

    public String author() {
        return author;
    }

    public String description() {
        return description;
    }

    public Path file() {
        return file;
    }

    public boolean loop() {
        return loop;
    }

    public int returnTick() {
        return returnTick;
    }

    public int beginTick() {
        return beginTick;
    }

    public int endTick() {
        return endTick;
    }

    public int stopTick() {
        return stopTick;
    }

    public boolean degrees() {
        return degrees;
    }

    public Map<String, NpcEmoteBoneTrack> bones() {

        return Collections.unmodifiableMap(
                bones
        );
    }

    public NpcEmoteBoneTrack bone(
            String name
    ) {

        if (name == null) {
            return null;
        }

        return bones.get(name);
    }

    public KeyframeAnimation playerAnimation() {
        return playerAnimation;
    }

    public int totalKeyframes() {

        int total = 0;

        for (NpcEmoteBoneTrack track :
                bones.values()) {

            total += track.getKeyframeCount();
        }

        return total;
    }

    @Override
    public String toString() {
        return id;
    }
}
