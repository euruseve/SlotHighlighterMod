package com.euruseve.slothighlighter;

import com.euruseve.slothighlighter.command.ModCommands;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.RegisterCommandsEvent;
import org.slf4j.Logger;

import com.mojang.logging.LogUtils;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;

// The value here should match an entry in the META-INF/neoforge.mods.toml file
@Mod(SlotHighlighter.MODID)
public class SlotHighlighter
{
    // Define mod id in a common place for everything to reference
    public static final String MODID = "slothighlighter";
    // Directly reference a slf4j logger
    private static final Logger LOGGER = LogUtils.getLogger();

    public SlotHighlighter(IEventBus modEventBus, ModContainer modContainer)
    {
        modContainer.registerConfig(ModConfig.Type.COMMON, Config.SPEC);

        NeoForge.EVENT_BUS.addListener(this::onRegisterCommands);
    }


    private void onRegisterCommands(RegisterCommandsEvent event) {
        ModCommands.register(event.getDispatcher());
    }
}
