package cs.youtrade.autotrade.client.util.autotrade;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
public enum ItemScoringType implements FcdDistance {
    SINGLE("Одиночный", "cur"),
    GROUP("Групповой", "gr"),
    MEAN("Усредненная", "avg");

    @Getter
    private final String russianName;
    private final String shortName;

    public String getShortName(int days) {
        if (this.equals(MEAN))
            return String.format("%s(%sd). ", shortName, days);
        else
            return String.format("%s. ", shortName);
    }
}
