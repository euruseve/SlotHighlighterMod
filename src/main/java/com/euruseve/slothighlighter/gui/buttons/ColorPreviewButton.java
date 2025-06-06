package com.euruseve.slothighlighter.gui.buttons;

import com.euruseve.slothighlighter.config.ColorConfig;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.network.chat.Component;

public class ColorPreviewButton extends TitledButton {

    public ColorPreviewButton(int x, int y, int width, int height,
                              Component title, Button.OnPress onPress) {
        super(x, y, width, height, title,
                Component.literal(getCurrentHexColor()),
                onPress);
    }

    @Override
    public void renderWidget(GuiGraphics gui, int mouseX, int mouseY, float delta) {
        this.button.setMessage(Component.literal(getCurrentHexColor()));

        super.renderWidget(gui, mouseX, mouseY, delta);
    }

    private static String getCurrentHexColor() {
        return String.format("#%06X", ColorConfig.getHighlightingColor() & 0xFFFFFF);
    }

}
