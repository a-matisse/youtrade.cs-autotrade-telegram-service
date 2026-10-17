package cs.youtrade.autotrade.client.telegram.menu.start.user.accounts;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AccountInfoData {
    private Long tokenId;
    private boolean passwordVerified;
    public enum Result { SENT, RATE_LIMITED }

    private volatile Result result;
}
