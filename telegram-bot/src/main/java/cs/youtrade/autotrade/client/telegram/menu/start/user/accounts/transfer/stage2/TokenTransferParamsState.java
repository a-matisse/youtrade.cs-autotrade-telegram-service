package cs.youtrade.autotrade.client.telegram.menu.start.user.accounts.transfer.stage2;

import cs.youtrade.autotrade.client.telegram.menu.UserMenu;
import cs.youtrade.autotrade.client.telegram.menu.start.user.accounts.transfer.UserTokenTransferRegistry;
import cs.youtrade.autotrade.client.telegram.prototype.data.UserData;
import cs.youtrade.autotrade.client.telegram.prototype.menu.text.YTPTerminalTextMenuState;
import cs.youtrade.autotrade.client.telegram.prototype.sender.text.UserTextMessageSender;
import cs.youtrade.autotrade.client.util.autotrade.dto.FcdDefaultDto;
import cs.youtrade.autotrade.client.util.autotrade.dto.user.params.FcdParamsListDto;
import cs.youtrade.autotrade.client.util.autotrade.endpoint.user.accounts.dto.FcdAccountsTransferInput;
import cs.youtrade.autotrade.client.util.autotrade.endpoint.user.params.ParamsEndpoint;
import cs.youtrade.autotrade.client.util.emoji.DynamicEmoji;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.generics.TelegramClient;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Log4j2
public class TokenTransferParamsState extends YTPTerminalTextMenuState {
    private final UserTokenTransferRegistry registry;
    private final ParamsEndpoint endpoint;

    public TokenTransferParamsState(
            UserTextMessageSender sender,
            UserTokenTransferRegistry registry,
            ParamsEndpoint endpoint
    ) {
        super(sender);
        this.registry = registry;
        this.endpoint = endpoint;
    }

    @Override
    public UserMenu retState() {
        return UserMenu.ACCOUNTS;
    }

    @Override
    public UserMenu supportedState() {
        return UserMenu.ACCOUNTS_TRANSFER_STAGE_2;
    }

    @Override
    public UserMenu onNoCallback(TelegramClient bot, Update update, UserData user) {
        if (!update.hasMessage()) {
            sender.sendTextMes(bot, user, "#0: Получено пустое сообщение. Возвращение обратно...");
            return UserMenu.ACCOUNTS;
        }

        String input = update.getMessage().getText();
        var data = registry.getOrCreate(user, FcdAccountsTransferInput::new);
        data.setDestinationParameters(input);
        return UserMenu.ACCOUNTS_TRANSFER_STAGE_P;
    }

    @Override
    public String getHeaderText(TelegramClient bot, UserData user) {
        var restAns = endpoint.listParams(user.getChatId());
        if (restAns.getStatus() >= 300)
            return null;

        var fcd = restAns.getResponse();
        if (!fcd.isResult())
            return null;

        var data = registry.getOrCreate(user, FcdAccountsTransferInput::new);
        String tokens = data.getTokenIds().stream().map(Object::toString).collect(Collectors.joining(" "));
        return String.format("""
                        %s <b>Теперь укажите параметры, на которые хотите перевести токены</b>
                        <blockquote>• Выбранные токены — <code>%s</code></blockquote>
                        
                        %s <i>Выберите одни из списка параметров ниже (можете указать имя или ID)</i>
                        <blockquote expandable>%s</blockquote>
                        """,
                DynamicEmoji.WRITE.getEmoji(),
                tokens,
                DynamicEmoji.SUCCESS.getEmoji(),
                getStringFromFcd(fcd));
    }

    private String getStringFromFcd(FcdDefaultDto<List<FcdParamsListDto>> fcd) {
        return fcd
                .getData()
                .stream()
                .map(FcdParamsListDto::asMessage)
                .collect(Collectors.joining("\n"));
    }
}
