package com.euruseve.slothighlighter.mixin.acs;

import com.euruseve.slothighlighter.config.ItemScaleConfig;
import com.euruseve.slothighlighter.render.ItemScaleRenderer;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.world.inventory.Slot;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(AbstractContainerScreen.class)
public class ItemScaleMixin {

    @Inject(method = "renderSlot", at = @At("HEAD"), cancellable = true)
    private void onRenderSlot(GuiGraphics guiGraphics, Slot slot, CallbackInfo ci) {
        if (ItemScaleRenderer.shouldSkipSlotRendering(slot) && ItemScaleConfig.isModScalingEnabled()) {
            ci.cancel();
        }
    }

    @Inject(method = "renderFloatingItem", at = @At("HEAD"), cancellable = true)
    private void onRenderFloatingItem(CallbackInfo ci) {
        if (ItemScaleRenderer.shouldRenderCustomCarriedItem() && ItemScaleConfig.isModScalingEnabled()) {
            ci.cancel();
        }
    }
}
