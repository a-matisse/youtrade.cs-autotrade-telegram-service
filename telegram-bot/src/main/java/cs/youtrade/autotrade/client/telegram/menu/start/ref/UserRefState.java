package cs.youtrade.autotrade.client.telegram.menu.start.ref;

import cs.youtrade.autotrade.client.telegram.menu.UserMenu;
import cs.youtrade.autotrade.client.telegram.menu.start.ref.transfer.RefTransferRegistry;
import cs.youtrade.autotrade.client.telegram.prototype.data.UserData;
import cs.youtrade.autotrade.client.telegram.prototype.menu.text.base.YTPTextMenuState;
import cs.youtrade.autotrade.client.telegram.prototype.sender.text.UserTextMessageSender;
import cs.youtrade.autotrade.client.util.autotrade.dto.user.ref.FcdRefDto;
import cs.youtrade.autotrade.client.util.autotrade.dto.user.ref.FcdReferralBalanceDto;
import cs.youtrade.autotrade.client.util.autotrade.endpoint.user.ref.RefEndpoint;
import cs.youtrade.autotrade.client.util.emoji.DynamicEmoji;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.GetMe;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.meta.generics.TelegramClient;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Predicate;

@Service
public class UserRefState extends YTPTextMenuState<UserRefMenu> {
    private final RefEndpoint endpoint;
    private final RefTransferRegistry transferRegistry;
    private final Map<Long, FcdRefDto> referralData = new ConcurrentHashMap<>();
    private final Map<Long, FcdReferralBalanceDto> balanceData = new ConcurrentHashMap<>();
    private final Map<Long, String> inviteUrls = new ConcurrentHashMap<>();
    private volatile String botUsername;

    public UserRefState(UserTextMessageSender sender, RefEndpoint endpoint, RefTransferRegistry transferRegistry) {
        super(sender);
        this.endpoint = endpoint;
        this.transferRegistry = transferRegistry;
    }

    @Override
    public UserMenu supportedState() {
        return UserMenu.REF;
    }

    @Override
    public UserRefMenu getOption(String optionStr) {
        return UserRefMenu.valueOf(optionStr);
    }

    @Override
    public UserRefMenu[] getOptions(UserData userData) {
        return UserRefMenu.values();
    }

    @Override
    public UserMenu executeCallback(TelegramClient bot, Update update, UserData user, UserRefMenu option) {
        return switch (option) {
            case REF_CONNECT -> UserMenu.REF_CONNECT_STAGE_1;
            case REF_CREATE -> UserMenu.REF_CREATE;
            case REF_INVITE -> UserMenu.REF;
            case REF_TRANSFER -> transferRegistry.get(user).isPresent()
                    ? UserMenu.REF_TRANSFER_CONFIRM
                    : UserMenu.REF_TRANSFER_AMOUNT;
            case REF_PAYOUT -> UserMenu.REF_PAYOUT;
            case RETURN -> UserMenu.START;
        };
    }

    @Override
    public String getHeaderText(TelegramClient bot, UserData user) {
        var refAnswer = endpoint.refGet(user.getChatId());
        var balanceAnswer = endpoint.getBalance(user.getChatId());
        if (refAnswer.getStatus() >= 300 || balanceAnswer.getStatus() >= 300)
            return null;

        var refResponse = refAnswer.getResponse();
        FcdReferralBalanceDto balances = balanceAnswer.getResponse();
        if (!refResponse.isResult() || balances == null)
            return refResponse.isResult() ? null : refResponse.getCause();

        FcdRefDto ref = refResponse.getData();
        referralData.put(user.getChatId(), ref);
        balanceData.put(user.getChatId(), balances);

        String inviteLink = buildInviteLink(bot, ref.getThisRef());
        if (inviteLink != null)
            inviteUrls.put(user.getChatId(), buildShareUrl(inviteLink));
        else
            inviteUrls.remove(user.getChatId());

        return String.format("""
                        %s <i>Реферальная система</i>

                        %s

                        %s

                        %s
                        """,
                DynamicEmoji.YOUTRADE.getEmoji(),
                buildRewardBlock(balances, ref),
                buildInviteBlock(ref, inviteLink),
                buildConnectedBlock(ref)
        );
    }

    private String buildRewardBlock(FcdReferralBalanceDto balances, FcdRefDto ref) {
        BigDecimal total = balances.getTotalReferralEarnings() != null
                ? balances.getTotalReferralEarnings()
                : ref.getTotalReferralEarnings();
        return String.format("""
                        %s <b>Вознаграждение</b>
                        <blockquote>• Доступно: <b>%s</b>
                        • Заработано за всё время: <b>%s</b></blockquote>""",
                DynamicEmoji.MONEY.getEmoji(),
                safeMoney(balances.getReferralBalance()),
                safeMoney(total)
        );
    }

