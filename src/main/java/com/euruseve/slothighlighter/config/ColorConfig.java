package com.euruseve.slothighlighter.config;

public class ColorConfig
{
    public static final int DEFAULT_COLOR = 0xFFA5D977;
    public static final int BORDER_COLOR = 0xFAFDFFFD;

    private static int highlightingColor = DEFAULT_COLOR;

    public static void setHighlightingColor(int color) {
        highlightingColor = color;
    }

    public static int getHighlightingColor() {
        return highlightingColor;
    }
}
