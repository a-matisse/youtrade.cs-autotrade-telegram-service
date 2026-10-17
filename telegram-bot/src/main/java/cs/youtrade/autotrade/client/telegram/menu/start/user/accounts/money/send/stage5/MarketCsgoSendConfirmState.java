package cs.youtrade.autotrade.client.telegram.menu.start.user.accounts.money.send.stage5;

import cs.youtrade.autotrade.client.telegram.menu.start.user.accounts.money.send.MarketCsgoSendData;
import cs.youtrade.autotrade.client.telegram.menu.start.user.accounts.money.send.AbstractMarketCsgoSendState;
import cs.youtrade.autotrade.client.telegram.menu.start.user.accounts.money.send.MarketCsgoSendRegistry;
import cs.youtrade.autotrade.client.telegram.menu.start.user.accounts.money.send.MarketCsgoSendTransfer;
import cs.youtrade.autotrade.client.telegram.menu.start.user.accounts.money.send.stage6.MarketCsgoSendProcessingState;

import cs.youtrade.autotrade.client.telegram.menu.UserMenu;
import cs.youtrade.autotrade.client.telegram.prototype.data.UserData;
import cs.youtrade.autotrade.client.telegram.prototype.sender.text.UserTextMessageSender;
import cs.youtrade.autotrade.client.util.emoji.DynamicEmoji;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.generics.TelegramClient;

@Service
public class MarketCsgoSendConfirmState extends AbstractMarketCsgoSendState<MarketCsgoSendConfirmMenu> {
    private final MarketCsgoSendTransfer transfer;
    private final MarketCsgoSendProcessingState processingState;

    public MarketCsgoSendConfirmState(UserTextMessageSender sender, MarketCsgoSendRegistry registry,
                                      MarketCsgoSendTransfer transfer, MarketCsgoSendProcessingState processingState) {
        super(sender, registry);
        this.transfer = transfer;
        this.processingState = processingState;
    }

    @Override
    public UserMenu supportedState() { return UserMenu.ACCOUNTS_SEND_CONFIRM; }

    @Override
    public String getHeaderText(TelegramClient bot, UserData user) {
        return String.format("""
                %s%s <b>Проверьте данные и подтвердите перевод</b>
                <blockquote>Сначала отправим <b>$%s</b> для проверки получателя. После вашего подтверждения отправим остаток выбранной суммы.</blockquote>
                """, summary(user), DynamicEmoji.QUESTION.getEmoji(), transfer.testAmount().toPlainString());
    }

    @Override
    public UserMenu executeCallback(TelegramClient bot, Update update, UserData user, MarketCsgoSendConfirmMenu option) {
        var data = registry.getOrCreate(user, MarketCsgoSendData::new);
        synchronized (data) {
            if (data.isProcessing()) return UserMenu.ACCOUNTS_SEND_PROCESSING;
            if (data.getResult() != null) return UserMenu.ACCOUNTS_SEND_RESULT;
            if (data.isTestSent()) return UserMenu.ACCOUNTS_SEND_VERIFY;
            if (option == MarketCsgoSendConfirmMenu.CANCEL) return leave(user);
            if (option != MarketCsgoSendConfirmMenu.CONFIRM) return supportedState();
            data.setProcessing(true);
            processingState.show(bot, user);
            try {
                data.setTestSent(transfer.sendTest(user, data));
                return data.isTestSent() ? UserMenu.ACCOUNTS_SEND_VERIFY : UserMenu.ACCOUNTS_SEND_RESULT;
            } finally {
                data.setProcessing(false);
            }
        }
    }

    @Override
    public MarketCsgoSendConfirmMenu getOption(String value) {
        return MarketCsgoSendConfirmMenu.valueOf(value);
    }

    @Override
    public MarketCsgoSendConfirmMenu[] getOptions(UserData user) {
        return MarketCsgoSendConfirmMenu.values();
    }
}
