package com.euruseve.slothighlighter.render;

import com.euruseve.slothighlighter.SlotHighlighter;
import com.euruseve.slothighlighter.config.ItemScaleConfig;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ScreenEvent;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

@EventBusSubscriber(modid = SlotHighlighter.MODID, value = Dist.CLIENT)
public class ItemScaleRenderer {

    private static class SlotAnimation {
        float scale = 1.0f, target = 1.0f, startScale = 1.0f;
        long startTime = 0;
        boolean animating = false, scalingUp = false;

        void start(float targetScale, boolean up) {
            target = targetScale; scalingUp = up; startTime = System.currentTimeMillis();
            startScale = scale; animating = true;
        }
    }

    private static final float SCALE_HOVER = 1.4f, SCALE_NORMAL = 1.0f;
    private static final float ANIM_IN = 150f, ANIM_OUT = 120f, THRESHOLD = 0.005f;

    private static final Map<Slot, SlotAnimation> animations = new HashMap<>();
    private static Slot hoveredSlot = null;
    private static float carriedScale = 1.0f;
    private static boolean carriedWasScaled = false;

    public static boolean shouldSkipSlotRendering(Slot slot) {
        SlotAnimation anim = animations.get(slot);
        return anim != null && anim.scale != SCALE_NORMAL;
    }

    public static boolean shouldRenderCustomCarriedItem() {
        return Minecraft.getInstance().player != null &&
                !Minecraft.getInstance().player.containerMenu.getCarried().isEmpty();
    }

    @SubscribeEvent
    public static void onScreenClose(ScreenEvent.Closing event) {
        animations.clear(); hoveredSlot = null; carriedScale = 1.0f; carriedWasScaled = false;
    }

    @SubscribeEvent
    public static void onRenderScreenPost(ScreenEvent.Render.Post event) {
        if (!(event.getScreen() instanceof AbstractContainerScreen<?> screen) ||
                !ItemScaleConfig.isModScalingEnabled())
            return;

        Minecraft mc = Minecraft.getInstance();
        double mouseX = mc.mouseHandler.xpos() * mc.getWindow().getGuiScaledWidth() / mc.getWindow().getScreenWidth();
        double mouseY = mc.mouseHandler.ypos() * mc.getWindow().getGuiScaledHeight() / mc.getWindow().getScreenHeight();

        ItemStack carried = mc.player.containerMenu.getCarried();
        boolean holdingItem = !carried.isEmpty();

        Slot currentHovered = screen.getMenu().slots.stream()
                .filter(slot -> isMouseOver(slot, mouseX, mouseY, screen.getGuiLeft(), screen.getGuiTop()))
                .findFirst().orElse(null);

        if (currentHovered != hoveredSlot) {
            if (hoveredSlot != null) startAnimation(hoveredSlot, SCALE_NORMAL, false);
            if (currentHovered != null) startAnimation(currentHovered, SCALE_HOVER, true);
            hoveredSlot = currentHovered;
        }

        updateAnimations();

        if (holdingItem && currentHovered != null && !carriedWasScaled)
            carriedWasScaled = true;

        if (!holdingItem)
            carriedWasScaled = false;

        float targetCarried = (holdingItem && (carriedWasScaled || currentHovered != null)) ? SCALE_HOVER * 0.95f : SCALE_NORMAL;
        carriedScale = lerp(carriedScale, targetCarried, Math.max(0.15f, Math.min(0.45f, Math.abs(targetCarried - carriedScale) * 2.0f)));

        renderSlots(screen, event.getGuiGraphics());
        if (holdingItem)
            renderCarried(event.getGuiGraphics(), carried, mouseX, mouseY);
    }

    private static void startAnimation(Slot slot, float target, boolean up) {
        animations.computeIfAbsent(slot, k -> new SlotAnimation()).start(target, up);
    }

    private static void updateAnimations() {
        Iterator<Map.Entry<Slot, SlotAnimation>> it = animations.entrySet().iterator();
        while (it.hasNext()) {
            SlotAnimation anim = it.next().getValue();
            if (anim.animating) {
                float elapsed = System.currentTimeMillis() - anim.startTime;
                float duration = Math.max((anim.scalingUp ? ANIM_IN : ANIM_OUT) * 0.4f,
                        (anim.scalingUp ? ANIM_IN : ANIM_OUT) * Math.abs(anim.target - anim.startScale) / (SCALE_HOVER - SCALE_NORMAL));

                float progress = Math.min(elapsed / duration, 1.0f);
                float ease = anim.scalingUp ?
                        1.0f - (float)Math.pow(1.0f - progress, 1.6f) + (progress > 0.85f && duration > ANIM_IN * 0.6f ?
                                (float)Math.sin((progress - 0.85f) / 0.15f * Math.PI) * 0.03f : 0) :
                        (float)Math.pow(progress, 1.8f);

                anim.scale = Math.max(SCALE_NORMAL, Math.min(anim.startScale + (anim.target - anim.startScale) * ease, SCALE_HOVER * 1.05f));

                if (progress >= 1.0f) { anim.scale = anim.target; anim.animating = false; }
            }
            if (!anim.animating && Math.abs(anim.scale - SCALE_NORMAL) <= THRESHOLD) it.remove();
        }
    }

    private static void renderSlots(AbstractContainerScreen<?> screen, GuiGraphics graphics) {
        PoseStack pose = graphics.pose();
        for (Slot slot : screen.getMenu().slots) {
            if (!slot.isActive() || slot.getItem().isEmpty()) continue;
            SlotAnimation anim = animations.get(slot);
            if (anim != null && anim.scale != SCALE_NORMAL) {
                int x = screen.getGuiLeft() + slot.x, y = screen.getGuiTop() + slot.y;
                pose.pushPose();
                float center = 8;
                pose.translate(x + center, y + center, 200);
                pose.scale(anim.scale, anim.scale, 1.0f);
                pose.translate(-x - center, -y - center, 0);
                renderItem(graphics, slot.getItem(), x, y);
                pose.popPose();
            }
        }
    }

    private static void renderCarried(GuiGraphics graphics, ItemStack item, double mouseX, double mouseY) {
        PoseStack pose = graphics.pose();
        pose.pushPose();
        int x = (int)mouseX - 8, y = (int)mouseY - 8;
        pose.translate(x + 8, y + 8, 300);
        pose.scale(carriedScale, carriedScale, 1.0f);
        pose.translate(-x - 8, -y - 8, 0);
        renderItem(graphics, item, x, y);
        pose.popPose();
    }

    private static void renderItem(GuiGraphics graphics, ItemStack item, int x, int y) {
        graphics.renderItem(item, x, y);
        graphics.renderItemDecorations(Minecraft.getInstance().font, item, x, y);
    }

    private static boolean isMouseOver(Slot slot, double mouseX, double mouseY, int guiLeft, int guiTop) {
        int x = guiLeft + slot.x, y = guiTop + slot.y;
        return mouseX >= x && mouseX < x + 16 && mouseY >= y && mouseY < y + 16;
    }

    private static float lerp(float start, float end, float alpha) {
        return start + alpha * (end - start);
    }
}