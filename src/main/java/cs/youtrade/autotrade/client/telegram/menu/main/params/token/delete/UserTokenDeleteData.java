package cs.youtrade.autotrade.client.telegram.menu.main.params.token.delete;

import lombok.Data;

@Data
public class UserTokenDeleteData {
    private TokenDeleteOption opt;
    private Long tokenId;
}
