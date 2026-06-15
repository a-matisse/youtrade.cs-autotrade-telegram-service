package cs.youtrade.autotrade.client.telegram.menu.start.user.accounts.delete;

import cs.youtrade.autotrade.client.telegram.menu.start.user.accounts.util.AccountsChooseOption;
import lombok.Data;

import java.util.List;

@Data
public class UserTokenDeleteData {
    private AccountsChooseOption opt;
    private List<Long> tokenIds;
}
