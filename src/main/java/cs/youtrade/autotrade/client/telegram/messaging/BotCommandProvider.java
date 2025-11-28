package cs.youtrade.autotrade.client.telegram.messaging;

import cs.youtrade.autotrade.client.telegram.menu.UserMenu;
import cs.youtrade.autotrade.client.telegram.prototype.data.UserData;
import lombok.Getter;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.commands.BotCommand;

import java.util.List;

@Service
public class BotCommandProvider {
    public List<BotCommand> getBotCommands() {
        return UserMenu.getCommands();
    }

    public UserMenu getCommandByCmd(String cmd) {
        return UserMenu.getByTextCmd(cmd);
    }
}
