package cs.youtrade.autotrade.client.util.autotrade.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class BuyTokenAddDto {
    private Long chatId;
    private String token;
    private String partnerId;
    private String steamToken;
}
