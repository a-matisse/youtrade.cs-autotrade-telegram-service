package cs.youtrade.autotrade.client.util.autotrade.dto.user.accounts;

import lombok.Getter;

@Getter
public class AccountExportResponseDto {
    private long userId;
    private String userApiKey;
    private boolean complete;
    private Account account;

    @Getter
    public static class Account {
        private Long steamTokenId;
        private String steamId64;
        private String givenName;
        private Long parametersId;
        private String parametersName;
        private String source;
        private String destination;
        private String sourceApiKey;
        private String buyApiKey;
        private String sellApiKey;
        private String steamPartner;
        private String steamTradeToken;
        private Long workerTokenId;
        private String steamLogin;
        private String steamPassword;
        private String guardCode;
        private String credentialsStatus;
        private String credentialsError;
    }
}
