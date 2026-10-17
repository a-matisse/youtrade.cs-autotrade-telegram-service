package cs.youtrade.autotrade.client.telegram.menu.start.user.accounts;

import lombok.Getter;
import lombok.Setter;

import cs.youtrade.autotrade.client.util.autotrade.util.accounts.FcdAccountV2Dto;

@Getter
@Setter
public class PurchaseFreezeData {
    private Long tokenId;
    private FcdAccountV2Dto account;
    private Integer responseStatus;
    private int requestedDays;
}
