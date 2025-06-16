package com.euruseve.slothighlighter.gui;

import com.euruseve.slothighlighter.config.Config;
import com.euruseve.slothighlighter.config.HighlightConfig;
import com.euruseve.slothighlighter.config.ItemScaleConfig;
import com.euruseve.slothighlighter.gui.buttons.ColorPreviewButton;
import com.euruseve.slothighlighter.gui.buttons.TitledButton;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.StringWidget;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

public class ConfigScreen extends Screen {

    private final int HEADER_HEIGHT = 25;
    private final int HEADER_COLOR = 0xCE171010;
    private final int HEADER_BORDER_COLOR = 0xCE2B2B2B;

    private StringWidget _header;

    public ConfigScreen() {
        super(Component.literal("Config Screen"));
    }

    @Override
    protected void init() {
        super.init();

        initHeader();
        initButtons();
        initExperimental();
    }

    private void initHeader() {

        _header = new StringWidget(
                this.width,
                HEADER_HEIGHT,
                Component.literal("Slot Highlighter Config"),
                this.font
        );

        _header.alignCenter();

        addRenderableWidget(_header);
    }

    private void initButtons(){

        int buttonWidth = 300;
        int buttonX = (this.width - buttonWidth) / 2;

        this.addRenderableWidget(new TitledButton(
                buttonX, 40, buttonWidth, 20,
                Component.literal("Mod Highlighting"),
                Component.literal(
                        HighlightConfig.isModHighlightEnabled() ? "True" : "False"
                ),
                button -> {
                    boolean highlight = !HighlightConfig.isModHighlightEnabled();

                    HighlightConfig.setModHighlight(highlight);
                    Config.USE_MOD_HIGHLIGHT.set(highlight);
                    Config.SPEC.save();

                    button.setMessage(Component.literal(
                            HighlightConfig.isModHighlightEnabled() ? "True" : "False"
                    ));
                }
        ));

        this.addRenderableWidget(new ColorPreviewButton(
                buttonX, 70, buttonWidth, 20,
                Component.literal("Highlighting Color"),
                button -> {
                    Minecraft.getInstance().setScreen(new ColorPickerScreen(this));
                }
        ));
    }

    private void initExperimental() {
        if(!Config.USE_EXPERIMENTAL_FEATURES.get())
            return;

        int buttonWidth = 300;
        int buttonX = (this.width - buttonWidth) / 2;

        this.addRenderableWidget(new AbstractWidget(buttonX, 110, buttonWidth, 20,
                Component.literal("EXPERIMENTAL FEATURES")) {
            @Override
            public void renderWidget(GuiGraphics gui, int mouseX, int mouseY, float delta) {
                gui.drawCenteredString(
                        Minecraft.getInstance().font,
                        this.getMessage(),
                        this.getX() + this.getWidth() / 2,
                        this.getY() + 6,
                        0xFFFFFF
                );
            }

            @Override
            protected void updateWidgetNarration(NarrationElementOutput output) {
                defaultButtonNarrationText(output);
            }
        });

        this.addRenderableWidget(new TitledButton(
                buttonX, 130, buttonWidth, 20,
                Component.literal("Mod Item Scaling"),
                Component.literal(
                        ItemScaleConfig.isModScalingEnabled() ? "True" : "False"
                ),
                button -> {
                    boolean highlight = !ItemScaleConfig.isModScalingEnabled();

                    ItemScaleConfig.setModScaling(highlight);

                    button.setMessage(Component.literal(
                            ItemScaleConfig.isModScalingEnabled() ? "True" : "False"
                    ));
                }
        ));

    }

    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        super.render(guiGraphics, mouseX, mouseY, partialTick);

        renderHeaderBg(guiGraphics);
        renderBg(guiGraphics);
        renderFooterBg(guiGraphics);
    }

    private void renderHeaderBg(GuiGraphics guiGraphics) {
        guiGraphics.fill(0, 0, this.width, HEADER_HEIGHT, HEADER_COLOR);
        guiGraphics.fill(0, HEADER_HEIGHT - 1, this.width, HEADER_HEIGHT, HEADER_BORDER_COLOR);
    }

    private void renderFooterBg(GuiGraphics guiGraphics) {
        int y = this.height - HEADER_HEIGHT;
        guiGraphics.fill(0, y, this.width, y + HEADER_HEIGHT, HEADER_COLOR);
        guiGraphics.fill(0, y, this.width, y + 1, HEADER_BORDER_COLOR);
    }

    private void renderBg(GuiGraphics guiGraphics) {
        final int color = 0x882B2B2B;

        guiGraphics.fill(0, HEADER_HEIGHT, this.width, this.height - HEADER_HEIGHT, color);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        return super.mouseClicked(mouseX, mouseY, button);
    }
}