package cs.youtrade.autotrade.client.telegram.menu.start.user.portfolio;

import cs.youtrade.autotrade.client.telegram.menu.UserMenu;
import cs.youtrade.autotrade.client.telegram.prototype.data.UserData;
import cs.youtrade.autotrade.client.telegram.prototype.menu.text.AbstractTextMenuState;
import cs.youtrade.autotrade.client.telegram.prototype.sender.text.UserTextMessageSender;
import cs.youtrade.autotrade.client.util.autotrade.dto.user.params.FcdParamsGetDto;
import cs.youtrade.autotrade.client.util.autotrade.endpoint.user.params.ParamsEndpoint;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.generics.TelegramClient;

@Service
public class UserTableState extends AbstractTextMenuState<UserTableMenu> {
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
    public UserTableMenu[] getOptions() {
        return UserTableMenu.values();
    }

    @Override
    public UserMenu executeCallback(TelegramClient bot, Update update, UserData userData, UserTableMenu t) {
        return switch (t) {
            case TABLE_SELLING -> UserMenu.PORTFOLIO_SELLING_STAGE_1;
            case TABLE_WAITING -> UserMenu.PORTFOLIO_WAITING;
            case TABLE_HISTORY -> UserMenu.PORTFOLIO_HISTORY_STAGE_1;
            case TABLE_UPLOAD -> UserMenu.PORTFOLIO_UPLOAD_STAGE_1;
            case TABLE_CHANGE -> UserMenu.PORTFOLIO_CHANGE_STAGE_CHOOSE;
            case TABLE_RESTRICT -> UserMenu.PORTFOLIO_RESTRICT_STAGE_1;
            case RETURN -> UserMenu.USER;
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
                        💼 <b>Портфель пользователя</b>
                        ━━━━━━━━━━━━━━━━━━━━━
                        
                        👤 Ваш ID: <b>%s</b>
                        🆔 Params ID: <b>%s</b>
                        
                        %s ━━ %s
                        """,
                fcd.getTdId(),
                fcd.getTdpId(),
                getStatusStr(fcd.getBuyWorks(), "Покупка"),
                getStatusStr(fcd.getSellWorks(), "Продажа")
        );
    }

    private String getStatusStr(boolean status, String name) {
        return status
                ? String.format("🟢 <b>%s</b>", name)
                : String.format("🔴 <b>%s</b>", name);
    }
}
