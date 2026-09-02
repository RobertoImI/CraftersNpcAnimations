package org.crafterscr.craftersnpcanimations.animation.emote;

import org.crafterscr.craftersnpcanimations.CraftersNpcAnimations;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Locale;
import java.util.stream.Stream;

public final class NpcEmoteLoader {

    private static Path emoteDirectory;

    private NpcEmoteLoader() {
    }

    public static void initialize(
            Path gameDirectory
    ) {

        emoteDirectory =
                gameDirectory.resolve(
                        "emotes"
                );

        reload();
    }

    public static void reload() {

        if (emoteDirectory == null) {

            CraftersNpcAnimations.LOGGER.warn(
                    "La carpeta de emotes todavía no está inicializada."
            );

            return;
        }

        try {

            Files.createDirectories(
                    emoteDirectory
            );

        } catch (IOException exception) {

            CraftersNpcAnimations.LOGGER.error(
                    "No se pudo crear la carpeta de emotes.",
                    exception
            );

            return;
        }

        NpcEmoteRegistry.clear();

        try (Stream<Path> files =
                     Files.list(
                             emoteDirectory
                     )) {

            files
                    .filter(Files::isRegularFile)
                    .filter(NpcEmoteLoader::isJson)
                    .sorted()
                    .forEach(
                            NpcEmoteLoader::loadFile
                    );

        } catch (IOException exception) {

            CraftersNpcAnimations.LOGGER.error(
                    "Error leyendo /emotes.",
                    exception
            );
        }

        CraftersNpcAnimations.LOGGER.info(
                "Biblioteca cargada: {} emote(s).",
                NpcEmoteRegistry.size()
        );
    }

    private static void loadFile(
            Path file
    ) {

        try {

            NpcEmote emote =
                    NpcEmoteParser.parse(
                            file
                    );

            NpcEmoteRegistry.register(
                    emote
            );

            CraftersNpcAnimations.LOGGER.info(
                    "Emote cargado correctamente: {}",
                    emote.id()
            );

        } catch (Exception exception) {

            CraftersNpcAnimations.LOGGER.error(
                    "No se pudo cargar el emote: {}",
                    file.getFileName(),
                    exception
            );
        }
    }

    private static boolean isJson(
            Path path
    ) {

        return path
                .getFileName()
                .toString()
                .toLowerCase(Locale.ROOT)
                .endsWith(".json");
    }

    public static Path getEmoteDirectory() {
        return emoteDirectory;
    }
}