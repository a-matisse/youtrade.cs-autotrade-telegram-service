package cs.youtrade.autotrade.client.util.autotrade.dto;

public record ItemStatsSummaryDto(
        String itemName,
        double minPrice,
        double maxPrice,
        double minIntermarketPrice,
        double minPriceFactor,
        double maxPriceFactor,
        long popularity,
        int pricePeriod,
        double pricePhase,
        int updateCount,
        FcdNewestScoringData singleData,
        FcdNewestScoringData groupData,
        FcdNewestScoringData[] meanData
) {
}
