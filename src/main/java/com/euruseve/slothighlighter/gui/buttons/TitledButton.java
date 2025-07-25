package com.euruseve.slothighlighter.gui.buttons;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.network.chat.Component;

public class TitledButton extends AbstractWidget {
    protected final Component text;
    protected final Button.OnPress onPress;
    protected final Button button;

    public TitledButton(int x, int y, int width, int height,
                        Component text, Component btnText, Button.OnPress onPress) {
        super(x, y, width, height, text);
        this.text = text;
        this.onPress = onPress;

        int buttonWidth = width / 3;
        this.button = Button.builder(btnText, onPress)
                .bounds(x + width - buttonWidth, y, buttonWidth, height)
                .build();
    }

    @Override
    public void renderWidget(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {

        int textColor = this.active ? 0xFFFFFF : 0xA0A0A0;
        guiGraphics.drawString(Minecraft.getInstance().font, this.text,
                this.getX() + 5, // Відступ від лівого краю
                this.getY() + (this.getHeight() - 8) / 2,
                textColor);

        this.button.render(guiGraphics, mouseX, mouseY, partialTick);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        return this.button.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public boolean isMouseOver(double mouseX, double mouseY) {
        return mouseX >= this.getX() && mouseY >= this.getY() &&
                mouseX < this.getX() + this.getWidth() &&
                mouseY < this.getY() + this.getHeight();
    }

    @Override
    protected void updateWidgetNarration(NarrationElementOutput narrationElementOutput) {
        this.defaultButtonNarrationText(narrationElementOutput);
    }
}