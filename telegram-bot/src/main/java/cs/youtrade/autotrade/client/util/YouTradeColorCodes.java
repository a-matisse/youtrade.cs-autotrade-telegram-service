package cs.youtrade.autotrade.client.util;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public enum YouTradeColorCodes {
    MAIN(
            new String[]{"#F1F1EE"},
            new String[]{"#72716D"}
    ),
    CONTROL(
            new String[]{"#FBEFF0"},
            new String[]{"#8F3037"}
    ),
    SINGLE(
            new String[]{"#FFFFFF"},
            new String[]{"#202124"}
    ),
    GROUP(
            new String[]{"#FFFFFF"},
            new String[]{"#202124"}
    ),
    RANDOM(
            new String[]{"#F8F7F3", "#F5F4EF"},
            new String[]{"#5F6560", "#5F6560"}
    );
    private final String[] bgColor;
    private final String[] textColor;

    public String getBgColor(int index) {
        return bgColor[Math.floorMod(index, length())];
    }

    public String getTextColor(int index) {
        return textColor[Math.floorMod(index, length())];
    }

    public int length() {
        return bgColor.length;
    }
}
