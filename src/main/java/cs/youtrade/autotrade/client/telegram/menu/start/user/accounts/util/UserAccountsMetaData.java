package cs.youtrade.autotrade.client.telegram.menu.start.user.accounts.util;

import cs.youtrade.autotrade.client.util.autotrade.dto.user.accounts.FcdAccountsV2Dto;
import cs.youtrade.autotrade.client.util.autotrade.dto.user.params.FcdParamsGetDto;
import lombok.Getter;
import lombok.Setter;

public class UserAccountsMetaData {
    private static final int SIZE = 50;

    @Getter
    private final long chatId;
    @Getter
    private final FcdParamsGetDto ydp;
    @Getter
    private UserAccountsMode mode = UserAccountsMode.GENERAL;
    @Getter
    private int size = SIZE;
    private boolean hasNext = true;
    private boolean hasPrevious = false;
    @Getter
    @Setter
    private int page = 0;

    public UserAccountsMetaData(
            long chatId,
            FcdParamsGetDto ydp
    ) {
        this.chatId = chatId;
        this.ydp = ydp;
    }

    public synchronized void incrementPage() {
        page++;
    }

    public synchronized void decrementPage() {
        page--;
    }

    public synchronized FcdAccountsV2Dto setPageMetadata(FcdAccountsV2Dto dto) {
        this.size = dto.getAccounts().getSize();
        this.hasNext = dto.getAccounts().hasNext();
        this.hasPrevious = dto.getAccounts().hasPrevious();
        return dto;
    }

    public synchronized void switchMode() {
        this.mode = switch (mode) {
            case GENERAL -> UserAccountsMode.BUYER;
            case BUYER -> UserAccountsMode.SELLER;
            case SELLER -> UserAccountsMode.WORKER;
            case WORKER -> UserAccountsMode.GENERAL;
        };
    }

    public boolean hasNext() {
        return hasNext;
    }

    public boolean hasPrevious() {
        return hasPrevious;
    }
}
