package org.crafterscr.craftersnpcanimations.compat.craftersnpc;

import net.minecraft.world.entity.Entity;
import net.neoforged.fml.ModList;
import org.crafterscr.craftersnpcanimations.CraftersNpcAnimations;

import java.lang.reflect.Method;

public final class CraftersNpcCompat {

    public static final String MOD_ID =
            "craftersnpc";

    private static final String CNPC_CLASS_NAME =
            "org.crafterscr.craftersnpc.entity.CnpcEntity";

    private static Class<?> cnpcClass;

    private static Method getNpcIdMethod;
    private static Method getAnimationIdMethod;
    private static Method isAnimationLoopingMethod;
    private static Method getAnimationStartMethod;

    private static Method playAnimationMethod;
    private static Method stopAnimationMethod;

    private static boolean initialized;
    private static boolean valid;

    private CraftersNpcCompat() {
    }

    public static boolean isLoaded() {

        return ModList.get()
                .isLoaded(
                        MOD_ID
                );
    }

    private static void initialize() {

        if (initialized) {
            return;
        }

        initialized = true;

        if (!isLoaded()) {
            valid = false;
            return;
        }

        try {

            cnpcClass =
                    Class.forName(
                            CNPC_CLASS_NAME
                    );

            getNpcIdMethod =
                    cnpcClass.getMethod(
                            "getNpcId"
                    );

            getAnimationIdMethod =
                    cnpcClass.getMethod(
                            "getAnimationId"
                    );

            isAnimationLoopingMethod =
                    cnpcClass.getMethod(
                            "isAnimationLooping"
                    );

            getAnimationStartMethod =
                    cnpcClass.getMethod(
                            "getAnimationStart"
                    );

            playAnimationMethod =
                    cnpcClass.getMethod(
                            "playAnimation",
                            String.class,
                            boolean.class
                    );

            stopAnimationMethod =
                    cnpcClass.getMethod(
                            "stopAnimation"
                    );

            valid = true;

            CraftersNpcAnimations.LOGGER.info(
                    "Compatibilidad con CraftersNpc activada."
            );

        } catch (Exception exception) {

            valid = false;

            CraftersNpcAnimations.LOGGER.error(
                    "No se pudo inicializar compatibilidad con CraftersNpc.",
                    exception
            );
        }
    }

    public static boolean isAvailable() {

        initialize();

        return valid;
    }

    public static boolean isCnpc(
            Entity entity
    ) {

        if (!isAvailable()
                || entity == null) {

            return false;
        }

        return cnpcClass.isInstance(
                entity
        );
    }

    public static String getNpcId(
            Entity entity
    ) {

        if (!isCnpc(entity)) {
            return "";
        }

        try {

            return String.valueOf(
                    getNpcIdMethod.invoke(
                            entity
                    )
            );

        } catch (Exception exception) {

            return "";
        }
    }

    public static String getAnimationId(
            Entity entity
    ) {

        if (!isCnpc(entity)) {
            return "";
        }

        try {

            return String.valueOf(
                    getAnimationIdMethod.invoke(
                            entity
                    )
            );

        } catch (Exception exception) {

            return "";
        }
    }

    public static boolean isAnimationLooping(
            Entity entity
    ) {

        if (!isCnpc(entity)) {
            return false;
        }

        try {

            return (boolean)
                    isAnimationLoopingMethod.invoke(
                            entity
                    );

        } catch (Exception exception) {

            return false;
        }
    }

    public static long getAnimationStart(
            Entity entity
    ) {

        if (!isCnpc(entity)) {
            return 0L;
        }

        try {

            return (long)
                    getAnimationStartMethod.invoke(
                            entity
                    );

        } catch (Exception exception) {

            return 0L;
        }
    }

    public static boolean playAnimation(
            Entity entity,
            String animationId,
            boolean loop
    ) {

        if (!isCnpc(entity)) {
            return false;
        }

        try {

            playAnimationMethod.invoke(
                    entity,
                    animationId,
                    loop
            );

            return true;

        } catch (Exception exception) {

            CraftersNpcAnimations.LOGGER.error(
                    "No se pudo iniciar animación en CraftersNpc.",
                    exception
            );

            return false;
        }
    }

    public static boolean stopAnimation(
            Entity entity
    ) {

        if (!isCnpc(entity)) {
            return false;
        }

        try {

            stopAnimationMethod.invoke(
                    entity
            );

            return true;

        } catch (Exception exception) {

            CraftersNpcAnimations.LOGGER.error(
                    "No se pudo detener animación en CraftersNpc.",
                    exception
            );

            return false;
        }
    }
}