package cs.youtrade.autotrade.client.telegram.menu.start.user.accounts.money.collect.stage3;

import cs.youtrade.autotrade.client.telegram.menu.start.user.accounts.money.collect.MarketCsgoCollectData;
import cs.youtrade.autotrade.client.telegram.menu.start.user.accounts.money.collect.MarketCsgoCollectRegistry;

import cs.youtrade.autotrade.client.telegram.menu.UserMenu;
import cs.youtrade.autotrade.client.telegram.prototype.data.UserData;
import cs.youtrade.autotrade.client.telegram.prototype.menu.text.base.YTPTextMenuState;
import cs.youtrade.autotrade.client.telegram.prototype.sender.text.UserTextMessageSender;
import cs.youtrade.autotrade.client.util.emoji.DynamicEmoji;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.meta.generics.TelegramClient;

@Service
@Log4j2
public class MarketCsgoCollectProcessingState extends YTPTextMenuState<MarketCsgoCollectProcessingMenu> {
    private final MarketCsgoCollectRegistry registry;

    public MarketCsgoCollectProcessingState(UserTextMessageSender sender, MarketCsgoCollectRegistry registry) {
        super(sender);
        this.registry = registry;
    }

    @Override
    public UserMenu supportedState() { return UserMenu.ACCOUNTS_COLLECT_PROCESSING; }

    @Override
    public UserMenu executeCallback(TelegramClient bot, Update update, UserData user, MarketCsgoCollectProcessingMenu option) {
        return registry.getOrCreate(user, MarketCsgoCollectData::new).isCompleted()
                ? UserMenu.ACCOUNTS_COLLECT_RESULT : supportedState();
    }

    @Override
    public String getHeaderText(TelegramClient bot, UserData user) {
        return String.format("""
                %s <i>Сбор баланса Market.CSGO</i>

                %s <b>Получатель</b>
                <blockquote>• Аккаунт: <code>%s</code>
                • Пароль: ✅ Получен</blockquote>

                %s <b>Собираем балансы...</b>
                """, DynamicEmoji.YOUTRADE.getEmoji(), DynamicEmoji.MONEY.getEmoji(),
                registry.getOrCreate(user, MarketCsgoCollectData::new).getTokenId(), DynamicEmoji.WAIT.getEmoji());
    }

    public void show(TelegramClient bot, UserData user) {
        try {
            bot.execute(buildEdit(bot, user));
        } catch (TelegramApiException | RuntimeException error) {
            log.warn("Couldn't show balance collection progress: {}", error.getMessage());
        }
    }

    @Override
    public MarketCsgoCollectProcessingMenu getOption(String value) {
        return MarketCsgoCollectProcessingMenu.valueOf(value);
    }

    @Override
    public MarketCsgoCollectProcessingMenu[] getOptions(UserData user) {
        return MarketCsgoCollectProcessingMenu.values();
    }
}
