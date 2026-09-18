package cs.youtrade.autotrade.client.util.autotrade.endpoint.user.accounts.dto;

import lombok.Data;

import java.util.List;

@Data
public class FcdAccountsTransferInput {
    private String destinationParameters;
    private List<Long> tokenIds;
}
