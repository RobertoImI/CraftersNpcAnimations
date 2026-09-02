package org.crafterscr.craftersnpcanimations.animation.bend;

/**
 * Resultado de bend del cuerpo para un frame.
 *
 * Los valores están en radianes.
 */
public final class NpcBendPose {

    private float rightArm;
    private float leftArm;

    private float rightLeg;
    private float leftLeg;

    public float rightArm() {
        return rightArm;
    }

    public float leftArm() {
        return leftArm;
    }

    public float rightLeg() {
        return rightLeg;
    }

    public float leftLeg() {
        return leftLeg;
    }

    public void setRightArm(float value) {
        this.rightArm = value;
    }

    public void setLeftArm(float value) {
        this.leftArm = value;
    }

    public void setRightLeg(float value) {
        this.rightLeg = value;
    }

    public void setLeftLeg(float value) {
        this.leftLeg = value;
    }

    public void reset() {

        rightArm = 0.0F;
        leftArm = 0.0F;

        rightLeg = 0.0F;
        leftLeg = 0.0F;
    }
}