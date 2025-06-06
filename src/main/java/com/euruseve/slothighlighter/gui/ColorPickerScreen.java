package com.euruseve.slothighlighter.gui;

import com.euruseve.slothighlighter.config.Config;
import com.euruseve.slothighlighter.config.ColorConfig;
import com.euruseve.slothighlighter.gui.buttons.ColorSlider;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

public class ColorPickerScreen extends Screen {
    private final Screen parentScreen;
    private int red, green, blue;
    private int selectedColor;

    public ColorPickerScreen(Screen parentScreen) {
        super(Component.literal("Color Picker"));

        this.parentScreen = parentScreen;
        this.selectedColor = ColorConfig.getHighlightingColor();
        this.red = (selectedColor >> 16) & 0xFF;
        this.green = (selectedColor >> 8) & 0xFF;
        this.blue = selectedColor & 0xFF;
    }

    @Override
    public void render(GuiGraphics gui, int mouseX, int mouseY, float delta) {

        if (parentScreen != null) {
            parentScreen.render(gui, -1, -1, delta);
        }

        gui.fill(0, 0, this.width, this.height, 0xA0000000);

        int windowWidth = 240;
        int windowHeight = 210;
        int windowX = (this.width - windowWidth) / 2;
        int windowY = (this.height - windowHeight) / 2;

        super.render(gui, mouseX, mouseY, delta);

        renderColorPreview(gui, windowX + 20, windowY, windowWidth - 40, 30);
        renderHexText(gui, windowX + 20, windowY + 35);
    }

    @Override
    protected void init() {
        int windowWidth = 240;
        int windowHeight = 180;
        int windowX = (this.width - windowWidth) / 2;
        int windowY = (this.height - windowHeight) / 2;

        this.addRenderableWidget(new ColorSlider(
                windowX + 20, windowY + 70, windowWidth - 40, 20,
                "Red", red, value -> {
            red = value;
            updateColor();
        }
        ));

        this.addRenderableWidget(new ColorSlider(
                windowX + 20, windowY + 95, windowWidth - 40, 20,
                "Green", green, value -> {
            green = value;
            updateColor();
        }
        ));

        this.addRenderableWidget(new ColorSlider(
                windowX + 20, windowY + 120, windowWidth - 40, 20,
                "Blue", blue, value -> {
            blue = value;
            updateColor();
        }
        ));

        this.addRenderableWidget(Button.builder(
                Component.literal("Reset"),
                button -> resetColor()
        ).bounds(windowX + 20, windowY + 145, 80, 20).build());

        this.addRenderableWidget(Button.builder(
                Component.literal("Cancel"),
                button -> this.onClose()
        ).bounds(windowX + 110, windowY + 145, 50, 20).build());

        this.addRenderableWidget(Button.builder(
                Component.literal("Apply"),
                button -> applyColor()
        ).bounds(windowX + 170, windowY + 145, 50, 20).build());
    }

    private void updateColor() {
        selectedColor = (0xFF << 24) | (red << 16) | (green << 8) | blue;
    }

    private void resetColor() {
        selectedColor = ColorConfig.DEFAULT_COLOR;
        red = (selectedColor >> 16) & 0xFF;
        green = (selectedColor >> 8) & 0xFF;
        blue = selectedColor & 0xFF;

        updateColor();
        this.clearWidgets();
        this.init();
    }

    private void applyColor() {
        ColorConfig.setHighlightingColor(selectedColor);
        Config.HIGHLIGHTING_COLOR.set(selectedColor);
        Config.SPEC.save();

        this.onClose();
    }

    private void renderColorPreview(GuiGraphics gui, int x, int y, int width, int height) {
        gui.fill(x, y, x + width, y + height, selectedColor);

        int lighter = adjustColorBrightness(selectedColor, +30);
        int darker = adjustColorBrightness(selectedColor, -30);

        gui.fill(x, y, x + width, y + 1, lighter);
        gui.fill(x, y, x + 1, y + height, lighter);
        gui.fill(x, y + height - 1, x + width, y + height, darker);
        gui.fill(x + width - 1, y, x + width, y + height, darker);
    }

    private void renderHexText(GuiGraphics gui, int x, int y) {
        String hexText = String.format("#%02X%02X%02X", red, green, blue);
        gui.drawString( Minecraft.getInstance().font, hexText, x, y, 0xFFFFFF);
    }

    private int adjustColorBrightness(int color, int delta) {
        int r = Math.min(255, Math.max(0, ((color >> 16) & 0xFF) + delta));
        int g = Math.min(255, Math.max(0, ((color >> 8) & 0xFF) + delta));
        int b = Math.min(255, Math.max(0, (color & 0xFF) + delta));

        return (0xFF << 24) | (r << 16) | (g << 8) | b;
    }

    @Override
    public void onClose() {
        Minecraft.getInstance().setScreen(parentScreen);
    }

    @Override
    public void resize(Minecraft minecraft, int width, int height) {
        super.resize(minecraft, width, height);
        if (parentScreen != null) {
            parentScreen.resize(minecraft, width, height);
        }
    }
}