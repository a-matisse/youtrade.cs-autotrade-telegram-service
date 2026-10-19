package cs.youtrade.autotrade.client.telegram.menu.start.user.accounts;

import cs.youtrade.autotrade.client.telegram.localization.BotTexts;
import cs.youtrade.autotrade.client.telegram.localization.LocalizedTexts;
import cs.youtrade.autotrade.client.telegram.menu.UserMenu;
import cs.youtrade.autotrade.client.telegram.prototype.data.UserData;
import cs.youtrade.autotrade.client.telegram.prototype.sender.text.UserTextMessageSender;
import cs.youtrade.autotrade.client.util.autotrade.MarketType;
import cs.youtrade.autotrade.client.util.autotrade.dto.user.accounts.AccountExportResponseDto;
import cs.youtrade.autotrade.client.util.emoji.DynamicEmoji;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.generics.TelegramClient;

import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
public class AccountInfoResultState extends AbstractAccountInfoState {
    public AccountInfoResultState(UserTextMessageSender sender, AccountInfoRegistry registry, BotTexts botTexts) {
        super(sender, registry, botTexts);
    }

    @Override
    public UserMenu supportedState() { return UserMenu.ACCOUNTS_INFO_RESULT; }

    @Override
    public String getHeaderText(TelegramClient bot, UserData user) {
        var translations = botTexts.forUser(user, "accounts.AccountInfo");
        var data = registry.getOrCreate(user, AccountInfoData::new);
        if (data.getResult() == null)
            return translations.get("could-not-retrieve-the-account-details-request");
        String result = data.getResult() == AccountInfoData.Result.SENT
                ? String.format("""
                        %s <b>%s</b>
                        """,
                        DynamicEmoji.SUCCESS.getEmoji(),
                        translations.get("details-sent-in-a-separate-message"))
                : String.format("""
                        %s <b>%s</b>
                        """,
                        DynamicEmoji.WARNING.getEmoji(),
                        translations.get("too-many-attempts-try-again-in-15"));
        return String.format("""
                %s%s
                """, summary(user), result);
    }

    public void sendDetails(TelegramClient bot, UserData user, AccountExportResponseDto response) {
        var translations = botTexts.forUser(user, "accounts.AccountInfo");
        sender.sendTextMes(bot, user, format(response, translations));
    }

    private String format(AccountExportResponseDto response, LocalizedTexts translations) {
        var account = response.getAccount();
        String accountDetails = lines(
                "• ID: <code>%s</code>".formatted(account.getSteamTokenId()),
                field(translations.get("name"), account.getGivenName()),
                field("Steam ID", account.getSteamId64()),
                field(translations.get("parameters"), account.getParametersName()),
                account.getSource() != null && account.getDestination() != null
                        ? String.format("• %s: <b>%s → %s</b>",
                                translations.get("route"),
                                escape(market(account.getSource())),
                                escape(market(account.getDestination())))
                        : "");
        String keys = lines(
                secret(translations.get("y-cs-key"), response.getUserApiKey()),
                secret(translations.get("source-api-key"), account.getSourceApiKey()),
                secret(translations.get("buying-api-key"), account.getBuyApiKey()),
                secret(translations.get("selling-api-key"), account.getSellApiKey()),
                secret("Steam Partner", account.getSteamPartner()),
                secret("Steam Trade Token", account.getSteamTradeToken()));
        String steam = lines(
                secret(translations.get("steam-login"), account.getSteamLogin()),
                secret(translations.get("steam-password"), account.getSteamPassword()),
                secret("Guard", account.getGuardCode()),
                steamStatus(account.getCredentialsStatus(), translations));
        String reason = account.getCredentialsError() == null ? ""
                : String.format(" %s: %s",
                        translations.get("reason"),
                        escape(account.getCredentialsError()));
        String notice = response.isComplete() ? "" : String.format("""

                └ <i>%s</i>
                """,
                translations.format("some-steam-details-are-unavailable", Map.of("argument1", String.valueOf(reason)))).stripTrailing();

        return String.format("""
                        %s <i>%s</i>

                        %s <b>%s</b>
                        <blockquote>%s</blockquote>

                        %s <b>%s</b>
                        <blockquote expandable>%s</blockquote>

                        %s <b>Steam</b>
                        <blockquote>%s</blockquote>%s
                        """,
                        DynamicEmoji.YOUTRADE.getEmoji(),
                        translations.get("account-details"),
                        DynamicEmoji.STEAM.getEmoji(),
                        translations.get("account"),
                        accountDetails,
                        DynamicEmoji.KEY.getEmoji(),
                        translations.get("keys-and-trade-details"),
                        keys,
                        DynamicEmoji.SECURE.getEmoji(),
                        steam,
                        notice);
    }

    private static String market(String value) {
        try {
            return MarketType.valueOf(value).getMarketName();
        } catch (IllegalArgumentException error) {
            return value.replace('_', ' ');
        }
    }

    private static String lines(String... values) {
        return Stream.of(values)
                .filter(value -> !value.isBlank())
                .collect(Collectors.joining("\n"));
    }

    private static String field(String label, String value) {
        return value == null || value.isBlank()
                ? ""
                : "• %s: <code>%s</code>".formatted(label, escape(value));
    }

    private static String secret(String label, String value) {
        return value == null || value.isBlank()
                ? ""
                : "• %s: <tg-spoiler><code>%s</code></tg-spoiler>".formatted(label, escape(value));
    }

    private static String steamStatus(String status, LocalizedTexts translations) {
        if ("NOT_CONNECTED".equals(status))
            return String.format("• %s",
                    translations.get("y-cs-worker-is-not-connected"));
        if ("UNAVAILABLE".equals(status))
            return String.format("• %s",
                    translations.get("steam-details-are-temporarily-unavailable"));
        return "";
    }

    private static String escape(String value) {
        return value
                .replace("&", "&amp;")
                .replace("<", "&lt;")
                .replace(">", "&gt;")
                .replace("\"", "&quot;");
    }
}
