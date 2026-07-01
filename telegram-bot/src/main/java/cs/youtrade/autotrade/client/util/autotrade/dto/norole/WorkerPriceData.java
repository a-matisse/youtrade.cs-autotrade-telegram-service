package cs.youtrade.autotrade.client.util.autotrade.dto.norole;

import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;

import java.math.BigDecimal;

@Getter
@Data
@NoArgsConstructor
public class WorkerPriceData {
    private int accCount = 1;
    private int periodDays = 30;
    private BigDecimal price;
}
