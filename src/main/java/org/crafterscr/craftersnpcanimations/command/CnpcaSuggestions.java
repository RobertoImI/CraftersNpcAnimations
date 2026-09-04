package org.crafterscr.craftersnpcanimations.command;

import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;

import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.world.entity.Entity;

import org.crafterscr.craftersnpcanimations.animation.emote.NpcEmoteRegistry;
import org.crafterscr.craftersnpcanimations.compat.craftersnpc.CraftersNpcCompat;
import org.crafterscr.craftersnpcanimations.entity.AnimatedNpcEntity;

import java.util.Set;
import java.util.TreeSet;
import java.util.concurrent.CompletableFuture;

public final class CnpcaSuggestions {

    private CnpcaSuggestions() {
    }

    /*
     * ==========================================
     * NPCs propios de CraftersNpcAnimations
     * ==========================================
     */
    public static CompletableFuture<Suggestions> suggestAnimatedNpcIds(
            CommandContext<CommandSourceStack> context,
            SuggestionsBuilder builder
    ) {

        Set<String> ids =
                new TreeSet<>(
                        String.CASE_INSENSITIVE_ORDER
                );

        for (
                Entity entity :
                context.getSource()
                        .getLevel()
                        .getAllEntities()
        ) {

            if (
                    entity instanceof AnimatedNpcEntity npc
            ) {

                String id =
                        npc.getNpcId();

                if (
                        id != null
                                && !id.isBlank()
                ) {

                    ids.add(
                            id
                    );
                }
            }
        }

        return SharedSuggestionProvider.suggest(
                ids,
                builder
        );
    }

    /*
     * ==========================================
     * NPCs normales de CraftersNpc
     * ==========================================
     */
    public static CompletableFuture<Suggestions> suggestCnpcIds(
            CommandContext<CommandSourceStack> context,
            SuggestionsBuilder builder
    ) {

        Set<String> ids =
                new TreeSet<>(
                        String.CASE_INSENSITIVE_ORDER
                );

        /*
         * Si CraftersNpc no está instalado,
         * simplemente no devolvemos sugerencias.
         */
        if (!CraftersNpcCompat.isAvailable()) {

            return SharedSuggestionProvider.suggest(
                    ids,
                    builder
            );
        }

        for (
                Entity entity :
                context.getSource()
                        .getLevel()
                        .getAllEntities()
        ) {

            if (
                    CraftersNpcCompat.isCnpc(
                            entity
                    )
            ) {

                String id =
                        CraftersNpcCompat.getNpcId(
                                entity
                        );

                if (
                        id != null
                                && !id.isBlank()
                ) {

                    ids.add(
                            id
                    );
                }
            }
        }

        return SharedSuggestionProvider.suggest(
                ids,
                builder
        );
    }

    /*
     * ==========================================
     * Emotes cargados
     * ==========================================
     */
    public static CompletableFuture<Suggestions> suggestEmoteIds(
            CommandContext<CommandSourceStack> context,
            SuggestionsBuilder builder
    ) {

        return SharedSuggestionProvider.suggest(
                NpcEmoteRegistry.ids(),
                builder
        );
    }
}