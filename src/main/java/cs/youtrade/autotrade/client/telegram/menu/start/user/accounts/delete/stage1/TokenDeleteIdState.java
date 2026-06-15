package cs.youtrade.autotrade.client.telegram.menu.start.user.accounts.delete.stage1;

import cs.youtrade.autotrade.client.telegram.menu.UserMenu;
import cs.youtrade.autotrade.client.telegram.menu.start.user.accounts.delete.UserTokenDeleteData;
import cs.youtrade.autotrade.client.telegram.menu.start.user.accounts.delete.UserTokenDeleteRegistry;
import cs.youtrade.autotrade.client.telegram.menu.start.user.accounts.util.YTPAccountsPageProcessorDto;
import cs.youtrade.autotrade.client.telegram.menu.start.user.accounts.util.YTPPageProcessor;
import cs.youtrade.autotrade.client.telegram.prototype.data.UserData;
import cs.youtrade.autotrade.client.telegram.prototype.menu.PageMenu;
import cs.youtrade.autotrade.client.telegram.prototype.menu.text.YTPPageTextMenuState;
import cs.youtrade.autotrade.client.telegram.prototype.sender.text.UserTextMessageSender;
import cs.youtrade.autotrade.client.util.autotrade.endpoint.user.accounts.AccountsV2Endpoint;
import cs.youtrade.autotrade.client.util.autotrade.endpoint.user.params.ParamsEndpoint;
import cs.youtrade.autotrade.client.util.autotrade.util.accounts.FcdAccountV2Dto;
import cs.youtrade.autotrade.client.util.emoji.DynamicEmoji;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.generics.TelegramClient;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Log4j2
public class TokenDeleteIdState extends YTPPageTextMenuState {
    private final UserTokenDeleteRegistry registry;
    private final YTPPageProcessor pageProcessor;

    public TokenDeleteIdState(
            UserTextMessageSender sender,
            UserTokenDeleteRegistry registry,
            AccountsV2Endpoint endpoint,
            ParamsEndpoint paramsEndpoint
    ) {
        super(sender);
        this.registry = registry;
        this.pageProcessor = new YTPPageProcessor(endpoint, paramsEndpoint);
    }

    @Override
    public UserMenu supportedState() {
        return UserMenu.ACCOUNTS_REMOVE_STAGE_1;
    }

    @Override
    public UserMenu onNoCallback(TelegramClient bot, Update update, UserData userData) {
        if (!update.hasMessage()) {
            sender.sendTextMes(bot, userData, "#0: Получено пустое сообщение. Возвращение обратно...");
            return UserMenu.ACCOUNTS;
        }
        // Preparing the input
        String input = update.getMessage().getText();
        List<Long> tokenIds = new ArrayList<>();
        // Getting ids
        String[] parts = input.split("\\s+");
        for (String part : parts) {
            try {
                tokenIds.add(Long.parseLong(part));
            } catch (NumberFormatException e) {
                // Мусор отбрасываем, просто игнорируем
                log.debug("Skipping invalid number: {}", part);
            }
        }
        // If no correct ids were found - skipping
        if (tokenIds.isEmpty()) {
            sender.sendTextMes(bot, userData, "#1: Введенные значения не являются числами. Возвращение обратно...");
            return UserMenu.ACCOUNTS;
        }
        // Adding ids to the registry to complete on the next stage
        var data = registry.getOrCreate(userData, UserTokenDeleteData::new);
        data.setTokenIds(tokenIds);
        return UserMenu.ACCOUNTS_REMOVE_STAGE_P;
    }

    @Override
    public void onPreviousPage(TelegramClient bot, Update update, UserData userData, PageMenu t) {
        pageProcessor.decrementPage(userData.getChatId());
    }

    @Override
    public void onNextPage(TelegramClient bot, Update update, UserData userData, PageMenu t) {
        pageProcessor.incrementPage(userData.getChatId());
    }

    @Override
    public boolean hasNextPage(UserData userData) {
        return pageProcessor.hasNextPage(userData.getChatId());
    }

    @Override
    public boolean hasPreviousPage(UserData userData) {
        return pageProcessor.hasPreviousPage(userData.getChatId());
    }

    @Override
    public UserMenu retState() {
        return UserMenu.ACCOUNTS;
    }

    @Override
    public String getHeaderText(TelegramClient bot, UserData userData) {
        long chatId = userData.getChatId();
        try {
            // Returning the message
            var dto = pageProcessor.getPage(chatId);
            String accountsStr = dto.getAccountsListStr();
            var opt = registry.getOrCreate(userData, UserTokenDeleteData::new).getOpt();
            return String.format("""
                            %s <b>Теперь отправьте ID аккаунтов для удаления из списка выше</b>
                            <blockquote>• Формат сообщения — <code>%s</code>
                            • Режим: <b>%s</b></blockquote>
                            
                            %s <i><b>Осторожно!</b> Аккаунты удаляются со всеми данными</i>
                            <blockquote expandable>%s</blockquote>
                            """,
                    DynamicEmoji.WRITE.getEmoji(),
                    getRandomNumbersAsString(dto),
                    opt.getDynamicEmoji() + " " + opt.getDescription(),
                    DynamicEmoji.WARNING.getEmoji(),
                    accountsStr
            );
        } catch (RuntimeException e) {
            // Catching the error and sending the user
            log.error(e);
            return pageProcessor.getLastError(chatId);
        }
    }

    public static String getRandomNumbersAsString(YTPAccountsPageProcessorDto dto) {
        var numbers = dto.fcd().getAccounts().stream().map(FcdAccountV2Dto::getId).toList();
        if (numbers.isEmpty()) return "Недостаточно аккаунтов";
        // Shuffle
        int count = Math.min(3, numbers.size());
        List<Long> copy = new ArrayList<>(numbers);
        Collections.shuffle(copy);
        // Returning the sublist
        return copy.subList(0, count)
                .stream()
                .map(String::valueOf)
                .collect(Collectors.joining(" "));
    }
}
