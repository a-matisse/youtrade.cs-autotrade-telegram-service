package cs.youtrade.autotrade.client.telegram.localization;

import cs.youtrade.autotrade.client.telegram.menu.start.UserStartMenu;
import cs.youtrade.autotrade.client.telegram.menu.start.UserStartState;
import cs.youtrade.autotrade.client.telegram.prototype.data.UserData;
import cs.youtrade.autotrade.client.telegram.prototype.data.UserLanguage;
import cs.youtrade.autotrade.client.telegram.prototype.sender.text.UserTextMessageSender;
import cs.youtrade.autotrade.client.util.autotrade.dto.user.general.DepositBonusProgressDto;
import cs.youtrade.autotrade.client.util.autotrade.dto.user.general.DepositBonusTierDto;
import cs.youtrade.autotrade.client.util.autotrade.dto.user.general.FcdGeneralAccInfoDto;
import cs.youtrade.autotrade.client.util.autotrade.endpoint.user.general.GeneralEndpoint;
import cs.youtrade.autotrade.client.util.emoji.DynamicEmoji;
import cs.youtrade.ytrest.RestAnswer;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

class StartLocalizationTest {
    @Test
    void preservesRussianMainScreenLayoutExactly() {
        var profile = profile(UserLanguage.RU);
        var user = new UserData(1L);
        var state = state(profile);
        String expected = String.format("""
                        %s <i>Сервис YouTrade.CS</i>

                        %s <b>Профиль</b>
                        <blockquote>• ID пользователя: <b>123</b>
                        • Баланс пользователя → <tg-spoiler><b>$%.2f</b></tg-spoiler></blockquote>



                        <i><b>YouTrade.CS</b> — ваш ассистент в мире трейда CS2</i>
                        """, DynamicEmoji.YOUTRADE.getEmoji(), DynamicEmoji.PROFILE.getEmoji(), profile.getBalance());
        assertEquals(expected, state.getHeaderText(null, user));
        assertEquals(UserLanguage.RU, user.effectiveLanguage());
    }

    @Test
    void translatesBonusBranchesAndPreservesAmountsMarkupAndButtons() {
        var profile = profile(UserLanguage.EN);
        var progress = new DepositBonusProgressDto();
        progress.setTurnover(new BigDecimal("987.65"));
        progress.setCurrentBonusRate(new BigDecimal("0.03"));
        var tier = new DepositBonusTierDto();
        tier.setRemainingTurnover(new BigDecimal("1234.01"));
        tier.setBonusRate(new BigDecimal("0.04"));
        progress.setNextTier(tier);
        profile.setDepositBonusProgress(progress);
        var state = state(profile);
        var user = new UserData(1L);
        String header = state.getHeaderText(null, user);
        assertTrue(header.contains("• Top-up bonus: <b>+3%</b>"));
        assertTrue(header.contains("Another <b>$1,235 of trading volume</b> to unlock a bonus of <b>+4% on top-ups</b>"));
        assertFalse(header.matches("(?s).*[А-Яа-яЁё].*"));
        assertEquals("📊 Start trading", state.getTextFunctions(user).get(UserStartMenu.USER).apply(user));
        assertEquals("https://docs.youtradecs.xyz", state.getUrls(user).get(UserStartMenu.DOCS_URL));

        progress.setNextTier(null);
        assertTrue(state.getHeaderText(null, user).contains("Maximum bonus <b>+3%</b> is active"));
        progress.setLocked(true);
        header = state.getHeaderText(null, user);
        assertTrue(header.contains("• Personal top-up bonus: <b>+3%</b>"));
        assertTrue(header.contains("Thank you for choosing <b>YouTrade.CS</b> — we value your trust"));
    }

    @Test
    void translatesBlockedScreenAndKeepsAccessRestrictions() {
        var profile = profile(UserLanguage.EN);
        profile.setBlockedUntil("2100-01-01T00:00:00");
        var state = state(profile);
        var user = new UserData(1L);
        String header = state.getHeaderText(null, user);
        assertTrue(header.contains("Service suspended until 01.01.2100, 00:00 UTC."));
        assertTrue(header.contains("<a href=\"https://t.me/youtradecs_sup\">contact support</a>"));
        assertFalse(header.matches("(?s).*[А-Яа-яЁё].*"));
        assertFalse(state.getVisibilityPredicates(user).get(UserStartMenu.USER).test(user));
        assertEquals("🆘 Support", state.getTextFunctions(user).get(UserStartMenu.SUPPORT_URL).apply(user));
    }

    private UserStartState state(FcdGeneralAccInfoDto profile) {
        var endpoint = new GeneralEndpoint() {
            @Override
            public RestAnswer<FcdGeneralAccInfoDto> viewAccInfo(Long chatId) {
                return new RestAnswer<>(200, profile);
            }
        };
        return new UserStartState(new UserTextMessageSender(null), endpoint, new BotTexts());
    }

    private FcdGeneralAccInfoDto profile(UserLanguage language) {
        var profile = new FcdGeneralAccInfoDto();
        profile.setTdId(123L);
        profile.setQualified(false);
        profile.setBargainAllowed(false);
        profile.setLanguageCode(language.getCode());
        profile.setBalance(new BigDecimal("12.50"));
        return profile;
    }
}
