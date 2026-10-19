package cs.youtrade.autotrade.client.telegram.menu.start.user.accounts.transfer.stagep;

import cs.youtrade.autotrade.client.telegram.localization.BotTexts;
import cs.youtrade.autotrade.client.telegram.localization.LocalizedTexts;
import cs.youtrade.autotrade.client.telegram.menu.UserMenu;
import cs.youtrade.autotrade.client.telegram.menu.start.user.accounts.transfer.UserTokenTransferRegistry;
import cs.youtrade.autotrade.client.telegram.prototype.data.UserData;
import cs.youtrade.autotrade.client.telegram.prototype.menu.text.YTPTerminalTextMenuState;
import cs.youtrade.autotrade.client.telegram.prototype.sender.text.UserTextMessageSender;
import cs.youtrade.autotrade.client.util.autotrade.dto.AbstractFcdDto;
import cs.youtrade.autotrade.client.util.autotrade.dto.user.accounts.FcdAccountTransferResultDto;
import cs.youtrade.autotrade.client.util.autotrade.dto.user.accounts.FcdAccountsTransferDto;
import cs.youtrade.autotrade.client.util.autotrade.endpoint.user.accounts.AccountsV2Endpoint;
import cs.youtrade.autotrade.client.util.emoji.DynamicEmoji;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.generics.TelegramClient;

import java.util.Map;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Log4j2
public class TokenTransferProceedState extends YTPTerminalTextMenuState {
    private final UserTokenTransferRegistry registry;
    private final AccountsV2Endpoint endpoint;

    public TokenTransferProceedState(
            UserTextMessageSender sender,
            UserTokenTransferRegistry registry,
            AccountsV2Endpoint endpoint,
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
        return UserMenu.ACCOUNTS_TRANSFER_STAGE_P;
    }

    @Override
    public String getHeaderText(TelegramClient bot, UserData user) {
        var translations = botTexts.forUser(user, "accounts.TokenTransfer");
        var data = registry.remove(user);
        var restAns = endpoint.transfer(user.getChatId(), data);
        // 1. Checking if the answer came back from server
        if (restAns.getStatus() >= 300)
            return null;
        // 2. Checking if the method worked correctly
        var fcd = restAns.getResponse();
        if (!fcd.isResult())
            return fcd.getCause();
        // 3. Forming the success part of message
        var successDeleteStr = getSuccessfullyTransferredStr(fcd);
        String ans = completeSuccessStr(fcd, successDeleteStr, translations);
        var errorDeleteStr = getErrorTransferredStr(fcd);
        return completeErrorStr(fcd, ans, errorDeleteStr, translations);
    }

    private String completeSuccessStr(FcdAccountsTransferDto fcd, String successDeleteStr, LocalizedTexts translations) {
        String ans = String.format("%s %s",
                DynamicEmoji.SUCCESS.getEmoji(),
                translations.format("accounts-moved-b", Map.of("value1", String.format("<i>%s</i>", fcd.getDestinationParametersName()), "emphasis", String.format("<b>%s</b>", translations.get("accounts-moved-b.emphasis")))));
        if (!successDeleteStr.isEmpty())
            return String.format("""
                            %s
                            <blockquote>%s</blockquote>""",
                    ans, successDeleteStr);
        else
            return String.format("""
                            %s
                            <blockquote><i>%s</i></blockquote>""",
                            ans,
                            translations.get("the-account-list-is-empty"));
    }

    private String completeErrorStr(FcdAccountsTransferDto fcd, String ans, String errorDeleteStr, LocalizedTexts translations) {
        if (errorDeleteStr.isEmpty())
            return ans;
        else
            return String.format("""
                            %s
                            
                            %s %s
                            <blockquote>%s</blockquote>
                            """,
                            ans,
                            DynamicEmoji.ERROR.getEmoji(),
                            translations.format("move-failed-b", Map.of("value1", String.format("<i>%s</i>", fcd.getDestinationParametersName()), "emphasis", String.format("<b>%s</b>", translations.get("move-failed-b.emphasis")))),
                            errorDeleteStr);
    }

    // --- Assistive methods
    private String getSuccessfullyTransferredStr(FcdAccountsTransferDto fcd) {
        return fcd
                .getAnswers()
                .stream()
                .filter(AbstractFcdDto::isResult)
                .map(ans -> "<b><code>" + ans.getTokenId() + "</code></b>")
                .collect(Collectors.joining(" "));
    }

    private String getErrorTransferredStr(FcdAccountsTransferDto fcd) {
        return fcd
                .getAnswers()
                .stream()
                .filter(ans -> !ans.isResult())
                .collect(Collectors.groupingBy(
                        FcdAccountTransferResultDto::getStatus,
                        Collectors.collectingAndThen(Collectors.toList(), this::getErrorIdsStr))
                )
                .entrySet()
                .stream()
                .map(entry ->
                        "<b>#" + entry.getKey().ordinal() + "</b>: " + entry.getValue())
                .collect(Collectors.joining("\n"));
    }

    private String getErrorIdsStr(List<FcdAccountTransferResultDto> errorList) {
        return errorList.stream().map(ans ->
                "<b><code>" + ans.getTokenId() + "</code></b>").collect(Collectors.joining(" "));
    }
}
