package cs.youtrade.autotrade.client.telegram.menu.start.user.accounts.add.stages.worker.stage2;

import cs.youtrade.autotrade.client.telegram.menu.UserMenu;
import cs.youtrade.autotrade.client.telegram.menu.start.user.accounts.add.stages.worker.WorkerAddData;
import cs.youtrade.autotrade.client.telegram.menu.start.user.accounts.add.stages.worker.WorkerAddRegistry;
import cs.youtrade.autotrade.client.telegram.menu.start.user.accounts.add.stages.worker.parent.AbstractWorkerAddState;
import cs.youtrade.autotrade.client.telegram.prototype.data.UserData;
import cs.youtrade.autotrade.client.telegram.prototype.sender.text.UserTextMessageSender;
import cs.youtrade.autotrade.client.util.emoji.DynamicEmoji;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.generics.TelegramClient;

@Service
public class WorkerAddPasswordState extends AbstractWorkerAddState {
    public WorkerAddPasswordState(UserTextMessageSender sender, WorkerAddRegistry registry) {
        super(sender, registry);
    }

    @Override
    public UserMenu supportedState() {
        return UserMenu.ACCOUNTS_ADD_STAGE_2_WORKER;
    }

    @Override
    public String getHeaderText(TelegramClient bot, UserData userData) {
        return String.format("""
                        %s Теперь <b>отправьте</b> в этот чат <b><a href="%s">пароль</a> от Steam-аккаунта</b>, который хотите добавить
                        
                        <blockquote>%s <b>ВНИМАНИЕ! Логин, пароль и maFile аккаунта требуются для авторизации в Steam.</b> После авторизации YouTrade.CS сможет автоматически принимать и передавать предметы. <b>Никому больше не сообщайте эти данные!</b> Бот никогда не запросит их повторно.</blockquote>
                        """,
                DynamicEmoji.STEAM.getEmoji(),
                "https://store.steampowered.com/account/authorizeddevices",
                DynamicEmoji.WARNING.getEmoji()
        );
    }

    @Override
    public void setField(String text, WorkerAddData data) {
        data.setPassword(text);
    }

    @Override
    public UserMenu getNextState(TelegramClient bot, Update update, UserData userData) {
        return UserMenu.ACCOUNTS_ADD_STAGE_3_WORKER;
    }
}
