package cs.youtrade.autotrade.client.telegram.messaging;

import cs.youtrade.autotrade.client.telegram.menu.UserMenu;
import cs.youtrade.autotrade.client.telegram.localization.BotTexts;
import cs.youtrade.autotrade.client.telegram.prototype.data.UserData;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.commands.SetMyCommands;
import org.telegram.telegrambots.meta.api.objects.commands.BotCommand;
import org.telegram.telegrambots.meta.api.objects.commands.scope.BotCommandScopeChat;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.meta.generics.TelegramClient;

import java.util.List;
import java.util.Locale;

@Service
@RequiredArgsConstructor
public class BotCommandProvider {
    private final BotTexts texts;

    public List<BotCommand> getBotCommands(UserData user) {
        var text = texts.forUser(user, "common.Commands");
        if (user.isBlocked())
            return List.of(UserMenu.START.toBotCommand(text.get("blocked-start")),
                    UserMenu.LANGUAGE.toBotCommand(text.get("language")));
        return UserMenu.getCommands(user.isQualified(),
                menu -> text.get(menu.name().toLowerCase(Locale.ROOT).replace('_', '-')));
    }

    public void updateCommands(TelegramClient bot, UserData user) throws TelegramApiException {
        bot.execute(SetMyCommands.builder()
                .commands(getBotCommands(user))
                .scope(new BotCommandScopeChat(user.getChatId().toString()))
                .build());
    }

    public UserMenu getCommandByCmd(String cmd, boolean qualified) {
        return UserMenu.getByTextCmd(cmd, qualified);
    }
}
