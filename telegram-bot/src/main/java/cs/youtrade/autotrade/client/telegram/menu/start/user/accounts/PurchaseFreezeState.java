package cs.youtrade.autotrade.client.telegram.menu.start.user.accounts;

import cs.youtrade.autotrade.client.telegram.menu.UserMenu;
import cs.youtrade.autotrade.client.telegram.menu.start.user.accounts.util.YTPPageProcessor;
import cs.youtrade.autotrade.client.telegram.prototype.data.UserData;
import cs.youtrade.autotrade.client.telegram.prototype.menu.text.base.YTPTextMenuState;
import cs.youtrade.autotrade.client.telegram.prototype.sender.text.UserTextMessageSender;
import cs.youtrade.autotrade.client.util.autotrade.endpoint.user.accounts.AccountsV2Endpoint;
import cs.youtrade.autotrade.client.util.autotrade.util.HistoryDateTimeFormat;
import cs.youtrade.autotrade.client.util.autotrade.util.accounts.FcdAccountV2Dto;
import cs.youtrade.autotrade.client.util.emoji.DynamicEmoji;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.generics.TelegramClient;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.time.format.DateTimeParseException;

@Service
public class PurchaseFreezeState extends YTPTextMenuState<PurchaseFreezeMenu> {
    private final AccountsV2Endpoint endpoint;
    private final YTPPageProcessor pageProcessor;
    private final Map<Long, Data> registry = new ConcurrentHashMap<>();

    public PurchaseFreezeState(UserTextMessageSender sender, AccountsV2Endpoint endpoint,
                               YTPPageProcessor pageProcessor) {
        super(sender);
        this.endpoint = endpoint;
        this.pageProcessor = pageProcessor;
    }

    @Override
    public UserMenu supportedState() { return UserMenu.ACCOUNTS_PURCHASE_FREEZE; }

    @Override
    public PurchaseFreezeMenu getOption(String optionStr) { return PurchaseFreezeMenu.valueOf(optionStr); }

    @Override
    public PurchaseFreezeMenu[] getOptions(UserData user) {
        Data data = data(user);
        if (data.tokenId == null) return new PurchaseFreezeMenu[]{PurchaseFreezeMenu.BACK};
        if (data.account != null && data.account.getBuyBalance() != null
                && Boolean.TRUE.equals(data.account.getBuyBalance().getUserPurchaseFrozen()))
            return PurchaseFreezeMenu.values();
        return new PurchaseFreezeMenu[]{PurchaseFreezeMenu.ONE_DAY, PurchaseFreezeMenu.SEVEN_DAYS,
                PurchaseFreezeMenu.THIRTY_DAYS, PurchaseFreezeMenu.BACK};
    }

    @Override
    public UserMenu executeCallback(TelegramClient bot, Update update, UserData user, PurchaseFreezeMenu option) {
        if (option == PurchaseFreezeMenu.BACK) {
            registry.remove(user.getChatId());
            return UserMenu.ACCOUNTS_MORE;
        }
        Data data = data(user);
        if (data.tokenId == null) return supportedState();
        int days = switch (option) {
            case ONE_DAY -> 1;
            case SEVEN_DAYS -> 7;
            case THIRTY_DAYS -> 30;
            case RESUME -> 0;
            case BACK -> throw new IllegalStateException();
        };
        updateFreeze(user, data, days);
        return supportedState();
    }

    @Override
    public String getHeaderText(TelegramClient bot, UserData user) {
        Data data = data(user);
        String header = DynamicEmoji.YOUTRADE.getEmoji() + " <i>Пауза покупок</i>\n\n";
        if (data.tokenId == null) {
            try {
                return header + DynamicEmoji.WRITE.getEmoji() + " <b>Отправьте ID аккаунта покупки</b>\n"
                        + "<blockquote expandable>" + pageProcessor.getPage(user.getChatId()).getAccountsListStr()
                        + "</blockquote>";
            } catch (RuntimeException error) {
                return header + "Не удалось загрузить аккаунты. Попробуйте снова.";
            }
        }
        refreshAccount(user, data);
        StringBuilder text = new StringBuilder(header + DynamicEmoji.STEAM.getEmoji()
                + " <b>Аккаунт</b>\n<blockquote>• ID: <code>" + data.tokenId + "</code>");
        if (data.account != null && data.account.getGivenName() != null)
            text.append("\n• Имя: <b>").append(escape(data.account.getGivenName())).append("</b>");
        text.append("</blockquote>\n\n")
                .append(DynamicEmoji.ITEM_RECEIVE.getEmoji())
                .append(" <b>Состояние покупок</b>\n<blockquote>");
        if (data.account != null && data.account.getBuyBalance() != null) {
            var balance = data.account.getBuyBalance();
            if (Boolean.TRUE.equals(balance.getUserPurchaseFrozen()))
                text.append("• Ваша пауза: <b>до ")
                        .append(date(balance.getUserPurchaseUnfreezeAt())).append("</b>");
            else text.append("• Ваша пауза: <b>нет</b>");
            if (Boolean.TRUE.equals(balance.getSystemPurchaseFrozen()))
                text.append("\n• Системная пауза: <b>до ")
                        .append(date(balance.getAccountUnfreezeAt())).append("</b>");
            else text.append("\n• Системная пауза: <b>нет</b>");
        } else text.append("• Состояние пока недоступно");
        text.append("</blockquote>\n\n");
        if (data.result != null) text.append(data.result).append("\n\n");
        text.append(DynamicEmoji.WRITE.getEmoji())
                .append(" <b>Выберите срок или отправьте число дней</b>\n")
                .append("└ <i>От 1 до 3650 дней. Новый срок считается с момента выбора.</i>");
        return text.toString();
    }

