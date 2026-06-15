package cs.youtrade.autotrade.client.telegram.menu.start.user.accounts.add.choose;

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

import java.math.BigDecimal;
import java.math.RoundingMode;

import static cs.youtrade.autotrade.client.util.autotrade.dto.user.params.FcdParamsGetDto.decideLink;

@Service
public class AccountsAddChooseState extends YTPTextMenuState<AccountsChooseOption> {
    private final UserApiRegistry registry;
    private final ParamsEndpoint paramsEndpoint;
    private final SubGetEndpoint subGetEndpoint;

    public AccountsAddChooseState(
            UserTextMessageSender sender,
            UserApiRegistry registry,
            ParamsEndpoint paramsEndpoint,
            SubGetEndpoint subGetEndpoint
    ) {
        super(sender);
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
        BigDecimal buyerFee = fee.getBuySubPrices().get(source).setScale(1, RoundingMode.HALF_UP);
        BigDecimal sellerFee = fee.getSellSubPrices().get(destination).setScale(1, RoundingMode.HALF_UP);
        BigDecimal workerFee = fee.getWorkerPriceData().getPrice().setScale(1, RoundingMode.HALF_UP);
        return String.format("""
                        %s <b>Выберите, что хотите добавить</b>
                        
                        <blockquote>%s Покупка — <b><a href="%s">%s</a></b> (<i>~%s%% с покупки</i>)
                        %s Продажа — <b><a href="%s">%s</a></b> (<i>~%s%% с продажи</i>)
                        %s Воркер — <b><a href="%s">%s</a></b> (<i>$%s/мес за аккаунт</i>)
                        
                        <i><b>Добавляя аккаунты</b>, вы <b>автоматизируете свою торговлю</b>. Добавьте всё — и вам останется <b>только выводить деньги</b> %s</i></blockquote>
                        """,
                DynamicEmoji.CHOOSE.getEmoji(),
                AccountsChooseOption.BUYER_ACCOUNT.getDynamicEmoji(), decideLink(source), source.getMarketName(), buyerFee.toPlainString(),
                AccountsChooseOption.SELLER_ACCOUNT.getDynamicEmoji(), decideLink(destination), destination.getMarketName(), sellerFee.toPlainString(),
                AccountsChooseOption.WORKER_ACCOUNT.getDynamicEmoji(), "https://youtu.be/29jLB9GmKE4?si=N1po7FVLQUohi66J", "maFile", workerFee.toPlainString(),
                DynamicEmoji.BLINK_SMILE.getEmoji()
        );
    }
}
