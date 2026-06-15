package cs.youtrade.autotrade.client.telegram.menu.start.user.accounts.util;

import cs.youtrade.autotrade.client.telegram.menu.UserMenu;
import cs.youtrade.autotrade.client.telegram.prototype.data.UserData;
import cs.youtrade.autotrade.client.telegram.prototype.menu.PageMenu;
import cs.youtrade.autotrade.client.telegram.prototype.menu.text.YTPPageTextMenuState;
import cs.youtrade.autotrade.client.telegram.prototype.sender.text.UserTextMessageSender;
import cs.youtrade.autotrade.client.util.autotrade.util.accounts.FcdAccountV2Dto;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.generics.TelegramClient;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public abstract class YTPAccountPageTextMenuState extends YTPPageTextMenuState {
    protected final YTPPageProcessor pageProcessor;

    public YTPAccountPageTextMenuState(
            UserTextMessageSender sender,
            YTPPageProcessor pageProcessor
    ) {
        super(sender);
        this.pageProcessor = pageProcessor;
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

    public static String getRandomNumbersAsString(YTPAccountsPageProcessorDto dto, int num) {
        var numbers = dto.fcd().getAccounts().stream().map(FcdAccountV2Dto::getId).toList();
        if (numbers.isEmpty()) return "Недостаточно аккаунтов";
        // Shuffle
        int count = Math.min(num, numbers.size());
        List<Long> copy = new ArrayList<>(numbers);
        Collections.shuffle(copy);
        // Returning the sublist
        return copy.subList(0, count)
                .stream()
                .map(String::valueOf)
                .collect(Collectors.joining(" "));
    }
}
