package cs.youtrade.autotrade.client.util.autotrade.dto;

import lombok.Data;

public record LisItemStatsSummaryDto(
        String itemName,
        double minPrice,
        double maxPrice,
        double minIntermarketPrice,
        double minPriceFactor,
        double maxPriceFactor,
        long popularity,
        int updateCount,
        FcdNewestScoringData singleData,
        FcdNewestScoringData groupData,
        FcdNewestScoringData[] meanData
) {
}
