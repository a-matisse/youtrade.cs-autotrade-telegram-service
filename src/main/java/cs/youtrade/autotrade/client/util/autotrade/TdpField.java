package cs.youtrade.autotrade.client.util.autotrade;

import lombok.Getter;

import java.util.Arrays;
import java.util.stream.Collectors;

import static cs.youtrade.autotrade.client.util.autotrade.FcdStringUtils.findClosest;

@Getter
public enum TdpField implements FcdDistance {
    MIN_PRICE(
            DirType.BUY,
            "Минимальная цена",
            "Допустимая цена: от $0 (Пример: 20)"
    ),
    MAX_PRICE(
            DirType.BUY,
            "Максимальная цена",
            "Допустимая цена: от $0 и не меньше minPrice (Пример: 150)"
    ),
    MIN_POPULARITY(
            DirType.BUY,
            "Минимальное количество продаж в месяц",
            "Допустимое значение продаж в месяц: от 0 продаж (Пример: 50)"
    ),
    MAX_POPULARITY(
            DirType.BUY,
            "Максимальное количество продаж в месяц",
            "Допустимое значение продаж в месяц: от 0 продаж и не меньше minPopularity (Пример: 500)"
    ),
    PRICE_FACTOR(
            DirType.BUY,
            "Коэффициент превышения над минимальной рыночной ценой",
            "Допустимое значение коэффициента: от 10% до 1000% (Пример: 110)"
    ),
    MIN_DAYS_HOLD(
            DirType.BUY,
            "Минимальное количество дней, на которое должен удерживаться предмет",
            "Допустимое значение количества дней: от 0 дн. до maxDaysHold"
    ),
    MAX_DAYS_HOLD(
            DirType.BUY,
            "Максимальное количество дней, на которые возможно удержание предмета",
            "Допустимое значение количества дней: от minDaysHold до 8 дн."
    ),
    CORRECTION_COEFFICIENT(
            DirType.BUY,
            "Коэффициент для поправки на количество дней ожидания",
            "Допустимое значение коэффициента: от 1% до 100%"
    ),
    MAX_DUPLICATES(
            DirType.BUY,
            "Задает максимальное количество одинаковых предметов для ограничения дублирования покупок",
            "Допустимое количество дубликатов: от 0 шт."
    ),
    DUPLICATE_LAG(
            DirType.BUY,
            "Задает максимальное количество одинаковых предметов для ограничения дублирования покупок",
            "Допустимая для анализа дублирования: от 1 дн."
    ),
    MANIPULATION_COEFF(
            DirType.BUY,
            "Коэффициент для определения манипуляций на основе сильных колебаний цены",
            "Допустимое значение коэффициента: от 1% до 100%"
    ),
    MIN_AUTOSELL_PROFIT(
            DirType.SELL,
            "Минимальный процент рентабельности позиции",
            "Допустимая рентабельность: от 0 до maxAutoSellProfit"
    ),
    MAX_AUTOSELL_PROFIT(
            DirType.SELL,
            "Максимальный процент рентабельности позиции",
            "Допустимая рентабельность: не меньше minAutoSellProfit"
    ),
    EVAL_MODE_C1(
            DirType.SELL,
            "Первый коэффициент EvalMode для настройки автопродажи",
            "Допустимое значение коэффициента: от 1 до 99 (включительно)"
    );

    private final String fName;
    private final DirType dir;
    private final String desc;
    private final String fork;

    TdpField(
            DirType dir,
            String desc,
            String fork
    ) {
        this.fName = convertEnumName(this.name());
        this.dir = dir;
        this.desc = desc;
        this.fork = fork;
    }

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
        return String.format("%s — %s", fName, fork);
    }

    private static String convertEnumName(String enumName) {
        return enumName.toLowerCase().replace("_", "");
    }

    public enum DirType {
        BUY, SELL
    }
}
