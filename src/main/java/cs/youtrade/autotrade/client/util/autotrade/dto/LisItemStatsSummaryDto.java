package cs.youtrade.autotrade.client.util.autotrade.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.math.RoundingMode;

@Data
public class LisItemStatsSummaryDto {
    private final String itemName;
    private final double trendScore7d;
    private final double trendScore30d;
    private final double minLisPrice;
    private final double maxLisPrice;
    private final double tmSinglePrice;
    private final double tmGroupPrice;
    private final double tmMean7dPrice;
    private final double tmMean30dPrice;
    private final double minSinglePercent;
    private final double maxSinglePercent;
    private final double minGroupPercent;
    private final double maxGroupPercent;
    private final double minMean7dPercent;
    private final double maxMean7dPercent;
    private final double minMean30dPercent;
    private final double maxMean30dPercent;
    private final double minABCoefficient;
    private final double maxABCoefficient;
    private final long popularity;
    private final int updateCount;

    public LisItemStatsSummaryDto(
            String itemName,
            double trendScore7d,
            double trendScore30d,
            double minLisPrice,
            double maxLisPrice,
            double tmSinglePrice,
            double tmGroupPrice,
            double tmMean7dPrice,
            double tmMean30dPrice,
            double minSinglePercent,
            double maxSinglePercent,
            double minGroupPercent,
            double maxGroupPercent,
            double minMean7dPercent,
            double maxMean7dPercent,
            double minMean30dPercent,
            double maxMean30dPercent,
            double minABCoefficient,
            double maxABCoefficient,
            long popularity,
            int updateCount
    ) {
        this.itemName = itemName;
        this.trendScore7d = roundToPercent(trendScore7d);
        this.trendScore30d = roundToPercent(trendScore30d);
        this.minLisPrice = round(minLisPrice);
        this.maxLisPrice = round(maxLisPrice);
        this.tmSinglePrice = round(tmSinglePrice);
        this.tmGroupPrice = round(tmGroupPrice);
        this.tmMean7dPrice = round(tmMean7dPrice);
        this.tmMean30dPrice = round(tmMean30dPrice);
        this.minSinglePercent = roundToPercent(minSinglePercent);
        this.maxSinglePercent = roundToPercent(maxSinglePercent);
        this.minGroupPercent = roundToPercent(minGroupPercent);
        this.maxGroupPercent = roundToPercent(maxGroupPercent);
        this.minMean7dPercent = roundToPercent(minMean7dPercent);
        this.maxMean7dPercent = roundToPercent(maxMean7dPercent);
        this.minMean30dPercent = roundToPercent(minMean30dPercent);
        this.maxMean30dPercent = roundToPercent(maxMean30dPercent);
        this.minABCoefficient = round(minABCoefficient);
        this.maxABCoefficient = round(maxABCoefficient);
        this.popularity = popularity;
        this.updateCount = updateCount;
    }

    private double roundToPercent(double value) {
        return round(value * 100);
    }

    private double round(double value) {
        if (Double.isNaN(value) || Double.isInfinite(value))
            return 0.0; // or some other default value

        return BigDecimal.valueOf(value)
                .setScale(2, RoundingMode.HALF_UP)
                .doubleValue();
    }
}
