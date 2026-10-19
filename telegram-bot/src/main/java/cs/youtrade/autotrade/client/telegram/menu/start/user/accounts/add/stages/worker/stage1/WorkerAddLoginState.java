package cs.youtrade.autotrade.client.telegram.menu.start.user.accounts.add.stages.worker.stage1;

import cs.youtrade.autotrade.client.telegram.localization.BotTexts;
import java.util.Map;
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
public class WorkerAddLoginState extends AbstractWorkerAddState {
    public WorkerAddLoginState(
            UserTextMessageSender sender,
            WorkerAddRegistry registry,
            BotTexts botTexts
    ) {
        super(sender, registry, botTexts);
    }

    @Override
    public UserMenu supportedState() {
        return UserMenu.ACCOUNTS_ADD_STAGE_1_WORKER;
    }

    @Override
    public String getHeaderText(TelegramClient bot, UserData userData) {
        var translations = botTexts.forUser(userData, "accounts.add.WorkerAdd");
        return String.format("""
                        %s
                        
                        %s %s""",
                        getDefaultSteamWarning(translations),
                        DynamicEmoji.STEAM.getEmoji(),
                        translations.format("send-credential", Map.of(
                                "action", String.format("<b>%s</b>", translations.get("send")),
                                "credential", String.format("<b><a href=\"%s\">%s</a>%s</b>",
                                        "https://store.steampowered.com/account/authorizeddevices",
                                        translations.get("steam-login"), translations.get("steam-login-suffix")))));
    }

    @Override
    public void setField(String text, WorkerAddData data) {
        data.setLogin(text);
    }

    @Override
    public UserMenu getNextState(TelegramClient bot, Update update, UserData userData) {
        return UserMenu.ACCOUNTS_ADD_STAGE_2_WORKER;
    }
}
