package cs.youtrade.autotrade.client.telegram.menu.main.params.token.add;

import lombok.Data;

@Data
public class UserTokenAddData {
    private TokenChooseOption opt;
    private String api;
    private String partnerId;
    private String steamToken;
}
