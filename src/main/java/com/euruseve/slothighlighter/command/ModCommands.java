package com.euruseve.slothighlighter.command;

import com.euruseve.slothighlighter.gui.ConfigScreen;
import com.mojang.brigadier.CommandDispatcher;
import net.minecraft.client.Minecraft;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
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

        );
    }
}
