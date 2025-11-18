package cs.youtrade.autotrade.client.util.autotrade.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class SellTokenAddDto {
    private Long chatId;
    private String token;
}
