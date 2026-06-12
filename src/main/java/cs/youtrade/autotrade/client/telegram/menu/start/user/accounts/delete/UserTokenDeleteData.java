package cs.youtrade.autotrade.client.telegram.menu.start.user.accounts.delete;

import lombok.Data;

@Data
public class UserTokenDeleteData {
    private TokenDeleteOption opt;
    private Long tokenId;
}
