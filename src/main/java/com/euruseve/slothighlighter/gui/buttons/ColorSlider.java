package com.euruseve.slothighlighter.gui.buttons;

import net.minecraft.client.gui.components.AbstractSliderButton;
import net.minecraft.network.chat.Component;

public class ColorSlider extends AbstractSliderButton {

    private final String label;
    private final ValueCallback callback;

    public ColorSlider(int x, int y, int width, int height, String label, int initialValue, ValueCallback callback) {
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
