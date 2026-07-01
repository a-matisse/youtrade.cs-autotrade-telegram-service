package cs.youtrade.autotrade.client.util.autotrade.util.accounts;

import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class AccountData {
    Double available;
    Double frozen;
}
