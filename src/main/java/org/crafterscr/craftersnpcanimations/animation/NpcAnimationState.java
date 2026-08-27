package org.crafterscr.craftersnpcanimations.animation;

public final class NpcAnimationState {

    private String animationId = "";
    private boolean loop;
    private long startedAt;

    public String animationId() {
        return animationId;
    }

    public boolean loop() {
        return loop;
    }

    public long startedAt() {
        return startedAt;
    }

    public boolean isPlaying() {
        return !animationId.isBlank();
    }

    public void play(
            String animationId,
            boolean loop,
            long startedAt
    ) {
        this.animationId = animationId;
        this.loop = loop;
        this.startedAt = startedAt;
    }

    public void stop() {
        animationId = "";
        loop = false;
        startedAt = 0L;
    }
}