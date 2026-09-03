package org.crafterscr.craftersnpcanimations.client.model;

import net.minecraft.client.model.PlayerModel;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;

public final class AnimatedNpcModelDefinition {

    private AnimatedNpcModelDefinition() {
    }

    public static LayerDefinition createWide() {

        MeshDefinition mesh =
                PlayerModel.createMesh(
                        net.minecraft.client.model.geom.builders.CubeDeformation.NONE,
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
                        net.minecraft.client.model.geom.builders.CubeDeformation.NONE,
                        true
                );

        return LayerDefinition.create(
                mesh,
                64,
                64
        );
    }
}