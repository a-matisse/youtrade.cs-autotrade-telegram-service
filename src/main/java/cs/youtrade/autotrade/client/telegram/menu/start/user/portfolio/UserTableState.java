package cs.youtrade.autotrade.client.telegram.menu.start.user.portfolio;

import cs.youtrade.autotrade.client.telegram.menu.UserMenu;
import cs.youtrade.autotrade.client.telegram.prototype.data.UserData;
import cs.youtrade.autotrade.client.telegram.prototype.menu.text.base.YTPTextMenuState;
import cs.youtrade.autotrade.client.telegram.prototype.sender.text.UserTextMessageSender;
import cs.youtrade.autotrade.client.util.autotrade.dto.user.params.FcdParamsGetDto;
import cs.youtrade.autotrade.client.util.autotrade.endpoint.user.params.ParamsEndpoint;
import cs.youtrade.autotrade.client.util.emoji.DynamicEmoji;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.generics.TelegramClient;

import java.math.BigDecimal;

import static cs.youtrade.autotrade.client.util.autotrade.dto.user.params.FcdParamsGetDto.getMarketWithLink;

@Service
public class UserTableState extends YTPTextMenuState<UserTableMenu> {
    private final ParamsEndpoint paramsEndpoint;

    public UserTableState(
            UserTextMessageSender sender,
            ParamsEndpoint paramsEndpoint
    ) {
        super(sender);
        this.paramsEndpoint = paramsEndpoint;
    }

    @Override
    public UserMenu supportedState() {
        return UserMenu.PORTFOLIO;
    }

    @Override
    public UserTableMenu getOption(String optionStr) {
        return UserTableMenu.valueOf(optionStr);
    }

    @Override
    public UserTableMenu[] getOptions(UserData userData) {
        return UserTableMenu.values();
    }

    @Override
    public UserMenu executeCallback(TelegramClient bot, Update update, UserData userData, UserTableMenu t) {
        return switch (t) {
            case TABLE_INVENTORY -> UserMenu.PORTFOLIO_V2_INVENTORY_STAGE_1;
            case TABLE_SELLING -> UserMenu.PORTFOLIO_V2_SELLING_STAGE_1;
            case TABLE_WAITING -> UserMenu.PORTFOLIO_WAITING;
            case TABLE_HISTORY -> UserMenu.PORTFOLIO_HISTORY_STAGE_CHOOSE;
            case RETURN -> UserMenu.USER;
            case RESTORE -> UserMenu.PORTFOLIO_V2_RESTORE_STAGE_1;
        };
    }

    @Override
    public String getHeaderText(TelegramClient bot, UserData userData) {
        var restAns = paramsEndpoint.getCurrent(userData.getChatId());
        if (restAns.getStatus() >= 300)
            return null;

        var fcd = restAns.getResponse();
        if (!fcd.isResult())
            return fcd.getCause();

        return getPortfolioInfo(fcd.getData());
    }

    private String getPortfolioInfo(FcdParamsGetDto fcd) {
        return String.format("""                        
                        %s <i>Портфель пользователя</i>
                        
                        %s
                        
                        %s <b>Капитал</b>
                        <blockquote>Всего: <b>$%.2f</b>
                        ┣ %s: %s
                        ┣ %s: %s
                        ┗ Инвентарь: <b>$%.2f</b></blockquote>
                        
                        %s ━━ %s
                        """,
                DynamicEmoji.YOUTRADE.getEmoji(),
                fcd.getProfileStr(),

                DynamicEmoji.BANK.getEmoji(),
                fcd.getManagedFunds(),
                getMarketWithLink(fcd.getSource()), buyBalanceStr(fcd),
                getMarketWithLink(fcd.getDestination()), sellBalanceStr(fcd),
                fcd.getItemBalance(),

                getStatusStr(fcd.getBuyWorks(), "Покупка"),
                getStatusStr(fcd.getSellWorks(), "Продажа")
        );
    }

    private String getStatusStr(boolean status, String name) {
        return status
                ? String.format("%s <b>%s</b>",
                DynamicEmoji.ON.getEmoji(), name)
                : String.format("%s <b>%s</b>",
                DynamicEmoji.OFF.getEmoji(), name);
    }

    private String buyBalanceStr(FcdParamsGetDto fcd) {
        String ans = String.format("<b>$%.2f</b>", fcd.getBuyBalance());
        if (fcd.getBuyBalanceFrozen().compareTo(BigDecimal.ZERO) > 0)
            ans += String.format(" (<b>$%.2f</b> в холде)", fcd.getBuyBalanceFrozen());
        return ans;
    }

    private String sellBalanceStr(FcdParamsGetDto fcd) {
        String ans = String.format("<b>$%.2f</b>", fcd.getSellBalance());
        if (fcd.getSellBalanceFrozen().compareTo(BigDecimal.ZERO) > 0)
            ans += String.format(" (<b>$%.2f</b> в холде)", fcd.getSellBalanceFrozen());
        return ans;
    }
}
