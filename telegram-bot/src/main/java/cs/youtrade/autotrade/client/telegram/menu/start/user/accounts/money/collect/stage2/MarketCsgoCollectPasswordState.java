package cs.youtrade.autotrade.client.telegram.menu.start.user.accounts.money.collect.stage2;

import cs.youtrade.autotrade.client.telegram.prototype.menu.TerminalMenu;
import cs.youtrade.autotrade.client.telegram.menu.start.user.accounts.money.collect.MarketCsgoCollectData;
import cs.youtrade.autotrade.client.telegram.menu.start.user.accounts.money.collect.MarketCsgoCollectRegistry;
import cs.youtrade.autotrade.client.telegram.menu.start.user.accounts.money.collect.stage3.MarketCsgoCollectProcessingState;

import cs.youtrade.autotrade.client.telegram.menu.UserMenu;
import cs.youtrade.autotrade.client.telegram.prototype.data.UserData;
import cs.youtrade.autotrade.client.telegram.prototype.menu.text.YTPTerminalTextMenuState;
import cs.youtrade.autotrade.client.telegram.prototype.sender.text.UserTextMessageSender;
import cs.youtrade.autotrade.client.util.autotrade.endpoint.user.accounts.MarketCsgoBalanceEndpoint;
import cs.youtrade.autotrade.client.util.autotrade.endpoint.user.accounts.dto.FcdMarketCsgoBalanceTransferInput;
import cs.youtrade.autotrade.client.util.emoji.DynamicEmoji;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.generics.TelegramClient;

@Service
public class MarketCsgoCollectPasswordState extends YTPTerminalTextMenuState {
    private final MarketCsgoCollectRegistry registry;
    private final MarketCsgoBalanceEndpoint endpoint;
    private final MarketCsgoCollectProcessingState processingState;

    public MarketCsgoCollectPasswordState(UserTextMessageSender sender, MarketCsgoCollectRegistry registry,
                                          MarketCsgoBalanceEndpoint endpoint,
                                          MarketCsgoCollectProcessingState processingState) {
        super(sender);
        this.registry = registry;
        this.endpoint = endpoint;
        this.processingState = processingState;
    }

    @Override
    public UserMenu supportedState() { return UserMenu.ACCOUNTS_COLLECT_PASSWORD; }

    @Override
    public UserMenu executeCallback(TelegramClient bot, Update update, UserData user, TerminalMenu option) {
        var data = registry.getOrCreate(user, MarketCsgoCollectData::new);
        if (data.isProcessing()) return UserMenu.ACCOUNTS_COLLECT_PROCESSING;
        if (data.isCompleted()) return UserMenu.ACCOUNTS_COLLECT_RESULT;
        registry.get(user);
        return UserMenu.ACCOUNTS;
    }

    @Override
    public String getHeaderText(TelegramClient bot, UserData user) {
        return String.format("""
                %s <i>Сбор баланса Market.CSGO</i>

                %s <b>Получатель</b>
                <blockquote>• Аккаунт: <code>%s</code>
                • Пароль: ожидается</blockquote>

                %s <b>Отправьте платёжный пароль Market.CSGO</b>
                <blockquote>Пароль должен быть единым для всех аккаунтов. При разных паролях сбор остановится; уже подтверждённые переводы останутся выполненными.</blockquote>
                """, DynamicEmoji.YOUTRADE.getEmoji(), DynamicEmoji.MONEY.getEmoji(),
                registry.getOrCreate(user, MarketCsgoCollectData::new).getTokenId(), DynamicEmoji.WRITE.getEmoji());
    }

    @Override
    public UserMenu onNoCallback(TelegramClient bot, Update update, UserData user) {
        if (!update.hasMessage() || !update.getMessage().hasText()) {
            sender.sendTextMes(bot, user, "Отправьте значение текстовым сообщением.");
            return supportedState();
        }
        var data = registry.getOrCreate(user, MarketCsgoCollectData::new);
        synchronized (data) {
            if (data.isCompleted())
                return UserMenu.ACCOUNTS_COLLECT_RESULT;
            if (data.isProcessing())
                return UserMenu.ACCOUNTS_COLLECT_PROCESSING;
            sender.deleteMes(bot, user, update.getMessage()::getMessageId, null);
            String password = update.getMessage().getText().trim();
            if (password.isEmpty()) {
                sender.sendTextMes(bot, user, "Платёжный пароль не может быть пустым.");
                return supportedState();
            }
            data.setProcessing(true);
            processingState.show(bot, user);
            FcdMarketCsgoBalanceTransferInput input = new FcdMarketCsgoBalanceTransferInput();
            input.setDestinationTokenId(data.getTokenId());
            input.setPaymentPassword(password);
            try {
                var answer = endpoint.balanceTransfer(user.getChatId(), input);
                var response = answer.getResponse();
                data.setResponse(answer.getStatus() >= 300
                        && (response == null || response.isResult()) ? null : response);
            } catch (RuntimeException error) {
                data.setResponse(null);
            } finally {
                input.setPaymentPassword(null);
                data.setCompleted(true);
                data.setProcessing(false);
            }
            return UserMenu.ACCOUNTS_COLLECT_RESULT;
        }
    }

    @Override
    public UserMenu retState() {
        return UserMenu.ACCOUNTS;
    }
}
