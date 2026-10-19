package cs.youtrade.autotrade.client.telegram.menu.start.user.accounts.transfer.stage2;

import cs.youtrade.autotrade.client.telegram.localization.BotTexts;
import cs.youtrade.autotrade.client.telegram.localization.LocalizedTexts;
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

import java.util.Map;
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
            ParamsEndpoint endpoint,
            BotTexts botTexts
    ) {
        super(sender, botTexts);
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
        var translations = botTexts.forUser(user, "accounts.TokenTransfer");
        if (!update.hasMessage()) {
            sender.sendTextMes(bot, user, String.format("#0: %s",
                    translations.get("empty-message-received-going-back")));
            return UserMenu.ACCOUNTS;
        }

        String input = update.getMessage().getText();
        var data = registry.getOrCreate(user, FcdAccountsTransferInput::new);
        data.setDestinationParameters(input);
        return UserMenu.ACCOUNTS_TRANSFER_STAGE_P;
    }

    @Override
    public String getHeaderText(TelegramClient bot, UserData user) {
        var translations = botTexts.forUser(user, "accounts.TokenTransfer");
        var restAns = endpoint.listParams(user.getChatId());
        if (restAns.getStatus() >= 300)
            return null;

        var fcd = restAns.getResponse();
        if (!fcd.isResult())
            return null;

        var data = registry.getOrCreate(user, FcdAccountsTransferInput::new);
        String tokens = data.getTokenIds().stream().map(Object::toString).collect(Collectors.joining(" "));
        return String.format("""
                        %s <b>%s</b>
                        <blockquote>• %s</blockquote>
                        
                        %s <i>%s</i>
                        <blockquote expandable>%s</blockquote>
                        """,
                        DynamicEmoji.WRITE.getEmoji(),
                        translations.get("now-specify-the-parameters-to-move-the"),
                        translations.format("selected-accounts", Map.of("value1", String.format("<code>%s</code>", tokens))),
                        DynamicEmoji.SUCCESS.getEmoji(),
                        translations.get("choose-one-set-of-parameters-below-enter"),
                        getStringFromFcd(fcd, translations));
    }

    private String getStringFromFcd(FcdDefaultDto<List<FcdParamsListDto>> fcd, LocalizedTexts translations) {
        return fcd
                .getData()
                .stream()
                .map(value -> value.asMessage(translations.inBundle("summary.ParamsSummary")))
                .collect(Collectors.joining("\n"));
    }
}
