package cs.youtrade.autotrade.client.telegram.menu.start.getprice;

import cs.youtrade.autotrade.client.telegram.menu.UserMenu;
import cs.youtrade.autotrade.client.telegram.prototype.data.UserData;
import cs.youtrade.autotrade.client.telegram.prototype.menu.text.YTPTerminalTextMenuState;
import cs.youtrade.autotrade.client.telegram.prototype.sender.text.UserTextMessageSender;
import cs.youtrade.autotrade.client.util.autotrade.MarketType;
import cs.youtrade.autotrade.client.util.autotrade.dto.norole.FcdGetPricesDto;
import cs.youtrade.autotrade.client.util.autotrade.endpoint.norole.SubGetEndpoint;
import cs.youtrade.autotrade.client.util.autotrade.endpoint.user.params.ParamsEndpoint;
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
    private final ParamsEndpoint paramsEndpoint;

    public UserGetPriceState(
            UserTextMessageSender sender,
            SubGetEndpoint endpoint,
            ParamsEndpoint paramsEndpoint
    ) {
        super(sender);
        this.endpoint = endpoint;
        this.paramsEndpoint = paramsEndpoint;
    }

    @Override
    public UserMenu supportedState() {
        return UserMenu.GET_PRICE;
    }

    @Override
    public String getHeaderText(TelegramClient bot, UserData user) {
        var restAns = endpoint.getPrices(user.getChatId());
        if (restAns.getStatus() >= 300) return null;

        var fcd = restAns.getResponse();
        if (!fcd.isResult()) return fcd.getCause();

        var paramsAns = paramsEndpoint.getCurrent(user.getChatId());
        if (paramsAns.getStatus() >= 300) return null;

        var paramsFcd = paramsAns.getResponse();
        if (!paramsFcd.isResult()) return paramsFcd.getCause();

        String buyStr = getPricesStr(fcd.getBuySubPrices(), fcd);
        String bargainStr = getPricesStr(fcd.getBargainBuySubPrices(), fcd);
        String sellStr = getPricesStr(fcd.getSellSubPrices(), fcd);
        String getWorkerStr = getWorkerStr(fcd);

        var ans = new ArrayList<String>();
        // 1. Заголовок
        ans.add(
                "<i><b>ReFill</b> — комиссионная подписка: платите только с реальных сделок, пропорционально обороту</i>"
        );
        // 2. Покупка
        ans.add(String.format("""
                        %s <b>ReFill — Покупка</b>
                        %s""",
                DynamicEmoji.GAS.getEmoji(),
                buyStr
        ));
        // 3. Bargain (если можно)
        if (paramsFcd.getData().isBargainAllowed(user))
            ans.add(String.format("""
                            %s <b>ReFill — Баргейн</b>
                            %s""",
                    DynamicEmoji.GAS.getEmoji(),
                    bargainStr
            ));
        // 4. Продажа
        ans.add(String.format("""
                        %s <b>ReFill — Продажа</b>
                        %s""",
                DynamicEmoji.GAS.getEmoji(),
                sellStr
        ));
        // 5. Воркер
        ans.add(String.format("""
                        %s <b>Воркер</b>
                        %s""",
                DynamicEmoji.WORKER.getEmoji(),
                getWorkerStr
        ));
        // 6. Курс
        ans.add(String.format(
                "<i>1 USD = %.2f RUB</i>",
                fcd.getCurrency().doubleValue()
        ));
        return String.join("\n\n", ans);
    }

    private String getPricesStr(Map<MarketType, BigDecimal> prices, FcdGetPricesDto dto) {
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

                    return String.format("• <b>%s</b>: <code>$%.2f</code>  (~<b>%,d₽</b>) за $1000 оборота",
                            market.getMarketName(),
                            usdPrice.doubleValue(),
                            rubLong
                    );
                })
                .collect(Collectors.joining("\n"));
    }

    private String getWorkerStr(FcdGetPricesDto dto) {
        var wpd = dto.getWorkerPriceData();
        var usdPrice = wpd.getPrice().setScale(2, RoundingMode.HALF_UP);
        var rubPrice = usdPrice
                .multiply(dto.getCurrency())
                .setScale(0, RoundingMode.HALF_UP);
        return String.format("• <b>1 аккаунт</b>: <code>$%.2f</code> (~<b>%,d₽</b>) в месяц",
                usdPrice.doubleValue(),
                rubPrice.longValue()
        );
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
