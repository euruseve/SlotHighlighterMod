package com.euruseve.slothighlighter.command;

import com.euruseve.slothighlighter.config.ColorConfig;
import com.euruseve.slothighlighter.config.HighlightConfig;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.TextColor;

public class ModCommands
{
    public static void register(CommandDispatcher<CommandSourceStack> dispatcher)
    {
        dispatcher.register(
                Commands.literal("slothl")
                        .then(Commands.literal("setHighlightingColor")
                                .then(Commands.argument("hex", StringArgumentType.word())
                                        .executes(ModCommands::setHighlightingColorCommand)
                                )
                        )
                        .then(Commands.literal("setHighlightVariant")
                                .then(Commands.argument("variant", StringArgumentType.word())
                                        .suggests((ctx, builder) -> {
                                            builder.suggest("vanilla");
                                            builder.suggest("mod");
                                            return builder.buildFuture();
                                        })
                                        .executes(ModCommands::setHighlightVariantCommand)
                                )
                        )
        );
    }

    private static int setHighlightingColorCommand(CommandContext<CommandSourceStack> ctx) {
        String hexInput = StringArgumentType.getString(ctx, "hex").replace("#", "").toUpperCase();
        if (hexInput.length() != 6) {
            return fail(ctx, "Color must be in HEX format.");
        }

        try {
            String argbHex = "FF" + hexInput;
            int argb = (int) Long.parseLong(argbHex, 16);
            int rgb = Integer.parseInt(hexInput, 16);

            ColorConfig.INNER_COLOR = argb;

            Component colorSquare = Component.literal("\u25A0")
                    .setStyle(Style.EMPTY.withColor(TextColor.fromRgb(rgb)));

            ctx.getSource().sendSuccess(() ->
                    Component.literal("Highlight color changed to: ").append(colorSquare), false);

            return 1;
        } catch (NumberFormatException e) {
            return fail(ctx, "Invalid color code: " + hexInput);
        }
    }

    private static int setHighlightVariantCommand(CommandContext<CommandSourceStack> ctx) {

        String variant = StringArgumentType.getString(ctx, "variant");
        boolean useMod;

        switch (variant.toLowerCase()) {
            case "vanilla" -> useMod = false;
            case "mod" -> useMod = true;
            default -> {
                ctx.getSource().sendFailure(Component.literal("Invalid option. Use 'vanilla' or 'mod'."));
                return 0;
            }
        }

        HighlightConfig.setUseModHighlight(useMod);

        ctx.getSource().sendSuccess(() ->
                        Component.literal("Highlight variant set to: ")
                                .append(Component.literal(variant.toUpperCase())
                                        .withStyle(Style.EMPTY.withColor(useMod ? 0x55FF55 : 0xAAAAFF))),
                false
        );

        return 1;
    }


    private static int fail(CommandContext<CommandSourceStack> ctx, String message) {
        ctx.getSource().sendFailure(Component.literal(message));
        return 0;
    }
}
