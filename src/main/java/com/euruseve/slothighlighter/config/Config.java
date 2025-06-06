package com.euruseve.slothighlighter.config;

import com.euruseve.slothighlighter.SlotHighlighter;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.event.config.ModConfigEvent;
import net.neoforged.neoforge.common.ModConfigSpec;

// An example config class. This is not required, but it's a good idea to have one to keep your config organized.
// Demonstrates how to use Neo's config APIs
@EventBusSubscriber(modid = SlotHighlighter.MODID, bus = EventBusSubscriber.Bus.MOD)
public class Config
{
    private static final ModConfigSpec.Builder BUILDER = new ModConfigSpec.Builder();

    public static final ModConfigSpec.IntValue HIGHLIGHTING_COLOR = BUILDER
            .comment("Color used to highlight slots (ARGB format, e.g. 0xFFFF0000)")
            .defineInRange("innerColor", ColorConfig.DEFAULT_COLOR, 0xFF000000, 0xFFFFFFFF);

    public static final ModConfigSpec.BooleanValue USE_MOD_HIGHLIGHT = BUILDER
            .comment("Use mod's custom highlight instead of default")
            .define("useModHighlight", true);

    public static final ModConfigSpec SPEC = BUILDER.build();

    @SubscribeEvent
    public static void onLoad(final ModConfigEvent event) {

        ColorConfig.setHighlightingColor(HIGHLIGHTING_COLOR.get());
        HighlightConfig.setModHighlight(USE_MOD_HIGHLIGHT.get());

    }
}
