package cs.youtrade.autotrade.client.telegram.menu.start.getprice;

import cs.youtrade.autotrade.client.telegram.localization.BotTexts;
import cs.youtrade.autotrade.client.telegram.localization.LocalizedTexts;
import cs.youtrade.autotrade.client.telegram.menu.UserMenu;
import cs.youtrade.autotrade.client.telegram.prototype.data.UserData;
import cs.youtrade.autotrade.client.telegram.prototype.menu.text.YTPTerminalTextMenuState;
import cs.youtrade.autotrade.client.telegram.prototype.sender.text.UserTextMessageSender;
import cs.youtrade.autotrade.client.util.autotrade.MarketType;
import cs.youtrade.autotrade.client.util.autotrade.dto.norole.FcdGetPricesDto;
import cs.youtrade.autotrade.client.util.autotrade.endpoint.norole.SubGetEndpoint;
import cs.youtrade.autotrade.client.util.emoji.DynamicEmoji;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.generics.TelegramClient;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class UserGetPriceState extends YTPTerminalTextMenuState {
    private final SubGetEndpoint endpoint;

    public UserGetPriceState(
            UserTextMessageSender sender,
            SubGetEndpoint endpoint,
            BotTexts botTexts
    ) {
        super(sender, botTexts);
        this.endpoint = endpoint;
    }

    @Override
    public UserMenu supportedState() {
        return UserMenu.GET_PRICE;
    }

    @Override
    public String getHeaderText(TelegramClient bot, UserData user) {
        var translations = botTexts.forUser(user, "start.UserGetPrice");
        var restAns = endpoint.getPrices(user.getChatId());
        if (restAns.getStatus() >= 300) return null;

        var fcd = restAns.getResponse();
        if (!fcd.isResult()) return fcd.getCause();

        String buyStr = getPricesStr(fcd.getBuySubPrices(), fcd, translations);
        String bargainStr = getPricesStr(fcd.getBargainBuySubPrices(), fcd, translations);
        String sellStr = getPricesStr(fcd.getSellSubPrices(), fcd, translations);
        String getWorkerStr = getWorkerStr(fcd, translations);

        var ans = new ArrayList<String>();
        // 1. Заголовок
        ans.add(
                String.format("<i>%s</i>",
                        translations.format("refill-b-a-commission-based-subscription-pay", Map.of("emphasis", String.format("<b>%s</b>", translations.get("refill-b-a-commission-based-subscription-pay.emphasis")))))
        );
        // 2. Покупка
        ans.add(String.format("""
                        %s <b>%s</b>
                        %s""",
                        DynamicEmoji.GAS.getEmoji(),
                        translations.get("refill-buying"),
                        buyStr));
        // 3. Bargain (если можно)
        if (user.isBargainAllowed())
            ans.add(String.format("""
                            %s <b>%s</b>
                            %s""",
                            DynamicEmoji.GAS.getEmoji(),
                            translations.get("refill-bargain"),
                            bargainStr));
        // 4. Продажа
        ans.add(String.format("""
                        %s <b>%s</b>
                        %s""",
                        DynamicEmoji.GAS.getEmoji(),
                        translations.get("refill-selling"),
                        sellStr));
        // 5. Воркер
        ans.add(String.format("""
                        %s <b>%s</b>
                        %s""",
                        DynamicEmoji.WORKER.getEmoji(),
                        translations.get("y-cs-worker"),
                        getWorkerStr));
        // 6. Курс
        ans.add(String.format(
                "<i>1 USD = %.2f RUB</i>",
                fcd.getCurrency().doubleValue()
        ));
        return String.join("\n\n", ans);
    }

    private String getPricesStr(Map<MarketType, BigDecimal> prices, FcdGetPricesDto dto, LocalizedTexts translations) {
        return prices.entrySet()
                .stream()
                // сортируем по читабельному имени рынка
                .sorted(Comparator.comparing(e ->
                        e.getKey().getMarketName()))
                .map(entry -> {
                    MarketType market = entry.getKey();
                    BigDecimal usdPrice = entry.getValue() == null
                            ? BigDecimal.ZERO
                            : entry.getValue();
                    BigDecimal rubPrice = usdPrice
                            .multiply(dto.getCurrency())
                            .setScale(0, RoundingMode.HALF_UP);

                    long rubLong = rubPrice.longValue();

                    return String.format("• <b>%s</b>: <code>$%.2f</code>  (~<b>%,d₽</b>) %s",
                            market.getMarketName(),
                            usdPrice.doubleValue(),
                            rubLong,
                            translations.get("per-1-000-of-trading-volume"));
                })
                .collect(Collectors.joining("\n"));
    }

    private String getWorkerStr(FcdGetPricesDto dto, LocalizedTexts translations) {
        var wpd = dto.getWorkerPriceData();
        var usdPrice = wpd.getPrice().setScale(2, RoundingMode.HALF_UP);
        var rubPrice = usdPrice
                .multiply(dto.getCurrency())
                .setScale(0, RoundingMode.HALF_UP);
        return String.format("• %s",
                translations.format("account-b-per-month", Map.of("value1", String.format("<code>$%.2f</code>", usdPrice.doubleValue()), "value2", String.format("<b>%,d₽</b>", rubPrice.longValue()), "emphasis", String.format("<b>1 %s</b>", translations.get("account-b-per-month.emphasis")))));
    }

    @Override
    public UserMenu retState() {
        return UserMenu.START;
    }

    @RequiredArgsConstructor
    private enum Dir {
        BUY("Покупка"),
        SELL("Продажа");

        private final String name;
    }
}
