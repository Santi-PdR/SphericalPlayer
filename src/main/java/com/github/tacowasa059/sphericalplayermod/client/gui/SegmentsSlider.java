package com.github.tacowasa059.sphericalplayermod.client.gui;

import com.github.tacowasa059.sphericalplayermod.client.config.ClientConfig;
import net.minecraft.client.gui.components.AbstractSliderButton;
import net.minecraft.network.chat.Component;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class SegmentsSlider extends AbstractSliderButton {

    private final int minValue;
    private final int maxValue;

    public SegmentsSlider(int x, int y, int width, int height, int minValue, int maxValue, int currentValue) {
        super(x, y, width, height, Component.empty(), (double) (currentValue - minValue) / (maxValue - minValue));
        this.minValue = minValue;
        this.maxValue = maxValue;
        this.updateMessage();
    }

    @Override
    protected void updateMessage() {
        this.setMessage(Component.literal("Segments: " + this.getValue()));
    }

    @Override
    protected void applyValue() {
        int value = this.getValue();
        ClientConfig.SEGMENTS.set(value);
    }

    public int getValue() {
        int rawValue = (int) (this.value * (maxValue - minValue)) + minValue;
        return (rawValue / 4) * 4;
    }
}
