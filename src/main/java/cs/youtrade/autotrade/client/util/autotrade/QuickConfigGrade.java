package cs.youtrade.autotrade.client.util.autotrade;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum QuickConfigGrade {
    NONE("Отсутствует"),
    MINIMAL("Минимальный"),
    MODERATE("Умеренный"),
    STRICT("Строгий"),
    ABSOLUTE("Тотальный");

    private final String russianName;

    public int getScore() {
        return this.ordinal() * 20;
    }
}
