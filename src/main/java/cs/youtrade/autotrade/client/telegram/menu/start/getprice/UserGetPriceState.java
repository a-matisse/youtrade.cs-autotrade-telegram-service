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

import java.math.BigDecimal;
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
    public String getHeaderText(UserData user) {
        var restAns = endpoint.getPrices(user.getChatId());
        if (restAns.getStatus() >= 300)
            return null;

        var fcd = restAns.getResponse();
        if (!fcd.isResult())
            return fcd.getCause();

        String buyStr = getPricesStr(fcd.getBuySubPrices(), fcd, Dir.BUY);
        String sellStr = getPricesStr(fcd.getSellSubPrices(), fcd, Dir.SELL);
        return String.format("""
                        🔋 ReFill — умная подписка для автоторговли
                        
                        %s
                        
                        %s
                        
                        Что это? ReFill — это гибкая система комиссий, которая рассчитывается пропорционально вашему обороту. Вы платите только когда торгуете, и только с выполненных операций покупки/продажи.
                        
                        *1 USD = %s RUB
                        **💡 ReFill — подписка, которая считается за счет операций покупки или продаж сервисом.
                        """,
                buyStr,
                sellStr,
                fcd.getCurrency()
        );
    }

    @Override
    public UserMenu retState() {
        return UserMenu.START;
    }

    private String getPricesStr(Map<MarketType, BigDecimal> prices, FcdGetPricesDto dto, Dir dir) {
        return prices
                .entrySet()
                .stream()
                .map(entry -> {
                    MarketType market = entry.getKey();
                    BigDecimal usdPrice = entry.getValue();
                    BigDecimal rubPrice = usdPrice
                            .multiply(dto.getCurrency());

                    return String.format(
                            "⛽ ReFill-%s (%s): $%.0f (%.0f₽) за каждые $1000 оборота",
                            dir.name,
                            market.name(),
                            usdPrice,
                            rubPrice
                    );
                })
                .collect(Collectors.joining("\n"));
    }

    @RequiredArgsConstructor
    private enum Dir {
        BUY("Покупка"),
        SELL("Продажа");

        private final String name;
    }
}
