package com.euruseve.slothighlighter.config;

public class ItemScaleConfig {

    private static boolean useModScaling = false;

    public static boolean isModScalingEnabled() {
        return useModScaling;
    }

    public static void setModScaling(boolean scaling) {
        useModScaling = scaling;
    }
}
