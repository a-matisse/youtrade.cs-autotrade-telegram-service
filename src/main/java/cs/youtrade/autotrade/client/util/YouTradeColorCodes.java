package cs.youtrade.autotrade.client.util;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public enum YouTradeColorCodes {
    MAIN(
            new String[]{"#9BC2E6"},
            new String[]{"#1F497D"}
    ),
    CONTROL(
            new String[]{"#FFFF00"},
            new String[]{"#000000"}
    ),
    SINGLE(
            new String[]{"#C6EFCE"},
            new String[]{"#006100"}
    ),
    GROUP(
            new String[]{"#FFEB9C"},
            new String[]{"#BF8F00"}
    ),
    RANDOM(
            new String[]{"#F2F2F2", "#E6F2FF", "#F0E6FF", "#FFF0F0", "#F0FFF0"},
            new String[]{"#666666", "#0D47A1", "#4A148C", "#880E4F", "#1B5E20"}
    );
    private final String[] bgColor;
    private final String[] textColor;

    public String getBgColor(int index) {
        index = index > 0 ? index : 1;
        return bgColor[bgColor.length % index];
    }

    public String getTextColor(int index) {
        index = index > 0 ? index : 1;
        return textColor[textColor.length % index];
    }

    public int length() {
        return bgColor.length;
    }
}
