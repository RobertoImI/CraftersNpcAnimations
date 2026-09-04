package org.crafterscr.craftersnpcanimations.client.model;

import net.minecraft.client.model.PlayerModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.builders.CubeDeformation;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;

public final class AnimatedNpcModelDefinition {

    private AnimatedNpcModelDefinition() {
    }

    public static LayerDefinition createWide() {

        MeshDefinition mesh =
                PlayerModel.createMesh(
                        CubeDeformation.NONE,
                        false
                );

        return LayerDefinition.create(
                mesh,
                64,
                64
        );
    }

    public static LayerDefinition createSlim() {

        MeshDefinition mesh =
                PlayerModel.createMesh(
                        CubeDeformation.NONE,
                        true
                );

        return LayerDefinition.create(
                mesh,
                64,
                64
        );
    }

    /*
     * Se conservan estos métodos por compatibilidad con
     * cualquier código existente que los invoque.
     *
     * Ya NO necesitamos bakeRootWithBends().
     * PlayerAnimator inicializa BendyLib directamente
     * sobre los ModelPart del HumanoidModel/PlayerModel.
     */

    public static ModelPart bakeWide() {
        return createWide().bakeRoot();
    }

    public static ModelPart bakeSlim() {
        return createSlim().bakeRoot();
    }
}
