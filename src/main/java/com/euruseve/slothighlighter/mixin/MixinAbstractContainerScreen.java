package com.euruseve.slothighlighter.mixin;

import com.euruseve.slothighlighter.config.HighlightConfig;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.world.inventory.Slot;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(AbstractContainerScreen.class)
public class MixinAbstractContainerScreen
{

    @Inject(
            method = "renderSlotHighlight(Lnet/minecraft/client/gui/GuiGraphics;Lnet/minecraft/world/inventory/Slot;IIF)V",
            at = @At("HEAD"),
            cancellable = true
    )
    private void disableVanillaSlotHighlight(GuiGraphics guiGraphics, Slot slot, int mouseX, int mouseY, float partialTick, CallbackInfo ci) {
        if (HighlightConfig.isModHighlightEnabled()) {
            ci.cancel();
        }
    }
}
