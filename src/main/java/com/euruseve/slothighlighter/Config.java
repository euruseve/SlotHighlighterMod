package com.euruseve.slothighlighter;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import com.euruseve.slothighlighter.config.ColorConfig;
import com.euruseve.slothighlighter.config.HighlightConfig;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
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

    public static final ModConfigSpec.IntValue INNER_COLOR = BUILDER
            .comment("Color used to highlight slots (ARGB format, e.g. 0xFFFF0000)")
            .defineInRange("innerColor", ColorConfig.defaultColor, 0xFF000000, 0xFFFFFFFF);

    public static final ModConfigSpec.BooleanValue USE_MOD_HIGHLIGHT = BUILDER
            .comment("Use mod's custom highlight instead of vanilla")
            .define("useModHighlight", true);

    public static final ModConfigSpec SPEC = BUILDER.build();

    public static int innerColor;
    public static boolean useModHighlight;

    @SubscribeEvent
    public static void onLoad(final ModConfigEvent event) {
        innerColor = INNER_COLOR.get();
        useModHighlight = USE_MOD_HIGHLIGHT.get();

        ColorConfig.innerColor = innerColor;
        HighlightConfig.setUseModHighlight(useModHighlight);
    }
}
