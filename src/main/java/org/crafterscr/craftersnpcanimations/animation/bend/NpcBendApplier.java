package org.crafterscr.craftersnpcanimations.animation.bend;

import dev.kosmx.playerAnim.impl.Helper;
import dev.kosmx.playerAnim.impl.animation.IBendHelper;
import net.minecraft.client.model.geom.ModelPart;

public final class NpcBendApplier {

    private NpcBendApplier() {
    }

    /**
     * Wrapper de compatibilidad.
     *
     * El render principal ya no utiliza esta clase:
     * AnimationApplier aplica bend + axis directamente
     * mediante el sistema original de PlayerAnimator.
     */
    public static void apply(
            ModelPart part,
            float bend
    ) {

        if (part == null) {
            return;
        }

        IBendHelper.INSTANCE.bend(
                part,
                0.0F,
                bend
        );
    }

    public static void reset(
            ModelPart part
    ) {

        if (part == null) {
            return;
        }

        IBendHelper.INSTANCE.bend(
                part,
                null
        );
    }

    /**
     * Se conservan por compatibilidad con el diagnóstico
     * anterior. Ya no inspeccionamos BendableCuboids.
     */
    public static boolean isBendable(
            ModelPart part
    ) {

        return part != null
                && Helper.isBendEnabled();
    }

    public static boolean hasBendableCube(
            ModelPart part
    ) {

        return isBendable(
                part
        );
    }
}
