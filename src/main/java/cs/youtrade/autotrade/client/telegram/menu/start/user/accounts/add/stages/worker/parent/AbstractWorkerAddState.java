package cs.youtrade.autotrade.client.telegram.menu.start.user.accounts.add.stages.worker.parent;

import cs.youtrade.autotrade.client.telegram.menu.UserMenu;
import cs.youtrade.autotrade.client.telegram.menu.start.user.accounts.add.stages.worker.WorkerAddData;
import cs.youtrade.autotrade.client.telegram.menu.start.user.accounts.add.stages.worker.WorkerAddRegistry;
import cs.youtrade.autotrade.client.telegram.prototype.data.UserData;
import cs.youtrade.autotrade.client.telegram.prototype.menu.text.YTPTerminalTextMenuState;
import cs.youtrade.autotrade.client.telegram.prototype.sender.text.UserTextMessageSender;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.generics.TelegramClient;

public abstract class AbstractWorkerAddState extends YTPTerminalTextMenuState {
    private final WorkerAddRegistry registry;

    public AbstractWorkerAddState(
            UserTextMessageSender sender,
            WorkerAddRegistry registry
    ) {
        super(sender);
        this.registry = registry;
    }

    @Override
    public UserMenu retState() {
        return UserMenu.ACCOUNTS;
    }

    @Override
    public UserMenu onNoCallback(TelegramClient bot, Update update, UserData userData) {
        int mesId = update.getMessage().getMessageId();
        sender.deleteMes(bot, userData, mesId, null);

        if (!update.hasMessage()) {
            sender.sendTextMes(bot, userData, "#0: Получено пустое сообщение. Возвращение обратно...");
            return UserMenu.USER;
        }

        String text = update.getMessage().getText();
        var data = registry.getOrCreate(userData, WorkerAddData::new);
        setField(text, data);
        return getNextState(bot, update, userData);
    }

    public abstract void setField(String text, WorkerAddData data);

    public abstract UserMenu getNextState(TelegramClient bot, Update update, UserData userData);
}
