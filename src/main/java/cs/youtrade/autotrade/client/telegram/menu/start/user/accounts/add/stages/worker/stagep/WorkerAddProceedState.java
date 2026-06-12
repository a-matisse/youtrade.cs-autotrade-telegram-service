package cs.youtrade.autotrade.client.telegram.menu.start.user.accounts.add.stages.worker.stagep;

import cs.youtrade.autotrade.client.telegram.menu.UserMenu;
import cs.youtrade.autotrade.client.telegram.menu.start.user.accounts.add.parent.proceed.AbstractAddProceedState;
import cs.youtrade.autotrade.client.telegram.menu.start.user.accounts.add.stages.worker.WorkerAddRegistry;
import cs.youtrade.autotrade.client.telegram.prototype.data.UserData;
import cs.youtrade.autotrade.client.telegram.prototype.sender.text.UserTextMessageSender;
import cs.youtrade.autotrade.client.util.autotrade.endpoint.user.accounts.AccountsV2Endpoint;
import cs.youtrade.autotrade.client.util.autotrade.util.accounts.MaFileTokenAddInput;
import cs.youtrade.autotrade.client.util.emoji.DynamicEmoji;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.generics.TelegramClient;

@Service
public class WorkerAddProceedState extends AbstractAddProceedState {
    private final WorkerAddRegistry registry;
    private final AccountsV2Endpoint endpoint;

    public WorkerAddProceedState(
            UserTextMessageSender sender,
            WorkerAddRegistry registry,
            AccountsV2Endpoint endpoint
    ) {
        super(sender);
        this.registry = registry;
        this.endpoint = endpoint;
    }

    @Override
    public UserMenu supportedState() {
        return UserMenu.ACCOUNTS_ADD_STAGE_P_WORKER;
    }

    @Override
    public String getHeaderText(TelegramClient bot, UserData userData) {
        var data = registry.remove(userData);
        var req = MaFileTokenAddInput
                .builder()
                .login(data.getLogin())
                .password(data.getPassword())
                .maFile(data.getMaFile())
                .build();
        var restAns = endpoint.addWorker(userData.getChatId(), req);
        if (restAns.getStatus() >= 300)
            return null;

        var fcd = restAns.getResponse();
        if (!fcd.isResult())
            return fcd.getCause();

        return String.format("%s <b>Воркер-аккаунт успешно подключен! (Имя Steam-аккаунта: <tg-spoiler>\"%s\"</tg-spoiler>)</b>",
                DynamicEmoji.SUCCESS.getEmoji(),
                data.getLogin()
        );
    }
}
