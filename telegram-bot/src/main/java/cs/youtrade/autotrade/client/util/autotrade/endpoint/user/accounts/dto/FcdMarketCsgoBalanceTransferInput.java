package cs.youtrade.autotrade.client.util.autotrade.endpoint.user.accounts.dto;

import lombok.Data;

@Data
public class FcdMarketCsgoBalanceTransferInput {
    private Long destinationTokenId;
    private String paymentPassword;
}
