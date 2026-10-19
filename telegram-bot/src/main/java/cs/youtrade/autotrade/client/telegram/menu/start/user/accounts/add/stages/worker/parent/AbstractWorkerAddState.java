package cs.youtrade.autotrade.client.telegram.menu.start.user.accounts.add.stages.worker.parent;

import cs.youtrade.autotrade.client.telegram.localization.BotTexts;
import cs.youtrade.autotrade.client.telegram.localization.LocalizedTexts;
import cs.youtrade.autotrade.client.telegram.menu.UserMenu;
import cs.youtrade.autotrade.client.telegram.menu.start.user.accounts.add.stages.worker.WorkerAddData;
import cs.youtrade.autotrade.client.telegram.menu.start.user.accounts.add.stages.worker.WorkerAddRegistry;
import cs.youtrade.autotrade.client.telegram.prototype.data.UserData;
import cs.youtrade.autotrade.client.telegram.prototype.menu.text.YTPTerminalTextMenuState;
import cs.youtrade.autotrade.client.telegram.prototype.sender.text.UserTextMessageSender;
import cs.youtrade.autotrade.client.util.emoji.DynamicEmoji;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.generics.TelegramClient;

import java.util.Map;
import java.util.function.Supplier;

public abstract class AbstractWorkerAddState extends YTPTerminalTextMenuState {
    private final WorkerAddRegistry registry;

    public AbstractWorkerAddState(
            UserTextMessageSender sender,
            WorkerAddRegistry registry,
            BotTexts botTexts
    ) {
        super(sender, botTexts);
        this.registry = registry;
    }

    @Override
    public UserMenu retState() {
        return UserMenu.ACCOUNTS;
    }

    @Override
    public UserMenu onNoCallback(TelegramClient bot, Update update, UserData userData) {
        var translations = botTexts.forUser(userData, "accounts.add.WorkerAdd");
        Supplier<Integer> mesIdSupplier = () -> update.getMessage().getMessageId();
        sender.deleteMes(bot, userData, mesIdSupplier, null);

        if (!update.hasMessage()) {
            sender.sendTextMes(bot, userData, String.format("#0: %s",
                    translations.get("empty-message-received-going-back")));
            return UserMenu.USER;
        }

        String text = update.getMessage().getText();
        var data = registry.getOrCreate(userData, WorkerAddData::new);
        setField(text, data);
        return getNextState(bot, update, userData);
    }

    public abstract void setField(String text, WorkerAddData data);

    public abstract UserMenu getNextState(TelegramClient bot, Update update, UserData userData);

    public static String getDefaultSteamWarning(LocalizedTexts translations) {
        return String.format("""
                <blockquote>%s %s
                
                %s</blockquote>""",
                DynamicEmoji.WARNING.getEmoji(),
                translations.format("warning-your-account-login-password-and-mafile", Map.of("value1", "<b>Y.CS</b>", "emphasis", String.format("<b>%s</b>", translations.get("warning-your-account-login-password-and-mafile.emphasis")))),
                translations.format("do-not-share-these-details-with-anyone", Map.of("emphasis", String.format("<b>%s</b>", translations.get("do-not-share-these-details-with-anyone.emphasis")))));
    }
}
