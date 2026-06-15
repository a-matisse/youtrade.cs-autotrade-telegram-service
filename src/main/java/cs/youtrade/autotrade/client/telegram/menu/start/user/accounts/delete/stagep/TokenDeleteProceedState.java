package cs.youtrade.autotrade.client.telegram.menu.start.user.accounts.delete.stagep;

import cs.youtrade.autotrade.client.telegram.menu.UserMenu;
import cs.youtrade.autotrade.client.telegram.menu.start.user.accounts.delete.UserTokenDeleteRegistry;
import cs.youtrade.autotrade.client.telegram.prototype.data.UserData;
import cs.youtrade.autotrade.client.telegram.prototype.menu.text.YTPTerminalTextMenuState;
import cs.youtrade.autotrade.client.telegram.prototype.sender.text.UserTextMessageSender;
import cs.youtrade.autotrade.client.util.autotrade.dto.AbstractFcdDto;
import cs.youtrade.autotrade.client.util.autotrade.dto.user.accounts.FcdCodeAnsDto;
import cs.youtrade.autotrade.client.util.autotrade.dto.user.accounts.FcdCodeBulkAnswer;
import cs.youtrade.autotrade.client.util.autotrade.endpoint.user.accounts.AccountsV2Endpoint;
import cs.youtrade.autotrade.client.util.emoji.DynamicEmoji;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.generics.TelegramClient;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class TokenDeleteProceedState extends YTPTerminalTextMenuState {
    private final UserTokenDeleteRegistry registry;
    private final AccountsV2Endpoint endpoint;

    public TokenDeleteProceedState(
            UserTextMessageSender sender,
            UserTokenDeleteRegistry registry,
            AccountsV2Endpoint endpoint
    ) {
        super(sender);
        this.registry = registry;
        this.endpoint = endpoint;
    }

    @Override
    public UserMenu supportedState() {
        return UserMenu.ACCOUNTS_REMOVE_STAGE_P;
    }

    @Override
    public UserMenu retState() {
        return UserMenu.ACCOUNTS;
    }

    @Override
    public String getHeaderText(TelegramClient bot, UserData user) {
        // 1. Preparing the data
        var data = registry.remove(user);
        long chatId = user.getChatId();
        var tokenIds = data.getTokenIds();
        // 2. Sending the request to server
        var restAns = switch (data.getOpt()) {
            case BUYER_ACCOUNT -> endpoint.deleteBuyer(chatId, tokenIds);
            case SELLER_ACCOUNT -> endpoint.deleteSeller(chatId, tokenIds);
            case WORKER_ACCOUNT -> endpoint.deleteWorker(chatId, tokenIds);
            case RETURN -> null;
        };
        // 3. Checking if the answer came back from server
        if (restAns == null || restAns.getStatus() >= 300)
            return null;
        // 4. Checking if the method worked correctly
        var fcd = restAns.getResponse();
        if (!fcd.isResult())
            return fcd.getCause();
        // 5. Forming the success part of message
        var successDeleteStr = getSuccessfullyDeletedStr(fcd);
        String ans = completeSuccessStr(successDeleteStr);
        var errorDeleteStr = getErrorDeletedStr(fcd);
        return completeErrorStr(ans, errorDeleteStr);
    }

    private String completeSuccessStr(String successDeleteStr) {
        String ans = String.format("%s <b>Аккаунты успешно удалены</b>",
                DynamicEmoji.SUCCESS.getEmoji());
        if (!successDeleteStr.isEmpty())
            return String.format("""
                            %s
                            <blockquote>%s</blockquote>""",
                    ans, successDeleteStr);
        else
            return String.format("""
                            %s
                            <blockquote><i>Список аккаунтов пуст</i></blockquote>""",
                    ans);
    }

    private String completeErrorStr(String ans, String errorDeleteStr) {
        if (errorDeleteStr.isEmpty())
            return ans;
        else
            return String.format("""
                            %s
                            
                            %s <b>Удаление завершилось неудачно</b>
                            <blockquote>%s</blockquote>
                            """,
                    ans,
                    DynamicEmoji.ERROR.getEmoji(), errorDeleteStr);
    }

    // --- Assistive methods
    private String getSuccessfullyDeletedStr(FcdCodeBulkAnswer fcd) {
        return fcd
                .getAnswers()
                .stream()
                .filter(AbstractFcdDto::isResult)
                .map(ans -> "<b><code>" + ans.getTokenId() + "</code></b>")
                .collect(Collectors.joining(" "));
    }

    private String getErrorDeletedStr(FcdCodeBulkAnswer fcd) {
        return fcd
                .getAnswers()
                .stream()
                .filter(ans -> !ans.isResult())
                .map(this::modifyCode)
                .collect(Collectors.groupingBy(FcdCodeAnsDto::getCode, Collectors.collectingAndThen(
                        Collectors.toList(),
                        this::getErrorIdsStr
                )))
                .entrySet()
                .stream()
                .map(entry -> "<b>#" + entry.getKey() + "</b>: " + entry.getValue())
                .collect(Collectors.joining("\n"));
    }

    private String getErrorIdsStr(List<FcdCodeAnsDto> errorList) {
        return errorList.stream().map(ans ->
                "<b><code>" + ans.getTokenId() + "</code></b>").collect(Collectors.joining(" "));
    }

    private FcdCodeAnsDto modifyCode(FcdCodeAnsDto ans) {
        if (ans.getCode() == null) ans.setCode(-2);
        return ans;
    }
}
