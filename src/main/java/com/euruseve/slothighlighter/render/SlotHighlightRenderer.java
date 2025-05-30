package com.euruseve.slothighlighter.render;

import com.euruseve.slothighlighter.SlotHighlighter;
import com.euruseve.slothighlighter.config.ColorConfig;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ScreenEvent;

@EventBusSubscriber(modid = SlotHighlighter.MODID, value = Dist.CLIENT)
public class SlotHighlightRenderer {

//    private static final int INNER_HIGHLIGHT_COLOR = 0xFFA5D977;
//    private static final int OUTER_BORDER_COLOR = 0xFAFDFFFD;
    private static final int BORDER_THICKNESS = 2;

    @SubscribeEvent
    public static void onRenderScreenPost(ScreenEvent.Render.Post event) {
        if (!(event.getScreen() instanceof AbstractContainerScreen<?> screen)) return;

        Minecraft mc = Minecraft.getInstance();
        GuiGraphics guiGraphics = event.getGuiGraphics();
        ItemStack carried = mc.player.containerMenu.getCarried();

        for (Slot slot : screen.getMenu().slots) {
            if (isMouseOverSlot(slot, event.getMouseX(), event.getMouseY(), screen.getGuiLeft(), screen.getGuiTop())) {
                int x = screen.getGuiLeft() + slot.x;
                int y = screen.getGuiTop() + slot.y;
                drawSlotHighlight(guiGraphics, slot, x, y, !carried.isEmpty());
            }
        }
    }

    private static boolean isMouseOverSlot(Slot slot, double mouseX, double mouseY, int guiLeft, int guiTop) {
        int x = guiLeft + slot.x;
        int y = guiTop + slot.y;
        return mouseX >= x && mouseX < x + 16 && mouseY >= y && mouseY < y + 16;
    }

    private static void drawSlotHighlight(GuiGraphics graphics, Slot slot, int x, int y, boolean isHoldingItem) {
        RenderSystem.disableDepthTest();
        RenderSystem.enableBlend();
        RenderSystem.setShader(GameRenderer::getPositionColorShader);

        if (!slot.getClass().getName().contains("ResultSlot")) {
            graphics.fill(
                    x - BORDER_THICKNESS,
                    y - BORDER_THICKNESS,
                    x + 16 + BORDER_THICKNESS,
                    y + 16 + BORDER_THICKNESS,
                    ColorConfig.BORDER_COLOR
            );
        }

        graphics.fill(x - 1, y - 1, x + 17, y + 17, ColorConfig.INNER_COLOR);

        RenderSystem.disableBlend();
        RenderSystem.enableDepthTest();
    }
}