    private String buildInviteBlock(FcdRefDto data, String inviteLink) {
        if (isBlank(data.getThisRef()))
            return String.format("%s <b>Приглашение ещё не создано</b>", DynamicEmoji.OFF.getEmoji());

        String linkLine = inviteLink == null
                ? ""
                : String.format("\n\n<b>Ваша ссылка:</b>\n<code>%s</code>", escapeHtml(inviteLink));
        return String.format("""
                        %s <b>Приглашение</b>
                        <blockquote>• Ваш код: <code>%s</code>
                        • Процент с рефералов: <b>%s</b>
                        • Бонус другу: <b>%s</b></blockquote>%s""",
                DynamicEmoji.LINK.getEmoji(),
                escapeHtml(data.getThisRef()),
                formatPercent(data.getRefRate()),
                safeMoney(data.getRefReward()),
                linkLine
        );
    }

    private String buildConnectedBlock(FcdRefDto data) {
        if (isBlank(data.getUsedRef()))
            return String.format("%s <b>Код друга не подключён</b>", DynamicEmoji.OFF.getEmoji());
        return String.format("%s Код друга подключён: <tg-spoiler>%s</tg-spoiler>",
                DynamicEmoji.CONNECT.getEmoji(), escapeHtml(data.getUsedRef()));
    }

    private String buildInviteLink(TelegramClient bot, String code) {
        if (isBlank(code))
            return null;
        String username = getBotUsername(bot);
        return isBlank(username) ? null : "https://t.me/" + username + "?start=promo_" + code;
    }

    private String getBotUsername(TelegramClient bot) {
        if (!isBlank(botUsername))
            return botUsername;
        try {
            botUsername = bot.execute(GetMe.builder().build()).getUserName();
            return botUsername;
        } catch (TelegramApiException e) {
            return null;
        }
    }

    private String buildShareUrl(String inviteLink) {
        return "https://t.me/share/url?url="
                + URLEncoder.encode(inviteLink, StandardCharsets.UTF_8)
                + "&text="
                + URLEncoder.encode("Присоединяйся к YouTrade.CS по моей ссылке", StandardCharsets.UTF_8);
    }

    @Override
    public Map<UserRefMenu, String> getUrls(UserData user) {
        String inviteUrl = inviteUrls.get(user.getChatId());
        return inviteUrl == null ? Map.of() : Map.of(UserRefMenu.REF_INVITE, inviteUrl);
    }

    @Override
    public Map<UserRefMenu, Predicate<UserData>> getVisibilityPredicates(UserData userData) {
        return Map.of(
                UserRefMenu.REF_CREATE, user -> !hasOwnCode(user),
                UserRefMenu.REF_INVITE, this::hasInviteUrl,
                UserRefMenu.REF_CONNECT, user -> !hasConnectedCode(user),
                UserRefMenu.REF_TRANSFER, this::hasReferralBalance,
                UserRefMenu.REF_PAYOUT, this::hasReferralBalance
        );
    }

    private boolean hasOwnCode(UserData user) {
        FcdRefDto data = referralData.get(user.getChatId());
        return data != null && !isBlank(data.getThisRef());
    }

    private boolean hasInviteUrl(UserData user) {
        return inviteUrls.containsKey(user.getChatId());
    }

    private boolean hasConnectedCode(UserData user) {
        FcdRefDto data = referralData.get(user.getChatId());
        return data != null && !isBlank(data.getUsedRef());
    }

    private boolean hasReferralBalance(UserData user) {
        FcdReferralBalanceDto data = balanceData.get(user.getChatId());
        return data != null && valueOrZero(data.getReferralBalance()).signum() > 0;
    }

    private boolean isBlank(String value) {
        return value == null || value.isBlank();
    }

    private BigDecimal valueOrZero(BigDecimal value) {
        return value == null ? BigDecimal.ZERO : value;
    }

    private String safeMoney(BigDecimal value) {
        return String.format(Locale.US, "$%,.2f", valueOrZero(value));
    }

    private String formatPercent(BigDecimal rate) {
        return valueOrZero(rate).multiply(BigDecimal.valueOf(100))
                .setScale(0, RoundingMode.HALF_UP).toPlainString() + "%";
    }

    private String escapeHtml(String value) {
        if (value == null)
            return "";
        return value.replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;");
    }
}
