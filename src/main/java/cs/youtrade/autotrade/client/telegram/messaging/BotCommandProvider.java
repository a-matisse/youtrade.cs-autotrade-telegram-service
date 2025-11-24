package cs.youtrade.autotrade.client.telegram.messaging;

import lombok.Getter;
import org.telegram.telegrambots.meta.api.objects.commands.BotCommand;

import java.util.List;

public class BotCommandProvider {
    @Getter
    private static final List<BotCommand> DEF_COMMANDS = List.of(
            new BotCommand("/start",
                    "Содержит описание бота"),
            new BotCommand("/menu",
                    "Напишите для того, чтобы вывести главное меню")
    );
}
