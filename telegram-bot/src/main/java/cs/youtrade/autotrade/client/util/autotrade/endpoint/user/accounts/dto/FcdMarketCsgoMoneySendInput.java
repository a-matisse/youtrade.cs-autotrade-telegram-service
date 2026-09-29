package cs.youtrade.autotrade.client.util.autotrade.endpoint.user.accounts.dto;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class FcdMarketCsgoMoneySendInput {
    private Long sourceTokenId;
    private String destinationApiKey;
    private BigDecimal amount;
    private String currency;
    private String paymentPassword;
}
