package cs.youtrade.autotrade.client.util.autotrade;

import lombok.Getter;

@Getter
public enum DuplicateMode {
    NUMERIC("Штучный"),
    PERCENTAGE("Процентный"),
    PERCENTAGE_PORTFOLIO("Процентный (от банка)");

    private final String russianName;

    DuplicateMode(String russianName) {
        this.russianName = russianName;
    }
}
