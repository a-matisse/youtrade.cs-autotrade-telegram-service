package cs.youtrade.autotrade.client.util.autotrade;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;
import java.util.stream.Collectors;

@Getter
@AllArgsConstructor
public enum ItemScoringType implements FcdDistance {
    SINGLE(
            "Одиночный",
            "cur",
            "Текущие значения позиции"
    ),
    GROUP(
            "Групповой",
            "gr",
            "Текущие минимальные значения группы"
    ),
    MEAN(
            "Усредненная",
            "avg",
            "Усредненные значения позиции за период (SMA)"
    );

    private final String russianName;
    private final String shortName;
    private final String desc;

    public String getShortName(int days) {
        if (this.equals(MEAN))
            return String.format("%s(%sd). ", shortName, days);
        else
            return String.format("%s. ", shortName);
    }

    public static String generateDescription() {
        return Arrays
                .stream(values())
                .map(val -> String.format(
                        "<code>%s</code> - %s", val.name(), val.getDesc()))
                .collect(Collectors.joining("\n"));
    }
}
