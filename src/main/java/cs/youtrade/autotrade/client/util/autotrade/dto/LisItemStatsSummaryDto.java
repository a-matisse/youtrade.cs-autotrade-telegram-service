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
    private final double minPriceFactor;
    private final double maxPriceFactor;
    private final long popularity;
    private final int updateCount;
}
