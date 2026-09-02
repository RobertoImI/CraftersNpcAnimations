package org.crafterscr.craftersnpcanimations.animation.emote;

/**
 * Pose calculada de un hueso en un instante concreto.
 *
 * No representa un keyframe.
 *
 * Representa el resultado después de interpolar
 * los keyframes del emote.
 */
public final class NpcEmoteTransform {

    private float x;
    private float y;
    private float z;

    private float pitch;
    private float yaw;
    private float roll;

    private float bend;

    public float x() {
        return x;
    }

    public float y() {
        return y;
    }

    public float z() {
        return z;
    }

    public float pitch() {
        return pitch;
    }

    public float yaw() {
        return yaw;
    }

    public float roll() {
        return roll;
    }

    public float bend() {
        return bend;
    }

    public void setX(float x) {
        this.x = x;
    }

    public void setY(float y) {
        this.y = y;
    }

    public void setZ(float z) {
        this.z = z;
    }

    public void setPitch(float pitch) {
        this.pitch = pitch;
    }

    public void setYaw(float yaw) {
        this.yaw = yaw;
    }

    public void setRoll(float roll) {
        this.roll = roll;
    }

    public void setBend(float bend) {
        this.bend = bend;
    }

    @Override
    public String toString() {

        return "NpcEmoteTransform{" +
                "x=" + x +
                ", y=" + y +
                ", z=" + z +
                ", pitch=" + pitch +
                ", yaw=" + yaw +
                ", roll=" + roll +
                ", bend=" + bend +
                '}';
    }
}