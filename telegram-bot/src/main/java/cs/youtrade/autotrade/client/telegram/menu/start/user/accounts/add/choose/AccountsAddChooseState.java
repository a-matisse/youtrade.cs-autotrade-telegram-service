package cs.youtrade.autotrade.client.telegram.menu.start.user.accounts.add.choose;

import cs.youtrade.autotrade.client.telegram.localization.BotTexts;
import cs.youtrade.autotrade.client.telegram.menu.UserMenu;
import cs.youtrade.autotrade.client.telegram.menu.start.user.accounts.add.parent.registry.UserApiData;
import cs.youtrade.autotrade.client.telegram.menu.start.user.accounts.add.parent.registry.UserApiRegistry;
import cs.youtrade.autotrade.client.telegram.menu.start.user.accounts.util.AccountsChooseOption;
import cs.youtrade.autotrade.client.telegram.prototype.data.UserData;
import cs.youtrade.autotrade.client.telegram.prototype.menu.text.base.YTPTextMenuState;
import cs.youtrade.autotrade.client.telegram.prototype.sender.text.UserTextMessageSender;
import cs.youtrade.autotrade.client.util.autotrade.MarketType;
import cs.youtrade.autotrade.client.util.autotrade.endpoint.norole.SubGetEndpoint;
import cs.youtrade.autotrade.client.util.autotrade.endpoint.user.params.ParamsEndpoint;
import cs.youtrade.autotrade.client.util.emoji.DynamicEmoji;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.generics.TelegramClient;

import java.util.Map;
import java.math.BigDecimal;
import java.math.RoundingMode;

import static cs.youtrade.autotrade.client.util.autotrade.dto.user.params.FcdParamsGetDto.decideLink;

@Service
public class AccountsAddChooseState extends YTPTextMenuState<AccountsChooseOption> {
    private static final BigDecimal TRANSACTION_FEE = BigDecimal.valueOf(10);
    private final UserApiRegistry registry;
    private final ParamsEndpoint paramsEndpoint;
    private final SubGetEndpoint subGetEndpoint;

    public AccountsAddChooseState(
            UserTextMessageSender sender,
            UserApiRegistry registry,
            ParamsEndpoint paramsEndpoint,
            SubGetEndpoint subGetEndpoint,
            BotTexts botTexts
    ) {
        super(sender, botTexts);
        this.registry = registry;
        this.paramsEndpoint = paramsEndpoint;
        this.subGetEndpoint = subGetEndpoint;
    }

    @Override
    public UserMenu supportedState() {
        return UserMenu.ACCOUNTS_ADD_STAGE_CHOOSE;
    }

    @Override
    public AccountsChooseOption getOption(String optionStr) {
        return AccountsChooseOption.valueOf(optionStr);
    }

    @Override
    public AccountsChooseOption[] getOptions(UserData userData) {
        return AccountsChooseOption.values();
    }

    @Override
    public UserMenu executeCallback(TelegramClient bot, Update update, UserData user, AccountsChooseOption t) {
        return switch (t) {
            case BUYER_ACCOUNT -> UserMenu.ACCOUNTS_ADD_STAGE_1_BUYER;
            case SELLER_ACCOUNT -> UserMenu.ACCOUNTS_ADD_STAGE_1_SELLER;
            case WORKER_ACCOUNT -> UserMenu.ACCOUNTS_ADD_STAGE_1_WORKER;
            case RETURN -> UserMenu.ACCOUNTS;
        };
    }

    @Override
    public String getHeaderText(TelegramClient bot, UserData userData) {
        var translations = botTexts.forUser(userData, "accounts.add.AccountsAddChoose");
        // 1. Получаем данные о направлениях торговли
        var restAns = paramsEndpoint.getCurrent(userData.getChatId());
        if (restAns.getStatus() >= 300)
            return null;
        var fcd = restAns.getResponse();
        if (!fcd.isResult())
            return fcd.getCause();
        var data = registry.getOrCreate(userData, UserApiData::new);
        data.setDirection(fcd.getData());
        MarketType source = data.getSource();
        MarketType destination = data.getDestination();
        // 1. Получаем данные о комиссиях торговли
        var feeAns = subGetEndpoint.getPrices(userData.getChatId());
        if (feeAns.getStatus() >= 300)
            return null;
        var fee = feeAns.getResponse();
        if (!fee.isResult())
            return fee.getCause();
        // Estimate buy fee
        BigDecimal buyerFee = fee
                .getBuySubPrices()
                .get(source)
                .divide(TRANSACTION_FEE, RoundingMode.HALF_UP)
                .setScale(1, RoundingMode.HALF_UP);
        // Estimate sell fee
        BigDecimal sellerFee = fee
                .getSellSubPrices()
                .get(destination)
                .divide(TRANSACTION_FEE, RoundingMode.HALF_UP)
                .setScale(1, RoundingMode.HALF_UP);
        // Estimate worker fee
        BigDecimal workerFee = fee.getWorkerPriceData().getPrice().setScale(1, RoundingMode.HALF_UP);
        return String.format("""
                        %s <b>%s</b>
                        
                        <blockquote>%s %s
                        %s %s
                        %s %s
                        
                        <i>%s</i></blockquote>
                        """,
                        DynamicEmoji.CHOOSE.getEmoji(),
                        translations.get("choose-what-to-add"),
                        AccountsChooseOption.BUYER_ACCOUNT.getDynamicEmoji(),
                        translations.format("buying", Map.of("value1", String.format("<b><a href=\"%s\">%s</a></b>", decideLink(source), source.getMarketName()), "value2", String.format("<i>~%s%% %s</i>", buyerFee.toPlainString(), translations.get("per-purchase")))),
                        AccountsChooseOption.SELLER_ACCOUNT.getDynamicEmoji(),
                        translations.format("selling", Map.of("value1", String.format("<b><a href=\"%s\">%s</a></b>", decideLink(destination), destination.getMarketName()), "value2", String.format("<i>~%s%% %s</i>", sellerFee.toPlainString(), translations.get("per-sale")))),
                        AccountsChooseOption.WORKER_ACCOUNT.getDynamicEmoji(),
                        translations.format("y-cs-worker", Map.of("value1", String.format("<b><a href=\"%s\">%s</a></b>", "https://youtu.be/29jLB9GmKE4?si=N1po7FVLQUohi66J", "maFile"), "value2", String.format("<i>$%s/%s</i>", workerFee.toPlainString(), translations.get("month-per-account")))),
                        translations.format("by-adding-accounts-b-you-connect-all", Map.of("value1", String.format("<b>%s</b>", translations.get("automate-your-trading")), "value2", String.format("<b>%s</b>", translations.get("withdraw-your-funds")), "argument14", String.valueOf(DynamicEmoji.BLINK_SMILE.getEmoji()), "emphasis", String.format("<b>%s</b>", translations.get("by-adding-accounts-b-you-connect-all.emphasis")))));
    }
}