    @Override
    public UserMenu onNoCallback(TelegramClient bot, Update update, UserData user) {
        if (!update.hasMessage() || !update.getMessage().hasText()) {
            sender.sendTextMes(bot, user, "Отправьте значение текстовым сообщением.");
            return supportedState();
        }
        Data data = data(user);
        long value;
        try {
            value = Long.parseLong(update.getMessage().getText().trim());
        } catch (NumberFormatException error) {
            sender.sendTextMes(bot, user, data.tokenId == null
                    ? "Введите один ID аккаунта числом." : "Введите число дней от 1 до 3650.");
            return supportedState();
        }
        if (data.tokenId == null) {
            try {
                var account = pageProcessor.getPage(user.getChatId()).fcd().getAccounts().stream()
                        .filter(item -> item.getId().equals(value)).findFirst().orElse(null);
                if (account == null || account.getBuyBalance() == null) {
                    sender.sendTextMes(bot, user, "Аккаунт покупки не найден на текущей странице.");
                    return supportedState();
                }
                data.tokenId = value;
                data.account = account;
            } catch (RuntimeException error) {
                sender.sendTextMes(bot, user, "Не удалось проверить аккаунт. Попробуйте снова.");
            }
        } else if (value < 1 || value > 3650) {
            sender.sendTextMes(bot, user, "Введите число дней от 1 до 3650.");
        } else updateFreeze(user, data, (int) value);
        return supportedState();
    }

    private void updateFreeze(UserData user, Data data, int days) {
        try {
            var answer = endpoint.freezeBuyerPurchases(user.getChatId(), data.tokenId, days);
            data.result = switch (answer.getStatus()) {
                case 204 -> days == 0
                        ? DynamicEmoji.SUCCESS.getEmoji() + " <b>Ваша пауза снята</b>"
                        : DynamicEmoji.SUCCESS.getEmoji() + " <b>Покупки приостановлены на "
                                + days + " " + pluralDays(days) + "</b>";
                case 404 -> "Аккаунт покупки не найден или больше вам не принадлежит.";
                case 400 -> "Не удалось установить выбранный срок.";
                default -> "Не удалось изменить состояние покупок. Попробуйте позже.";
            };
            if (answer.getStatus() == 204) {
                refreshAccount(user, data);
                if (days == 0 && data.account != null && data.account.getBuyBalance() != null
                        && Boolean.TRUE.equals(data.account.getBuyBalance().getSystemPurchaseFrozen()))
                    data.result += "\n└ <i>Системная пауза продолжает действовать.</i>";
            }
        } catch (RuntimeException error) {
            data.result = "Не удалось изменить состояние покупок. Попробуйте позже.";
        }
    }

    private void refreshAccount(UserData user, Data data) {
        try {
            data.account = pageProcessor.getPage(user.getChatId()).fcd().getAccounts().stream()
                    .filter(item -> item.getId().equals(data.tokenId)).findFirst().orElse(null);
        } catch (RuntimeException ignored) { }
    }

    private String date(String value) {
        if (value == null) return "неизвестного времени";
        try {
            return HistoryDateTimeFormat.display(value);
        } catch (DateTimeParseException error) {
            return "неизвестного времени";
        }
    }

    private String pluralDays(int days) {
        if (days % 100 >= 11 && days % 100 <= 14) return "дней";
        return switch (days % 10) {
            case 1 -> "день";
            case 2, 3, 4 -> "дня";
            default -> "дней";
        };
    }

    private String escape(String value) {
        return value.replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;")
                .replace("\"", "&quot;");
    }

    private Data data(UserData user) {
        return registry.computeIfAbsent(user.getChatId(), _ -> new Data());
    }

    private static class Data {
        private Long tokenId;
        private FcdAccountV2Dto account;
        private String result;
    }
}
