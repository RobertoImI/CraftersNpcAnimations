package org.crafterscr.craftersnpcanimations.animation.bend;

import com.zigythebird.bendable_cuboids.api.BendableCube;
import com.zigythebird.bendable_cuboids.api.BendableModelPart;
import net.minecraft.client.model.geom.ModelPart;

public final class NpcBendApplier {

    private NpcBendApplier() {
    }

    public static void apply(
            ModelPart part,
            float bend
    ) {

        if (part == null) {
            return;
        }

        /*
         * El ModelPart solamente será bendable
         * si BendableCuboids lo convirtió durante
         * el bake.
         */
        if (!(part instanceof BendableModelPart bendablePart)) {
            return;
        }

        /*
         * Un brazo/pierna Vanilla normalmente
         * tiene un solo cubo principal.
         */
        BendableCube cube =
                bendablePart.bc$getCuboid(0);

        if (cube == null) {
            return;
        }

        cube.applyBend(
                bend
        );
    }

    public static void reset(
            ModelPart part
    ) {

        apply(
                part,
                0.0F
        );
    }
}