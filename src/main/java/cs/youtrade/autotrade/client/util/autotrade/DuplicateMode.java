package cs.youtrade.autotrade.client.util.autotrade;

import lombok.Getter;

@Getter
public enum DuplicateMode {
    NUMERIC("Количественный"),
    PERCENTAGE("Процентный");

    private final String russianName;

    DuplicateMode(String russianName) {
        this.russianName = russianName;
    }
}
