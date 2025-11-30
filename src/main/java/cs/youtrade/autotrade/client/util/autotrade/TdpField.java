package cs.youtrade.autotrade.client.util.autotrade;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static cs.youtrade.autotrade.client.util.autotrade.FcdStringUtils.findClosest;

@RequiredArgsConstructor
public enum TdpField implements FcdDistance {
    AB_MIN_PRICE(
            "minprice",
            DirType.BUY,
            "Минимальная цена",
            "Допустимая цена: от $0 (Пример: 20)"
    ),
    AB_MAX_PRICE(
            "maxprice",
            DirType.BUY,
            "Максимальная цена",
            "Допустимая цена: от $0 и не меньше minPrice (Пример: 150)"
    ),
    AB_PRICE_FACTOR(
            "pricefactor",
            DirType.BUY,
            "Коэффициент превышения над минимальной рыночной ценой",
            "Допустимое значение коэффициента: от 10% до 1000% (Пример: 110)"
    ),
    AB_MIN_POPULARITY(
            "minpopularity",
            DirType.BUY,
            "Минимальное количество продаж в месяц",
            "Допустимое значение продаж в месяц: от 0 продаж (Пример: 50)"
    ),
    AB_MAX_POPULARITY(
            "maxpopularity",
            DirType.BUY,
            "Максимальное количество продаж в месяц",
            "Допустимое значение продаж в месяц: от 0 продаж и не меньше minPopularity (Пример: 500)"
    ),
    AB_MIN_DAYS_HOLD(
            "mindayshold",
            DirType.BUY,
            "Минимальное количество дней, на которое должен удерживаться предмет",
            "Допустимое значение количества дней: от 0 дн. до maxDaysHold"
    ),
    AB_MAX_DAYS_HOLD(
            "maxdayshold",
            DirType.BUY,
            "Максимальное количество дней, на которые возможно удержание предмета",
            "Допустимое значение количества дней: от minDaysHold до 8 дн."
    ),
    AB_CORRECTION_COEFFICIENT(
            "correctioncoefficient",
            DirType.BUY,
            "Коэффициент для поправки на количество дней ожидания",
            "Допустимое значение коэффициента: от 1% до 100%"
    ),
    AB_MANIPULATION_COEFF(
            "manipulationcoeff",
            DirType.BUY,
            "Коэффициент для определения манипуляций на основе сильных колебаний цены",
            "Допустимое значение коэффициента: от 1% до 100%"
    ),
    AB_MAX_DUPLICATES(
            "maxduplicates",
            DirType.BUY,
            "Задает максимальное количество одинаковых предметов для ограничения дублирования покупок",
            "Допустимое количество дубликатов: от 0 шт."
    ),
    AB_DUPLICATE_LAG(
            "duplicatelag",
            DirType.BUY,
            "Задает максимальное количество одинаковых предметов для ограничения дублирования покупок",
            "Допустимая для анализа дублирования: от 1 дн."
    ),
    AB_MIN_TREND_SCORE(
            "mintrendscore",
            DirType.BUY,
            "Задает минимальный наклон тренда для покупаемого предмета",
            "Допустимый наклон: от -100%"
    ),
    AB_MAX_TREND_SCORE(
            "maxtrendscore",
            DirType.BUY,
            "Задает максимальный наклон тренда для покупаемого предмета",
            "Допустимый наклон: от -100% и больше minTrendScore"
    ),
    AS_MIN_AUTOSELL_PROFIT(
            "minautosellprofit",
            DirType.SELL,
            "Минимальный процент рентабельности позиции",
            "Допустимая рентабельность: от 0 до maxAutoSellProfit"
    ),
    AS_MAX_AUTOSELL_PROFIT(
            "maxautosellprofit",
            DirType.SELL,
            "Максимальный процент рентабельности позиции",
            "Допустимая рентабельность: не меньше minAutoSellProfit"
    ),
    AS_EVAL_MODE_C1(
            "evalmodec1",
            DirType.SELL,
            "Первый коэффициент EvalMode для настройки автопродажи",
            "Допустимое значение коэффициента: от 1 до 99 (включительно)"
    ),
    SC_MIN_PROFIT(
            "minprofit",
            DirType.SCORING,
            "Минимальная оценка дохода",
            "Минимальное значение: 90%"
    ),
    SC_PERIOD(
            "period",
            DirType.SCORING,
            "Период моделирования",
            "Минимальное значение: 1 дн."
    );

    @Getter
    private final String fName;
    private final DirType dir;
    @Getter
    private final String desc;
    @Getter
    private final String fork;

    public static TdpField fromFName(String input) {
        return findClosest(values(), input);
    }

    public static String generateDescription(DirType dir) {
        return Arrays
                .stream(values())
                .filter(t -> t.dir == dir)
                .map(val -> String.format(
                        "<code>%s</code> — %s", val.getFName(), val.getDesc()))
                .collect(Collectors.joining("\n"));
    }

    public String getForkByField() {
        return String.format(
                "%s — %s", fName, fork);
    }

    public enum DirType {
        BUY, SELL, SCORING
    }
}
