package cs.youtrade.autotrade.client.util.autotrade.dto;

import cs.youtrade.autotrade.client.util.autotrade.MarketType;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ParamsAddDto {
    private Long chatId;
    private MarketType source;
    private String token;
}
