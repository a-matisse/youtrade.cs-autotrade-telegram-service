package cs.youtrade.autotrade.client.util.autotrade.util;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.Objects;

@Data
@AllArgsConstructor
public class PriceIntervalUpdateV2Dto {
    private long youTradeId;
    private String name;
    private double marketPrice;
    private double minPrice;
    private double maxPrice;
    private double basePrice;

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        PriceIntervalUpdateV2Dto that = (PriceIntervalUpdateV2Dto) o;
        return youTradeId == that.youTradeId;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(youTradeId);
    }
}
