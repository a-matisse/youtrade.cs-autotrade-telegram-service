package cs.youtrade.autotrade.client.util.autotrade.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Map;

@Data
public class LisItemStatsSummaryDto {
    private final String itemName;
    private final double minPrice;
    private final double maxPrice;
    private final double minIntermarketPrice;
    private final double minPriceFactor;
    private final double maxPriceFactor;
    private final long popularity;
    private final int updateCount;
    private final FcdNewestScoringData singleData;
    private final FcdNewestScoringData groupData;
    private final Map<Integer, FcdNewestScoringData> meanData;
}
