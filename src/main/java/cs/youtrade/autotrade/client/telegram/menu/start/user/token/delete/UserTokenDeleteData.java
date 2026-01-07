package cs.youtrade.autotrade.client.telegram.menu.start.user.token.delete;

import lombok.Data;

@Data
public class UserTokenDeleteData {
    private TokenDeleteOption opt;
    private Long tokenId;
}
