package cs.youtrade.autotrade.client.telegram.menu.start;

import cs.youtrade.autotrade.client.telegram.menu.UserMenu;
import cs.youtrade.autotrade.client.telegram.prototype.data.UserData;
import cs.youtrade.autotrade.client.telegram.prototype.menu.img.YTPImageMenuState;
import cs.youtrade.autotrade.client.telegram.prototype.menu.text.base.YTPTextMenuState;
import cs.youtrade.autotrade.client.telegram.prototype.sender.image.UserImageMessageSender;
import cs.youtrade.autotrade.client.telegram.prototype.sender.text.UserTextMessageSender;
import cs.youtrade.autotrade.client.util.autotrade.dto.user.general.DepositBonusProgressDto;
import cs.youtrade.autotrade.client.util.autotrade.endpoint.user.general.GeneralEndpoint;
import cs.youtrade.autotrade.client.util.emoji.DynamicEmoji;
import cs.youtrade.telegram.buttons.menu.InlineKeyboardButtonStyle;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.generics.TelegramClient;

import java.util.Map;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Locale;
import java.util.function.Function;
import java.util.function.Predicate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Service
public class UserStartState extends YTPTextMenuState<UserStartMenu> {
    private static final String TELEGRAM_GROUP_LINK = "https://t.me/youtradecs";
    private static final String TELEGRAM_SUPPORT_LINK = "https://t.me/youtradecs_sup";
    private static final String DOCUMENTATION_LINK = "https://docs.youtradecs.xyz";

    private final GeneralEndpoint endpoint;
    public UserStartState(
            UserTextMessageSender sender,
            GeneralEndpoint endpoint
    ) {
        super(sender);
        this.endpoint = endpoint;
    }

    @Override
    public UserStartMenu getOption(String optionStr) {
        return UserStartMenu.valueOf(optionStr);
    }

    @Override
    public UserStartMenu[] getOptions(UserData userData) {
        return UserStartMenu.values();
    }

    @Override
    public UserMenu executeCallback(TelegramClient bot, Update update, UserData userData, UserStartMenu t) {
        return switch (t) {
            case USER -> UserMenu.USER;
            case REF -> UserMenu.REF;
            case TOP_UP -> UserMenu.TOP_UP_STAGE_1;
            case GET_PRICE -> UserMenu.GET_PRICE;
            case GROUP_URL, DOCS_URL, SUPPORT_URL -> UserMenu.START;
        };
    }

    @Override
    public String getHeaderText(TelegramClient bot, UserData user) {
        // Приветствие
        var restAns = endpoint.viewAccInfo(user.getChatId());
        if (restAns.getStatus() >= 300)
            return null;

        var fcd = restAns.getResponse();
        if (!fcd.isResult())
            return null;

        // Обновление команд пользователя
        user.updateQualified(fcd);

        if (user.isBlocked()) {
            return String.format("""
                            %s <i>Сервис YouTrade.CS</i>

                            %s <b>Профиль</b>
                            <blockquote>• ID пользователя: <b>%s</b>
                            • Баланс пользователя → <tg-spoiler><b>$%.2f</b></tg-spoiler>
                            • Реферальный баланс → <tg-spoiler><b>$%.2f</b></tg-spoiler></blockquote>

                            🛑 <b>Обслуживание приостановлено до %s.</b>
                            Кабинет и баланс доступны для просмотра. По вопросам ограничения и средств <a href="%s">напишите в поддержку</a>.
                            """,
                    DynamicEmoji.YOUTRADE.getEmoji(), DynamicEmoji.PROFILE.getEmoji(),
                    fcd.getTdId(), valueOrZero(fcd.getBalance()), valueOrZero(fcd.getReferralBalance()),
                    formatBlockedUntil(user.getBlockedUntil()), TELEGRAM_SUPPORT_LINK);
        }

        // Отправка заголовка
        return String.format("""
                        %s <i>Сервис YouTrade.CS</i>
                        
                        %s <b>Профиль</b>
                        <blockquote>• ID пользователя: <b>%s</b>
                        • Баланс пользователя → <tg-spoiler><b>$%.2f</b></tg-spoiler></blockquote>

                        %s
                        
                        %s
                        """,
                DynamicEmoji.YOUTRADE.getEmoji(),
                DynamicEmoji.PROFILE.getEmoji(),
                fcd.getTdId(),
                valueOrZero(fcd.getBalance()),
                buildDepositBonusProgress(fcd.getDepositBonusProgress()),
                buildFooter(fcd.getDepositBonusProgress())
        );
    }

