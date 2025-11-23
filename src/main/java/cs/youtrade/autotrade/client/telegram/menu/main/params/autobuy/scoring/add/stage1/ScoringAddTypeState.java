package cs.youtrade.autotrade.client.telegram.menu.main.params.autobuy.scoring.add.stage1;

import cs.youtrade.autotrade.client.telegram.menu.UserMenu;
import cs.youtrade.autotrade.client.telegram.menu.main.params.autobuy.scoring.add.ScoringAddData;
import cs.youtrade.autotrade.client.telegram.menu.main.params.autobuy.scoring.add.ScoringAddRegistry;
import cs.youtrade.autotrade.client.telegram.prototype.data.UserData;
import cs.youtrade.autotrade.client.telegram.prototype.def.AbstractTextState;
import cs.youtrade.autotrade.client.telegram.prototype.sender.text.UserTextMessageSender;
import cs.youtrade.autotrade.client.util.autotrade.ItemScoringType;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.generics.TelegramClient;

import java.util.Arrays;
import java.util.stream.Collectors;

import static cs.youtrade.autotrade.client.util.autotrade.FcdStringUtils.findClosest;

@Service
public class ScoringAddTypeState extends AbstractTextState {
    private final ScoringAddRegistry registry;

    public ScoringAddTypeState(
            UserTextMessageSender sender,
            ScoringAddRegistry registry
    ) {
        super(sender);
        this.registry = registry;
    }

    @Override
    protected String getMessage() {
        return String.format("Пожалуйста, введите тип scoring-ID (Доступные: %s)...", getTypes());
    }

    @Override
    public UserMenu supportedState() {
        return UserMenu.SCORING_ADD_STAGE_1;
    }

    @Override
    public UserMenu execute(TelegramClient bot, Update update, UserData user) {
        long chatId = user.getChatId();
        if (!update.hasMessage()) {
            sender.sendTextMes(bot, chatId, "#0: Получено пустое сообщение. Возвращение обратно...");
            return UserMenu.SCORING;
        }

        String input = update.getMessage().getText();
        ItemScoringType type = findClosestItemScoringType(input);
        if (type == null) {
            sender.sendTextMes(bot, chatId, "#1: Не удалось распознать ItemScoringType: " + input);
            return UserMenu.SCORING;
        }

        var data = registry.getOrCreate(user, ScoringAddData::new);
        data.setType(type);
        return UserMenu.SCORING_ADD_STAGE_2;
    }

    private String getTypes() {
        return Arrays.stream(ItemScoringType.values())
                .map(ItemScoringType::name)
                .collect(Collectors.joining(", "));
    }

    private ItemScoringType findClosestItemScoringType(String input) {
        return findClosest(ItemScoringType.values(), input, 5);
    }
}
