package cs.youtrade.autotrade.client.telegram.menu.start;

import cs.youtrade.autotrade.client.telegram.localization.BotTexts;
import cs.youtrade.autotrade.client.telegram.localization.LocalizedTexts;
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
import java.util.Arrays;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Service
public class UserStartState extends YTPTextMenuState<UserStartMenu> {
    private static final String TELEGRAM_GROUP_LINK = "https://t.me/youtradecs";
    private static final String TELEGRAM_SUPPORT_LINK = "https://t.me/youtradecs_sup";
    private static final String DOCUMENTATION_LINK = "https://docs.youtradecs.xyz";

    private final GeneralEndpoint endpoint;
    private final BotTexts texts;

    public UserStartState(
            UserTextMessageSender sender,
            GeneralEndpoint endpoint,
            BotTexts texts
    ) {
        super(sender, texts);
        this.endpoint = endpoint;
        this.texts = texts;
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
        var text = texts.forUser(user, "start.UserStart");

        if (user.isBlocked()) {
            return String.format("""
                            %s <i>%s</i>

                            %s <b>%s</b>
                            <blockquote>• %s: <b>%s</b>
                            • %s → <tg-spoiler><b>$%.2f</b></tg-spoiler>
                            • %s → <tg-spoiler><b>$%.2f</b></tg-spoiler></blockquote>

                            🛑 <b>%s</b>
                            %s
                            """,
                    DynamicEmoji.YOUTRADE.getEmoji(), text.get("service"),
                    DynamicEmoji.PROFILE.getEmoji(), text.get("profile"),
                    text.get("user-id"), fcd.getTdId(), text.get("balance"), valueOrZero(fcd.getBalance()),
                    text.get("referral-balance"), valueOrZero(fcd.getReferralBalance()),
                    text.format("suspended-until", Map.of("until", formatBlockedUntil(user.getBlockedUntil(), text))),
                    text.format("suspended-support", Map.of("support", String.format("<a href=\"%s\">%s</a>",
                            TELEGRAM_SUPPORT_LINK, text.get("contact-support")))));
        }

        // Отправка заголовка
        return String.format("""
                        %s <i>%s</i>

                        %s <b>%s</b>
                        <blockquote>• %s: <b>%s</b>
                        • %s → <tg-spoiler><b>$%.2f</b></tg-spoiler></blockquote>

                        %s

                        %s
                        """,
                DynamicEmoji.YOUTRADE.getEmoji(), text.get("service"),
                DynamicEmoji.PROFILE.getEmoji(), text.get("profile"),
                text.get("user-id"), fcd.getTdId(),
                text.get("balance"), valueOrZero(fcd.getBalance()),
                buildDepositBonusProgress(fcd.getDepositBonusProgress(), text),
                buildFooter(fcd.getDepositBonusProgress(), text)
        );
    }

    private String buildFooter(DepositBonusProgressDto progress, LocalizedTexts text) {
        if (progress != null && Boolean.TRUE.equals(progress.getLocked()))
            return String.format("<i>%s</i>", text.format("footer.thanks", Map.of("service", "<b>YouTrade.CS</b>")));
        return String.format("<i>%s</i>", text.format("footer.assistant", Map.of("service", "<b>YouTrade.CS</b>")));
    }

    private String buildDepositBonusProgress(DepositBonusProgressDto progress, LocalizedTexts text) {
        if (progress == null)
            return "";

        BigDecimal turnover = valueOrZero(progress.getTurnover());
        BigDecimal currentRate = valueOrZero(progress.getCurrentBonusRate());
        var nextTier = progress.getNextTier();

        if (Boolean.TRUE.equals(progress.getLocked())) {
            return String.format("""
                            %s <b>%s</b>
                            <blockquote>• %s: <b>+%s</b></blockquote>""",
                    DynamicEmoji.GRAPH.getEmoji(), text.get("your-terms"),
                    text.get("personal-top-up-bonus"), formatPercent(currentRate)
            );
        }

        if (nextTier == null) {
            return String.format("""
                            %s <b>%s</b>
                            <blockquote>• %s: <b>%s</b>
                            • %s</blockquote>""",
                    DynamicEmoji.GRAPH.getEmoji(), text.get("your-terms"),
                    text.get("turnover"), formatMoney(turnover),
                    text.format("maximum-bonus-active", Map.of("bonus", String.format("<b>+%s</b>", formatPercent(currentRate))))
            );
        }

        String currentBonus = currentRate.signum() > 0
                ? String.format("• %s: <b>+%s</b>\n", text.get("top-up-bonus"), formatPercent(currentRate))
                : "";
        String remainingTurnover = String.format("<b>%s</b>", text.format("turnover-amount", Map.of(
                "amount", formatWholeMoney(valueOrZero(nextTier.getRemainingTurnover())))));
        String nextBonus = String.format("<b>%s</b>", text.format("top-up-bonus-amount", Map.of(
                "amount", formatPercent(valueOrZero(nextTier.getBonusRate())))));
        return String.format("""
                        %s <b>%s</b>
                        <blockquote>• %s: <b>%s</b>
                        %s└ %s</blockquote>""",
                DynamicEmoji.GRAPH.getEmoji(), text.get("your-terms"),
                text.get("turnover"), formatMoney(turnover),
                currentBonus,
                text.format("next-bonus", Map.of("turnover", remainingTurnover, "bonus", nextBonus))
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

    private String formatBlockedUntil(String value, LocalizedTexts text) {
        try {
            return LocalDateTime.parse(value).format(DateTimeFormatter.ofPattern("dd.MM.yyyy, HH:mm")) + " UTC";
        } catch (RuntimeException e) {
            return text.get("blocked-until-unavailable");
        }
    }

    @Override
    public UserMenu supportedState() {
        return UserMenu.START;
    }

    @Override
    public Map<UserStartMenu, Function<UserData, String>> getTextFunctions(UserData user) {
        return Arrays.stream(UserStartMenu.values()).collect(Collectors.toMap(
                Function.identity(), option -> u -> option.getButtonName(texts.forUser(u,
                        "start.UserStart").get("button." + option.name()))));
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
