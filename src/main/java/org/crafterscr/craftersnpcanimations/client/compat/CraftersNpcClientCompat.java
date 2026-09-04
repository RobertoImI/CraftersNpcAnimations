package org.crafterscr.craftersnpcanimations.client.compat;

import com.mojang.blaze3d.vertex.PoseStack;

import net.minecraft.client.model.PlayerModel;
import net.minecraft.world.entity.Entity;

import org.crafterscr.craftersnpcanimations.CraftersNpcAnimations;
import org.crafterscr.craftersnpcanimations.compat.craftersnpc.CraftersNpcCompat;

import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

public final class CraftersNpcClientCompat {

    private static boolean installed;

    private CraftersNpcClientCompat() {
    }

    public static void install() {

        if (installed) {
            return;
        }

        installed = true;

        if (!CraftersNpcCompat.isAvailable()) {

            CraftersNpcAnimations.LOGGER.info(
                    "CraftersNpc no está instalado. Compatibilidad omitida."
            );

            return;
        }

        try {

            Class<?> hooksClass =
                    Class.forName(
                            "org.crafterscr.craftersnpc.client.animation.CnpcAnimationHooks"
                    );

            Class<?> modelHookClass =
                    Class.forName(
                            "org.crafterscr.craftersnpc.client.animation.CnpcAnimationHooks$ModelAnimationHook"
                    );

            Class<?> rootHookClass =
                    Class.forName(
                            "org.crafterscr.craftersnpc.client.animation.CnpcAnimationHooks$RootAnimationHook"
                    );

            Object modelHook =
                    Proxy.newProxyInstance(
                            modelHookClass.getClassLoader(),
                            new Class<?>[]{
                                    modelHookClass
                            },
                            (proxy, method, args) -> {

                                if (
                                        method.getName()
                                                .equals(
                                                        "apply"
                                                )
                                                && args != null
                                                && args.length == 3
                                ) {

                                    Entity entity =
                                            (Entity) args[0];

                                    PlayerModel<?> model =
                                            (PlayerModel<?>) args[1];

                                    float ageInTicks =
                                            ((Number) args[2])
                                                    .floatValue();

                                    CraftersNpcAnimationBridge
                                            .applyModel(
                                                    entity,
                                                    model,
                                                    ageInTicks
                                            );
                                }

                                return null;
                            }
                    );

            Object rootHook =
                    Proxy.newProxyInstance(
                            rootHookClass.getClassLoader(),
                            new Class<?>[]{
                                    rootHookClass
                            },
                            (proxy, method, args) -> {

                                if (
                                        method.getName()
                                                .equals(
                                                        "apply"
                                                )
                                                && args != null
                                                && args.length == 3
                                ) {

                                    Entity entity =
                                            (Entity) args[0];

                                    float partialTicks =
                                            ((Number) args[1])
                                                    .floatValue();

                                    PoseStack poseStack =
                                            (PoseStack) args[2];

                                    CraftersNpcAnimationBridge
                                            .applyBodyTransform(
                                                    entity,
                                                    partialTicks,
                                                    poseStack
                                            );
                                }

                                return null;
                            }
                    );

            Method registerModelHook =
                    hooksClass.getMethod(
                            "registerModelHook",
                            modelHookClass
                    );

            Method registerRootHook =
                    hooksClass.getMethod(
                            "registerRootHook",
                            rootHookClass
                    );

            registerModelHook.invoke(
                    null,
                    modelHook
            );

            registerRootHook.invoke(
                    null,
                    rootHook
            );

            CraftersNpcAnimations.LOGGER.info(
                    "Hooks de CraftersNpcAnimations conectados a CraftersNpc."
            );

        } catch (Exception exception) {

            CraftersNpcAnimations.LOGGER.error(
                    "Error conectando los hooks de CraftersNpc.",
                    exception
            );
        }
    }
}