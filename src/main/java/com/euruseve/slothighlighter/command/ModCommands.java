package com.euruseve.slothighlighter.command;

import com.euruseve.slothighlighter.config.Config;
import com.euruseve.slothighlighter.gui.ConfigScreen;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import net.minecraft.client.Minecraft;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;

public class ModCommands {
    public static void register(CommandDispatcher<CommandSourceStack> dispatcher)
    {
        dispatcher.register(
                Commands.literal("shl")
                        .then(Commands.literal("gui")
                                .executes(ctx -> {
                                    if (ctx.getSource().getEntity() instanceof Player player) {
                                        Minecraft.getInstance().execute(() -> {
                                            Minecraft.getInstance().setScreen(new ConfigScreen());
                                        });
                                    }
                                    return 1;
                                })
                        )
                        .then(Commands.literal("experimental")
                                        .then(Commands.argument("enable", StringArgumentType.word())
                                        .suggests((ctx, builder) -> {
                                            builder.suggest("false");
                                            builder.suggest("true");
                                            return builder.buildFuture();
                                        })
                                        .executes(ModCommands::setExperimentalFeature)
                                )
                        )

        );
    }

    private static int setExperimentalFeature(CommandContext<CommandSourceStack> ctx) {
        String variant = StringArgumentType.getString(ctx, "enable");
        boolean useMod;

        switch (variant.toLowerCase()) {
            case "false" -> useMod = false;
            case "true" -> useMod = true;
            default -> {
                ctx.getSource().sendFailure(Component.literal("Invalid option. Use 'true' or 'false'."));
                return 0;
            }
        }

        Config.USE_EXPERIMENTAL_FEATURES.set(useMod);
        Config.SPEC.save();

        return 1;
    }

}
