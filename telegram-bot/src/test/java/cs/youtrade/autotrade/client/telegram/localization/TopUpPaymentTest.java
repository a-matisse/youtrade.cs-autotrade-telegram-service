package cs.youtrade.autotrade.client.telegram.localization;

import cs.youtrade.autotrade.client.telegram.menu.start.topup.UserPayData;
import cs.youtrade.autotrade.client.telegram.menu.start.topup.UserPayRegistry;
import cs.youtrade.autotrade.client.telegram.menu.start.topup.stagep.UserPayProceedMenu;
import cs.youtrade.autotrade.client.telegram.menu.start.topup.stagep.UserPayProceedState;
import cs.youtrade.autotrade.client.telegram.prototype.data.UserData;
import cs.youtrade.autotrade.client.telegram.prototype.data.UserLanguage;
import cs.youtrade.autotrade.client.telegram.prototype.sender.text.UserTextMessageSender;
import cs.youtrade.autotrade.client.util.autotrade.TopUpType;
import cs.youtrade.autotrade.client.util.autotrade.dto.norole.FcdTopUpDto;
import cs.youtrade.autotrade.client.util.autotrade.endpoint.norole.SubGetEndpoint;
import cs.youtrade.ytrest.RestAnswer;
import cs.youtrade.ytrest.gson.GsonConfig;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

import static org.junit.jupiter.api.Assertions.*;

class TopUpPaymentTest {
    @ParameterizedTest
    @EnumSource(value = TopUpType.class, names = {"HELEKET", "TRYBIT"})
    void recognisesBackendProviderAndDisplaysTheInvoiceInBothLanguages(TopUpType provider) {
        var invoice = GsonConfig.createGson().fromJson("""
                {"result":true,"userTdId":1,"type":"%s","usdAmount":25,"rubAmount":2000,
                 "idempotencyKey":"pending-key","url":"https://payment.example/invoice"}
                """.formatted(provider), FcdTopUpDto.class);
        assertEquals(provider, invoice.getType());
        var endpoint = new SubGetEndpoint() {
            @Override
            public RestAnswer<FcdTopUpDto> topUp(Long chatId, Double amount) {
                return new RestAnswer<>(200, invoice);
            }
        };
        var registry = new UserPayRegistry();
        var state = new UserPayProceedState(new UserTextMessageSender(null), registry, endpoint,
                new BotTexts(), null, null);
        for (var language : new UserLanguage[]{UserLanguage.RU, UserLanguage.EN}) {
            var user = new UserData(1L);
            user.setLanguage(language);
            registry.getOrCreate(user, UserPayData::new).setAmount(25d);
            String header = state.getHeaderText(null, user);
            assertTrue(header.contains("<b>" + provider.getDisplayName() + "</b>"));
            assertFalse(header.contains("HeleketPay"));
            assertFalse(header.contains("60"));
            assertEquals(invoice.getUrl(), state.getUrls(user).get(UserPayProceedMenu.PAY));
        }
    }
}
