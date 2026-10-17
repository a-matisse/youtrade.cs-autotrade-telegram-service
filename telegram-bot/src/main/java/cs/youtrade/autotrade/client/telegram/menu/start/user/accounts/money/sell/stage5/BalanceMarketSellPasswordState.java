package cs.youtrade.autotrade.client.telegram.menu.start.user.accounts.money.sell.stage5;

import cs.youtrade.autotrade.client.telegram.menu.start.user.accounts.money.sell.BalanceMarketSellData;
import cs.youtrade.autotrade.client.telegram.menu.start.user.accounts.money.sell.AbstractBalanceMarketSellTerminalState;
import cs.youtrade.autotrade.client.telegram.menu.start.user.accounts.money.sell.BalanceMarketSellRegistry;
import cs.youtrade.autotrade.client.telegram.menu.start.user.accounts.money.sell.BalanceMarketSellPasswordCleaner;
import cs.youtrade.autotrade.client.telegram.menu.start.user.accounts.money.sell.BalanceMarketSellText;

import cs.youtrade.autotrade.client.telegram.menu.UserMenu;
import cs.youtrade.autotrade.client.telegram.prototype.data.UserData;
import cs.youtrade.autotrade.client.telegram.prototype.sender.text.UserTextMessageSender;
import cs.youtrade.autotrade.client.util.autotrade.dto.user.accounts.BalanceMarketDto;
import cs.youtrade.autotrade.client.util.autotrade.endpoint.user.accounts.BalanceMarketEndpoint;
import cs.youtrade.autotrade.client.util.emoji.DynamicEmoji;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.generics.TelegramClient;

@Service
public class BalanceMarketSellPasswordState extends AbstractBalanceMarketSellTerminalState {
    private final BalanceMarketEndpoint endpoint;
    private final BalanceMarketSellPasswordCleaner passwordCleaner;

    public BalanceMarketSellPasswordState(UserTextMessageSender sender, BalanceMarketSellRegistry registry,
                                          BalanceMarketSellText text, BalanceMarketEndpoint endpoint,
                                          BalanceMarketSellPasswordCleaner passwordCleaner) {
        super(sender, registry, text);
        this.endpoint = endpoint;
        this.passwordCleaner = passwordCleaner;
    }

    @Override
    public UserMenu supportedState() { return UserMenu.ACCOUNTS_SELL_PASSWORD; }

    @Override
    public String getHeaderText(TelegramClient bot, UserData user) {
        return String.format("""
                %s%s <b>Отправьте платёжный пароль Market.CSGO</b>
                └ <i>Сообщение с паролем будет сразу удалено. Затем покажем расчёт для проверки.</i>
                """, text.summary(registry.getOrCreate(user, BalanceMarketSellData::new)),
                DynamicEmoji.WRITE.getEmoji());
    }

    @Override
    public UserMenu onNoCallback(TelegramClient bot, Update update, UserData user) {
        if (!hasText(bot, update, user)) return supportedState();
        sender.deleteMes(bot, user, update.getMessage()::getMessageId, null);
        String password = update.getMessage().getText().trim();
        if (password.isEmpty()) {
            sender.sendTextMes(bot, user, "Платёжный пароль не может быть пустым.");
            return supportedState();
        }
        var data = registry.getOrCreate(user, BalanceMarketSellData::new);
        synchronized (data) {
            data.setPassword(password);
            passwordCleaner.track(data);
            data.setQuote(null);
            try {
                var answer = endpoint.quote(user.getChatId(),
                        new BalanceMarketDto.QuoteInput(data.getAccountId(), data.getAmount(), data.getMethod()));
                if (answer.getStatus() < 300 && answer.getResponse() != null
                        && answer.getResponse().isResult() && text.completeQuote(answer.getResponse().getData())) {
                    data.setQuote(answer.getResponse().getData());
                    return UserMenu.ACCOUNTS_SELL_REVIEW;
                }
            } catch (RuntimeException ignored) { }
            data.eraseSecrets();
            sender.sendTextMes(bot, user, "Котировка сейчас недоступна. Проверьте баланс и повторите запрос позже.");
            return supportedState();
        }
    }
}