    private String buildFooter(DepositBonusProgressDto progress) {
        if (progress != null && Boolean.TRUE.equals(progress.getLocked()))
            return "<i>Спасибо, что вы с <b>YouTrade.CS</b> — ценим ваше доверие</i>";
        return "<i><b>YouTrade.CS</b> — ваш ассистент в мире трейда CS2</i>";
    }

    private String buildDepositBonusProgress(DepositBonusProgressDto progress) {
        if (progress == null)
            return "";

        BigDecimal turnover = valueOrZero(progress.getTurnover());
        BigDecimal currentRate = valueOrZero(progress.getCurrentBonusRate());
        var nextTier = progress.getNextTier();

        if (Boolean.TRUE.equals(progress.getLocked())) {
            return String.format("""
                            %s <b>Ваши условия</b>
                            <blockquote>• Персональный бонус к пополнению: <b>+%s</b></blockquote>""",
                    DynamicEmoji.GRAPH.getEmoji(),
                    formatPercent(currentRate)
            );
        }

        if (nextTier == null) {
            return String.format("""
                            %s <b>Ваши условия</b>
                            <blockquote>• Оборот: <b>%s</b>
                            • Максимальный бонус <b>+%s</b> активен</blockquote>""",
                    DynamicEmoji.GRAPH.getEmoji(),
                    formatMoney(turnover),
                    formatPercent(currentRate)
            );
        }

        String currentBonus = currentRate.signum() > 0
                ? String.format("• Бонус к пополнению: <b>+%s</b>\n", formatPercent(currentRate))
                : "";
        return String.format("""
                        %s <b>Ваши условия</b>
                        <blockquote>• Оборот: <b>%s</b>
                        %s└ Ещё <b>%s оборота</b> до бонуса <b>+%s к пополнению</b></blockquote>""",
                DynamicEmoji.GRAPH.getEmoji(),
                formatMoney(turnover),
                currentBonus,
                formatWholeMoney(valueOrZero(nextTier.getRemainingTurnover())),
                formatPercent(valueOrZero(nextTier.getBonusRate()))
        );
    }

    private BigDecimal valueOrZero(BigDecimal value) {
        return value == null ? BigDecimal.ZERO : value;
    }

    private String formatMoney(BigDecimal value) {
        return String.format(Locale.US, "$%,.2f", value);
    }

    private String formatWholeMoney(BigDecimal value) {
        return String.format(Locale.US, "$%,d", value.setScale(0, RoundingMode.CEILING).longValueExact());
    }

    private String formatPercent(BigDecimal rate) {
        return rate.multiply(BigDecimal.valueOf(100))
                .setScale(0, RoundingMode.HALF_UP)
                .toPlainString() + "%";
    }

    private String formatBlockedUntil(String value) {
        try {
            return LocalDateTime.parse(value).format(DateTimeFormatter.ofPattern("dd.MM.yyyy, HH:mm")) + " UTC";
        } catch (RuntimeException e) {
            return "уточнения срока в поддержке";
        }
    }

    @Override
    public UserMenu supportedState() {
        return UserMenu.START;
    }

    @Override
    public Map<UserStartMenu, String> getUrls(UserData user) {
        return Map.of(
                UserStartMenu.GROUP_URL, TELEGRAM_GROUP_LINK,
                UserStartMenu.DOCS_URL, DOCUMENTATION_LINK,
                UserStartMenu.SUPPORT_URL, TELEGRAM_SUPPORT_LINK
        );
    }

    @Override
    public Map<UserStartMenu, Predicate<UserData>> getVisibilityPredicates(UserData user) {
        return Map.of(
                UserStartMenu.USER, u -> !u.isBlocked(),
                UserStartMenu.REF, u -> !u.isBlocked(),
                UserStartMenu.TOP_UP, u -> !u.isBlocked(),
                UserStartMenu.GET_PRICE, u -> !u.isBlocked()
        );
    }

    @Override
    public Map<UserStartMenu, Function<UserData, InlineKeyboardButtonStyle>> getButtonStyle(UserData userData) {
        return Map.of(UserStartMenu.USER, e -> InlineKeyboardButtonStyle.PRIMARY);
    }
}
