package cs.youtrade.autotrade.client.telegram.menu.start.getprice;

import cs.youtrade.autotrade.client.telegram.menu.UserMenu;
import cs.youtrade.autotrade.client.telegram.prototype.data.UserData;
import cs.youtrade.autotrade.client.telegram.prototype.menu.text.AbstractTerminalTextMenuState;
import cs.youtrade.autotrade.client.telegram.prototype.sender.text.UserTextMessageSender;
import cs.youtrade.autotrade.client.util.autotrade.MarketType;
import cs.youtrade.autotrade.client.util.autotrade.dto.norole.FcdGetPricesDto;
import cs.youtrade.autotrade.client.util.autotrade.endpoint.norole.SubGetEndpoint;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.generics.TelegramClient;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Comparator;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class UserGetPriceState extends AbstractTerminalTextMenuState {
    private final SubGetEndpoint endpoint;

    public UserGetPriceState(
            UserTextMessageSender sender,
            SubGetEndpoint endpoint
    ) {
        super(sender);
        this.endpoint = endpoint;
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

        String buyStr = getPricesStr(fcd.getBuySubPrices(), fcd);
        String sellStr = getPricesStr(fcd.getSellSubPrices(), fcd);

        return String.format("""
                        ⛽ <b>ReFill — Покупка</b>:
                        %s
                        
                        ⛽ <b>ReFill — Продажа</b>:
                        %s
                        
                        <b>ReFill</b> — комиссионная подписка: платите только с реально выполненных сделок, пропорционально обороту.
                        
                        <i>1 USD = %.2f RUB</i>
                        """,
                buyStr,
                sellStr,
                fcd.getCurrency().doubleValue()
        );
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

                    return String.format(
                            "<b>%s</b> — <b>$%.2f</b> (~<b>%,d₽</b>) за $1000 оборота",
                            market.getMarketName(),
                            usdPrice.doubleValue(),
                            rubLong
                    );
                })
                .collect(Collectors.joining("\n"));
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
