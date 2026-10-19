package cs.youtrade.autotrade.client.telegram.localization;

import cs.youtrade.autotrade.client.telegram.menu.start.user.accounts.add.stages.worker.WorkerAddRegistry;
import cs.youtrade.autotrade.client.telegram.menu.start.user.accounts.add.stages.worker.stage1.WorkerAddLoginState;
import cs.youtrade.autotrade.client.telegram.menu.start.user.accounts.add.stages.worker.stage2.WorkerAddPasswordState;
import cs.youtrade.autotrade.client.telegram.menu.start.user.accounts.add.stages.worker.stage3.WorkerAddMaFileState;
import cs.youtrade.autotrade.client.telegram.prototype.data.UserData;
import cs.youtrade.autotrade.client.telegram.prototype.data.UserLanguage;
import cs.youtrade.autotrade.client.telegram.prototype.sender.text.UserTextMessageSender;
import cs.youtrade.autotrade.client.util.autotrade.ItemScoringType;
import cs.youtrade.autotrade.client.util.autotrade.ParamsCopyOptions;
import cs.youtrade.autotrade.client.util.autotrade.TdpField;
import cs.youtrade.autotrade.client.util.autotrade.YdpField;
import cs.youtrade.autotrade.client.util.emoji.DynamicEmoji;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class SharedTextLocalizationTest {
    private final BotTexts texts = new BotTexts();

    @Test
    void purchasePauseUsesTheRecipientsPluralRules() {
        var ru = texts.forLanguage(UserLanguage.RU, "accounts.PurchaseFreeze");
        var en = ru.inLanguage(UserLanguage.EN);
        assertEquals("Покупки приостановлены на 21 день", ru.format("purchases-paused-for", Map.of("days", 21)));
        assertEquals("Покупки приостановлены на 22 дня", ru.format("purchases-paused-for", Map.of("days", 22)));
        assertEquals("Покупки приостановлены на 12 дней", ru.format("purchases-paused-for", Map.of("days", 12)));
        assertEquals("Purchases paused for 1 day", en.format("purchases-paused-for", Map.of("days", 1)));
        assertEquals("Purchases paused for 21 days", en.format("purchases-paused-for", Map.of("days", 21)));
    }

    @Test
    void parameterDescriptionsAndInputHintsHaveBothTranslations() {
        for (var language : UserLanguage.values()) {
            var trading = texts.forLanguage(language, "parameters.TdpField");
            for (var field : TdpField.values()) {
                assertFalse(trading.enumText(field, "desc").isBlank());
                assertTrue(field.getForkByField(trading).contains(field.getFName()));
            }
            var scoring = trading.inBundle("parameters.scoring.YdpField");
            for (var field : YdpField.values()) {
                assertFalse(scoring.enumText(field, "desc").isBlank());
                assertTrue(field.getForkByField(scoring).contains(field.getFName()));
            }
            String description = String.join("\n",
                    TdpField.generateDescription(TdpField.DirType.BUY, trading),
                    TdpField.generateDescription(TdpField.DirType.SELL, trading),
                    YdpField.generateDescription(scoring),
                    ParamsCopyOptions.generateDescription(trading.inBundle("parameters.follow.ParamsCopyOptions")),
                    ItemScoringType.generateDescription(trading.inBundle("parameters.TradingOptions")));
            assertTrue(description.contains("<code>"));
            if (language == UserLanguage.EN) assertFalse(description.matches("(?s).*[А-Яа-яЁё].*"));
        }
    }

    @Test
    void workerPromptsKeepTheirLinksAndEmojiInBothLanguages() {
        var sender = new UserTextMessageSender(null);
        var registry = new WorkerAddRegistry();
        var login = new WorkerAddLoginState(sender, registry, texts);
        var password = new WorkerAddPasswordState(sender, registry, texts);
        var maFile = new WorkerAddMaFileState(sender, registry, texts);
        var user = new UserData(1L);
        for (var language : UserLanguage.values()) {
            user.setLanguage(language);
            String loginText = login.getHeaderText(null, user);
            String passwordText = password.getHeaderText(null, user);
            String maFileText = maFile.getHeaderText(null, user);
            String accountLink = "href=\"https://store.steampowered.com/account/authorizeddevices\"";
            assertTrue(loginText.contains(accountLink));
            assertTrue(passwordText.contains(accountLink));
            assertTrue(maFileText.contains("href=\"https://youtu.be/29jLB9GmKE4?si=foaU45ol-Pw8_jrR\""));
            for (var message : new String[]{loginText, passwordText, maFileText}) {
                assertTrue(message.contains(DynamicEmoji.STEAM.getEmoji()));
                assertTrue(message.contains("<b>"));
                assertFalse(message.contains("{value"));
                assertFalse(message.codePoints().anyMatch(c -> c < 32 && c != '\n' && c != '\r' && c != '\t'));
                if (language == UserLanguage.EN) assertFalse(message.matches("(?s).*[А-Яа-яЁё].*"));
            }
        }
    }
}
