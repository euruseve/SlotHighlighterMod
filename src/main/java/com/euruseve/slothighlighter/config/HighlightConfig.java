package com.euruseve.slothighlighter.config;

public class HighlightConfig
{
    private static boolean useModHighlight = true;

    public static void setModHighlight(boolean value) {
        useModHighlight = value;
    }

    public static boolean isModHighlightEnabled() {
        return useModHighlight;
    }

}
