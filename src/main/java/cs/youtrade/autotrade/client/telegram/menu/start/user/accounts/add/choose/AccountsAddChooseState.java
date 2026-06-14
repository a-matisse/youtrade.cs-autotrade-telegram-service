package cs.youtrade.autotrade.client.telegram.menu.start.user.accounts.add.choose;

import cs.youtrade.autotrade.client.telegram.menu.UserMenu;
import cs.youtrade.autotrade.client.telegram.menu.start.user.accounts.add.parent.registry.UserApiData;
import cs.youtrade.autotrade.client.telegram.menu.start.user.accounts.add.parent.registry.UserApiRegistry;
import cs.youtrade.autotrade.client.telegram.prototype.data.UserData;
import cs.youtrade.autotrade.client.telegram.prototype.menu.text.base.YTPTextMenuState;
import cs.youtrade.autotrade.client.telegram.prototype.sender.text.UserTextMessageSender;
import cs.youtrade.autotrade.client.util.autotrade.endpoint.user.params.ParamsEndpoint;
import cs.youtrade.autotrade.client.util.emoji.DynamicEmoji;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.generics.TelegramClient;

import static cs.youtrade.autotrade.client.util.autotrade.dto.user.params.FcdParamsGetDto.decideLink;

@Service
public class AccountsAddChooseState extends YTPTextMenuState<AccountsAddChooseOption> {
    private final UserApiRegistry registry;
    private final ParamsEndpoint paramsEndpoint;

    public AccountsAddChooseState(
            UserTextMessageSender sender,
            UserApiRegistry registry,
            ParamsEndpoint paramsEndpoint
    ) {
        super(sender);
        this.registry = registry;
        this.paramsEndpoint = paramsEndpoint;
    }

    @Override
    public UserMenu supportedState() {
        return UserMenu.ACCOUNTS_ADD_STAGE_CHOOSE;
    }

    @Override
    public AccountsAddChooseOption getOption(String optionStr) {
        return AccountsAddChooseOption.valueOf(optionStr);
    }

    @Override
    public AccountsAddChooseOption[] getOptions(UserData userData) {
        return AccountsAddChooseOption.values();
    }

    @Override
    public UserMenu executeCallback(TelegramClient bot, Update update, UserData user, AccountsAddChooseOption t) {
        return switch (t) {
            case BUYER_ACCOUNT -> UserMenu.ACCOUNTS_ADD_STAGE_1_BUYER;
            case SELLER_ACCOUNT -> UserMenu.ACCOUNTS_ADD_STAGE_1_SELLER;
            case WORKER_ACCOUNT -> UserMenu.ACCOUNTS_ADD_STAGE_1_WORKER;
            case RETURN -> UserMenu.ACCOUNTS;
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

        var data = registry.getOrCreate(userData, UserApiData::new);
        data.setDirection(fcd.getData());
        return String.format("""
                        %s <b>Выберите, что хотите добавить</b>
                        
                        <blockquote>• Покупка: <b><a href="%s">%s</a></b>
                        • Продажа: <b><a href="%s">%s</a></b>
                        • Воркер: <b><a href="%s">%s</a></b>
                        
                        <i><b>Добавляя аккаунты</b>, вы <b>автоматизируете свою торговлю</b>. Добавьте всё — и вам останется <b>только выводить деньги</b> %s</i></blockquote>
                        """,
                DynamicEmoji.CHOOSE.getEmoji(),
                decideLink(data.getSource()), data.getSource().getMarketName(),
                decideLink(data.getDestination()), data.getDestination().getMarketName(),
                "https://youtu.be/29jLB9GmKE4?si=N1po7FVLQUohi66J", "maFile",
                DynamicEmoji.BLINK_SMILE.getEmoji()
        );
    }
}
