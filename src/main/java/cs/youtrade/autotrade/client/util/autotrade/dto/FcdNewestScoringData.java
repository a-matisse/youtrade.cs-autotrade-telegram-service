package cs.youtrade.autotrade.client.util.autotrade.dto;

import lombok.Data;

@Data
public class FcdNewestScoringData {
    private final Double meanPrice;
    private final Double minPercent;
    private final Double maxPercent;
    private final Double trendScore;
}
