package cs.youtrade.autotrade.client.telegram.menu.start.ref;

import cs.youtrade.autotrade.client.telegram.menu.UserMenu;
import cs.youtrade.autotrade.client.telegram.prototype.data.UserData;
import cs.youtrade.autotrade.client.telegram.prototype.menu.text.base.YTPTextMenuState;
import cs.youtrade.autotrade.client.telegram.prototype.sender.text.UserTextMessageSender;
import cs.youtrade.autotrade.client.util.autotrade.dto.user.ref.FcdRefDto;
import cs.youtrade.autotrade.client.util.autotrade.endpoint.user.ref.RefEndpoint;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.generics.TelegramClient;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Locale;

@Service
public class UserRefState extends YTPTextMenuState<UserRefMenu> {
    private static final BigDecimal ONE_HUNDRED = new BigDecimal(100);

    private final RefEndpoint endpoint;

    public UserRefState(
            UserTextMessageSender sender,
            RefEndpoint endpoint
    ) {
        super(sender);
        this.endpoint = endpoint;
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
    public UserMenu executeCallback(TelegramClient bot, Update update, UserData userData, UserRefMenu t) {
        return switch (t) {
            case REF_CONNECT -> UserMenu.REF_CONNECT_STAGE_1;
            case REF_CREATE -> UserMenu.REF_CREATE;
            case RETURN -> UserMenu.START;
        };
    }

    @Override
    public String getHeaderText(TelegramClient bot, UserData user) {
        var ans = endpoint.refGet(user.getChatId());
        if (ans.getStatus() >= 300) return null;

        var fcd = ans.getResponse();
        if (!fcd.isResult()) return fcd.getCause();

        var data = fcd.getData();
        return String.format("""
                        📊 <b>Реферальная система</b>
                        ━━━━━━━━━━━━━
                        
                        %s
                        %s
                        %s
                        """,
                buildStatsBlock(data),
                buildYourCodeBlock(data),
                buildConnectedBlock(data)
        );
    }

    private String buildStatsBlock(FcdRefDto d) {
        return String.format("""
                        💼 <b>Ваши показатели</b>
                        <blockquote>• Оборот: <b>%s</b>
                        • Бонус к пополнению: <b>%s</b></blockquote>
                        """,
                safeMoney(d.getTurnover()),
                safeDiscount(d.getDiscount())
        );
    }

    private String buildYourCodeBlock(FcdRefDto d) {
        if (isBlank(d.getThisRef()))
            return "🔴 <b>Реферальный код не создан</b>\n";

        return String.format("""
                        🔑 <b>Ваша ссылка</b> <code>%s</code>
                        <blockquote>• Процент с рефералов: <b>%s</b>
                        • Бонус по коду: <b>%s</b></blockquote>
                        """,
                escapeHtml(d.getThisRef()),
                formatPercent(d.getRefRate()),
                safeMoney(d.getRefReward())
        );
    }

    private String buildConnectedBlock(FcdRefDto d) {
        if (isBlank(d.getUsedRef()))
            return "🔴 <b>Код не подключен</b>";
        return String.format("🔗 Код подключен: <tg-spoiler>%s</tg-spoiler>", escapeHtml(d.getUsedRef()));
    }

    /* ---------- вспомогательные форматтеры ---------- */

    private static boolean isBlank(String s) {
        return s == null || s.isBlank();
    }

    private String safeMoney(BigDecimal value) {
        if (value == null)
            return "$0.00";
        return String.format(Locale.US, "$%,.2f", value.doubleValue());
    }

    private String safeDiscount(BigDecimal value) {
        if (value == null)
            return "0.00%";
        return String.format(Locale.US, "%,.2f%%", value.doubleValue() * 100d);
    }

    private String formatPercent(BigDecimal rate) {
        if (rate == null)
            return "0%";
        BigDecimal pct = rate
                .multiply(BigDecimal.valueOf(100))
                .setScale(0, RoundingMode.HALF_UP);
        return pct.toPlainString() + "%";
    }

    // Если строки могут содержать спецсимволы — экранируем для HTML (минимально)
    private String escapeHtml(String s) {
        if (s == null)
            return "";
        return s.replace("&", "&amp;")
                .replace("<", "&lt;")
                .replace(">", "&gt;");
    }
}
