package cs.youtrade.autotrade.client.util.autotrade.util.accounts;

import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class FcdAccountV2Dto {
    Long id;
    String steamToken;
    String givenName;
    AccountData buyBalance;
    AccountData sellBalance;
    WorkerData workerStatus;
}
