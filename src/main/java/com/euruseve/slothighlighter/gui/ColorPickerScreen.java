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

        /// Sliders

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

        /// Buttons

        int buttonY = y + 100;
        int buttonWidth = 90;
        int buttonSpacing = 10;

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
                .bounds(this.width / 2 - buttonWidth - buttonSpacing / 2, buttonY, buttonWidth, 20)
                .build());

        this.addRenderableWidget(Button.builder(
                        Component.literal("Apply"),
                        button -> {
                            ColorConfig.innerColor = selectedColor;

                            Config.INNER_COLOR.set(ColorConfig.innerColor);
                            Config.SPEC.save();

                            this.onClose();
                        })
                .bounds(this.width / 2 + buttonSpacing / 2, buttonY, buttonWidth, 20)
                .build());

    }

    private void updateColor() {
        selectedColor = (0xFF << 24) | (red << 16) | (green << 8) | blue;
    }

    @Override
    public void render(GuiGraphics gui, int mouseX, int mouseY, float delta) {
        super.render(gui, mouseX, mouseY, delta);

        gui.drawCenteredString(this.font, "Highlighting Color Picker", this.width / 2, 20, 0xFFFFFF);


        int rectX1 = this.width / 2 - 50;
        int rectX2 = this.width / 2 + 50;

        int rectY1 = this.height / 2 - 110;
        int rectY2 = this.height / 2 - 80;

        gui.fill(rectX1, rectY1, rectX2, rectY2, selectedColor);

        gui.fill(rectX1, rectY1, rectX2, rectY1 + 1, 0xFF000000);
        gui.fill(rectX1, rectY2 - 1, rectX2, rectY2, 0xFF000000);
        gui.fill(rectX1, rectY1, rectX1 + 1, rectY2, 0xFF000000);
        gui.fill(rectX2 - 1, rectY1, rectX2, rectY2, 0xFF000000);

        String hexText = String.format("#%02X%02X%02X", red, green, blue);

        int textWidth = this.font.width(hexText);
        int textX = (rectX1 + rectX2) / 2 - textWidth / 2;
        int textY = (rectY1 + rectY2) / 2 - this.font.lineHeight / 2;

        gui.drawString(this.font, hexText, textX, textY, 0xFFFFFFFF, true);
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
