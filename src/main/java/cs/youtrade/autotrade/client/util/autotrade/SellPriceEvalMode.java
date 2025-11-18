package cs.youtrade.autotrade.client.util.autotrade;

import cs.youtrade.bot.telegram.entity.TelegramDataParameters;
import cs.youtrade.bot.telegram.entity.purchase.YouTradePurchasedItem;
import cs.youtrade.marketcs.grpc.dto.TmFuzzyInfoDto;
import lombok.Getter;

import java.util.Optional;

@Getter
public enum SellPriceEvalMode {
    DEFAULT("Стандартный"),
    INTELLIGENT_V1("Intelligent_V1");

    private final String russianName;

    SellPriceEvalMode(String russianName) {
        this.russianName = russianName;
    }

    public double measureMinPrice(
            TelegramDataParameters tdp,
            YouTradePurchasedItem ytpi,
            TmFuzzyInfoDto tfid,
            double fee
    ) {
        return switch (this) {
            case DEFAULT -> defMinPriceMeasure(tdp, ytpi, fee);
            case INTELLIGENT_V1 -> intMinPriceMeasure(tdp, ytpi, tfid, fee);
        };
    }

    public double defMinPriceMeasure(
            TelegramDataParameters tdp,
            YouTradePurchasedItem ytpi,
            double fee
    ) {
        double basePrice = ytpi.getPrice() / (fee);
        return tdp.getMinSellProfit() * basePrice;
    }

    public double intMinPriceMeasure(
            TelegramDataParameters tdp,
            YouTradePurchasedItem ytpi,
            TmFuzzyInfoDto tfid,
            double fee
    ) {
        double defMinPrice = defMinPriceMeasure(tdp, ytpi, fee);

        var c1 = tdp.getEvalModeC1();
        var percentileOpt = Optional.ofNullable(tfid.getPercentile().get(c1));
        if (percentileOpt.isEmpty())
            return defMinPrice;

        var percentile = percentileOpt.get();
        double c1Price = percentile.getPPrice();

        return Math.max(c1Price, defMinPrice);
    }
}
