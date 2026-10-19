package cs.youtrade.autotrade.client.telegram.menu.start.user.portfolio;

import cs.youtrade.autotrade.client.telegram.localization.LocalizedTexts;
import cs.youtrade.autotrade.client.telegram.localization.BotTexts;
import cs.youtrade.autotrade.client.telegram.menu.UserMenu;
import cs.youtrade.autotrade.client.telegram.prototype.ErrorMessageGenerator;
import cs.youtrade.autotrade.client.telegram.prototype.data.UserData;
import cs.youtrade.autotrade.client.telegram.prototype.menu.text.base.YTPTextMenuState;
import cs.youtrade.autotrade.client.telegram.prototype.sender.text.UserTextMessageSender;
import cs.youtrade.autotrade.client.util.autotrade.dto.user.params.FcdParamsGetDto;
import cs.youtrade.autotrade.client.util.autotrade.endpoint.user.params.ParamsEndpoint;
import cs.youtrade.autotrade.client.util.emoji.DynamicEmoji;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.generics.TelegramClient;

import java.math.BigDecimal;

import static cs.youtrade.autotrade.client.util.autotrade.dto.user.params.FcdParamsGetDto.getMarketWithLink;

@Service
@Log4j2
public class UserTableState extends YTPTextMenuState<UserTableMenu> {
    private final ParamsEndpoint paramsEndpoint;
    private final PortfolioTableLoader tableLoader;
    private final BotTexts texts;

    public UserTableState(
            UserTextMessageSender sender,
            ParamsEndpoint paramsEndpoint,
            PortfolioTableLoader tableLoader,
            BotTexts texts
    ) {
        super(sender, texts);
        this.paramsEndpoint = paramsEndpoint;
        this.tableLoader = tableLoader;
        this.texts = texts;
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
        var translations = botTexts.forUser(userData, "portfolio.UserTable");
        try {
            return switch (t) {
                case TABLE_INVENTORY -> tableLoader.inventory(userData);
                case TABLE_SELLING -> tableLoader.selling(userData);
                case TABLE_WAITING -> tableLoader.waiting(userData);
                case TABLE_HISTORY -> UserMenu.PORTFOLIO_HISTORY_STAGE_CHOOSE;
                case RETURN -> UserMenu.USER;
                case RESTORE -> UserMenu.PORTFOLIO_V2_RESTORE_STAGE_1;
            };
        } catch (RuntimeException error) {
            log.warn("Couldn't load portfolio table {} for chat {}", t, userData.getChatId(), error);
            sender.sendTextMes(bot, userData, ErrorMessageGenerator.getErrorText(translations.inBundle("common.Interface")));
            return supportedState();
        }
    }

    @Override
    public String getHeaderText(TelegramClient bot, UserData user) {
        var restAns = paramsEndpoint.getCurrent(user.getChatId());
        if (restAns.getStatus() >= 300)
            return null;

        var fcd = restAns.getResponse();
        if (!fcd.isResult())
            return fcd.getCause();

        return getPortfolioInfo(user, fcd.getData());
    }

    private String getPortfolioInfo(UserData user, FcdParamsGetDto fcd) {
        var translations = botTexts.forUser(user, "portfolio.UserTable");
        return String.format("""
                        %s <i>%s</i>
                        
                        %s
                        
                        %s <b>%s</b>
                        <blockquote>%s: <b>$%.2f</b>
                        ┣ %s: %s
                        ┣ %s: %s
                        ┗ %s: <b>$%.2f</b></blockquote>
                        
                        %s ━━ %s
                        """,
                        DynamicEmoji.YOUTRADE.getEmoji(),
                        translations.get("your-portfolio"),
                        fcd.getProfileStr(user, texts.forUser(user, "summary.ParamsSummary")),
                        DynamicEmoji.BANK.getEmoji(),
                        translations.get("capital"),
                        translations.get("total"),
                        fcd.getManagedFunds(),
                        getMarketWithLink(fcd.getSource()),
                        buyBalanceStr(fcd, translations),
                        getMarketWithLink(fcd.getDestination()),
                        sellBalanceStr(fcd, translations),
                        translations.get("inventory"),
                        fcd.getItemBalance(),
                        getStatusStr(fcd.getBuyWorks(), translations.get("buying")),
                        getStatusStr(fcd.getSellWorks(), translations.get("selling")));
    }

    private String getStatusStr(boolean status, String name) {
        return status
                ? String.format("%s <b>%s</b>",
                DynamicEmoji.ON.getEmoji(), name)
                : String.format("%s <b>%s</b>",
                DynamicEmoji.OFF.getEmoji(), name);
    }

    private String buyBalanceStr(FcdParamsGetDto fcd, LocalizedTexts translations) {
        String ans = String.format("<b>$%.2f</b>", fcd.getBuyBalance());
        if (fcd.getBuyBalanceFrozen().compareTo(BigDecimal.ZERO) > 0)
            ans += String.format(" (<b>$%.2f</b> %s",
                    fcd.getBuyBalanceFrozen(),
                    translations.get("on-hold"));
        return ans;
    }

    private String sellBalanceStr(FcdParamsGetDto fcd, LocalizedTexts translations) {
        String ans = String.format("<b>$%.2f</b>", fcd.getSellBalance());
        if (fcd.getSellBalanceFrozen().compareTo(BigDecimal.ZERO) > 0)
            ans += String.format(" (<b>$%.2f</b> %s",
                    fcd.getSellBalanceFrozen(),
                    translations.get("on-hold"));
        return ans;
    }
}
