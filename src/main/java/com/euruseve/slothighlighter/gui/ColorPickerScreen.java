package com.euruseve.slothighlighter.gui;

import com.euruseve.slothighlighter.Config;
import com.euruseve.slothighlighter.config.ColorConfig;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractSliderButton;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

public class ColorPickerScreen extends Screen {

    private int red = (ColorConfig.innerColor >> 16) & 0xFF;
    private int green = (ColorConfig.innerColor >> 8) & 0xFF;
    private int blue = ColorConfig.innerColor & 0xFF;

    private int selectedColor = ColorConfig.innerColor;

    public ColorPickerScreen() {
        super(Component.literal("Color Picker"));
    }

    @Override
    protected void init() {
        super.init();

        initSliders();
        initButtons();
    }

    @Override
    public void render(GuiGraphics gui, int mouseX, int mouseY, float delta) {
        super.render(gui, mouseX, mouseY, delta);

        gui.drawCenteredString(this.font, "Highlighting Color Picker", this.width / 2, 20, 0xFFFFFF);

        renderColorPreview(gui);
        renderHexText(gui);
    }

    private void initSliders() {
        int startX = this.width / 2 - 110;
        int y = this.height / 2 - 60;

        this.addRenderableWidget(new RGBSlider(startX, y, 220, 20, "Red", red, value -> {
            red = value;
            updateColor();
        }));

        this.addRenderableWidget(new RGBSlider(startX, y + 30, 220, 20, "Green", green, value -> {
            green = value;
            updateColor();
        }));

        this.addRenderableWidget(new RGBSlider(startX, y + 60, 220, 20, "Blue", blue, value -> {
            blue = value;
            updateColor();
        }));
    }

    private void updateColor() {
        selectedColor = (0xFF << 24) | (red << 16) | (green << 8) | blue;
    }

    private void initButtons() {
        int y = this.height / 2 + 40;
        int buttonWidth = 90;
        int spacing = 10;

        this.addRenderableWidget(Button.builder(
                        Component.literal("Reset"),
                        button -> {
                            ColorConfig.innerColor = ColorConfig.defaultColor;
                            selectedColor = ColorConfig.defaultColor;
                            red = (selectedColor >> 16) & 0xFF;
                            green = (selectedColor >> 8) & 0xFF;
                            blue = selectedColor & 0xFF;

                            Config.INNER_COLOR.set(ColorConfig.defaultColor);
                            Config.SPEC.save();
                        })
                .bounds(this.width / 2 - buttonWidth - spacing / 2, y, buttonWidth, 20)
                .build());

        this.addRenderableWidget(Button.builder(
                        Component.literal("Apply"),
                        button -> {
                            ColorConfig.innerColor = selectedColor;
                            Config.INNER_COLOR.set(ColorConfig.innerColor);
                            Config.SPEC.save();
                            this.onClose();
                        })
                .bounds(this.width / 2 + spacing / 2, y, buttonWidth, 20)
                .build());
    }

    private void renderColorPreview(GuiGraphics gui) {
        int rectX1 = this.width / 2 - 50;
        int rectX2 = this.width / 2 + 50;
        int rectY1 = this.height / 2 - 110;
        int rectY2 = this.height / 2 - 80;

        gui.fill(rectX1, rectY1, rectX2, rectY2, selectedColor);

        int lighter = adjustColorBrightness(selectedColor, +30);
        int darker = adjustColorBrightness(selectedColor, -30);
        int midTone = blendColors(lighter, darker);

        // Top and Left
        gui.fill(rectX1 + 1, rectY1, rectX2 - 1, rectY1 + 1, lighter);
        gui.fill(rectX1, rectY1 + 1, rectX1 + 1, rectY2 - 1, lighter);

        // Bottom and Right
        gui.fill(rectX1 + 1, rectY2 - 1, rectX2 - 1, rectY2, darker);
        gui.fill(rectX2 - 1, rectY1 + 1, rectX2, rectY2 - 1, darker);

        // Corners
        gui.fill(rectX1, rectY1, rectX1 + 1, rectY1 + 1, midTone);
        gui.fill(rectX2 - 1, rectY2 - 1, rectX2, rectY2, midTone);
    }

    private void renderHexText(GuiGraphics gui) {
        String hexText = String.format("#%02X%02X%02X", red, green, blue);
        int rectX1 = this.width / 2 - 50;
        int rectX2 = this.width / 2 + 50;
        int rectY1 = this.height / 2 - 110;
        int rectY2 = this.height / 2 - 80;

        int textWidth = this.font.width(hexText);
        int textX = (rectX1 + rectX2) / 2 - textWidth / 2;
        int textY = (rectY1 + rectY2) / 2 - this.font.lineHeight / 2;

        gui.drawString(this.font, hexText, textX, textY, 0xFFFFFFFF, true);
    }

    private int adjustColorBrightness(int color, int delta) {
        int a = (color >> 24) & 0xFF;
        int r = Math.min(255, Math.max(0, ((color >> 16) & 0xFF) + delta));
        int g = Math.min(255, Math.max(0, ((color >> 8) & 0xFF) + delta));
        int b = Math.min(255, Math.max(0, (color & 0xFF) + delta));
        return (a << 24) | (r << 16) | (g << 8) | b;
    }

    private int blendColors(int color1, int color2) {
        int a = ((color1 >> 24) & 0xFF + (color2 >> 24) & 0xFF) / 2;
        int r = (((color1 >> 16) & 0xFF) + ((color2 >> 16) & 0xFF)) / 2;
        int g = (((color1 >> 8) & 0xFF) + ((color2 >> 8) & 0xFF)) / 2;
        int b = ((color1 & 0xFF) + (color2 & 0xFF)) / 2;
        return (a << 24) | (r << 16) | (g << 8) | b;
    }

    private static class RGBSlider extends AbstractSliderButton {

        private final String label;
        private final ValueCallback callback;

        public RGBSlider(int x, int y, int width, int height, String label, int initialValue, ValueCallback callback) {
            super(x, y, width, height, Component.literal(label + ": " + initialValue), initialValue / 255.0);
            this.label = label;
            this.callback = callback;
        }

        @Override
        protected void updateMessage() {
            int value = (int) (this.value * 255);
            this.setMessage(Component.literal(label + ": " + value));
        }

        @Override
        protected void applyValue() {
            int value = (int) (this.value * 255);
            callback.onValueChanged(value);
        }

        public interface ValueCallback {
            void onValueChanged(int value);
        }
    }
}
