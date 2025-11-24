package cs.youtrade.autotrade.client.util.autotrade;

import lombok.Getter;

@Getter
public enum SellPriceEvalMode {
    DEFAULT("Стандартный"),
    INTELLIGENT_V1("Intelligent_V1");

    private final String russianName;

    SellPriceEvalMode(String russianName) {
        this.russianName = russianName;
    }
}
