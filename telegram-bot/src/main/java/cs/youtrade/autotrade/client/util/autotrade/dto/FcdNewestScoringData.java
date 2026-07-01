package cs.youtrade.autotrade.client.util.autotrade.dto;

public record FcdNewestScoringData(
        float meanPrice,
        float minPercent,
        float maxPercent,
        float trendScore
) {
}
