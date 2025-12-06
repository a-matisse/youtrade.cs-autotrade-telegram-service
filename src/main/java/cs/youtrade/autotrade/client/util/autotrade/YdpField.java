package cs.youtrade.autotrade.client.util.autotrade;

import lombok.Getter;

import java.util.Arrays;
import java.util.Optional;
import java.util.stream.Collectors;

import static cs.youtrade.autotrade.client.util.autotrade.FcdStringUtils.findClosest;

@Getter
public enum YdpField implements FcdDistance {
    PERIOD(
            "Период моделирования",
            "Минимальное значение: 1 дн."
    ),
    MIN_PROFIT(
            "Минимальная оценка дохода",
            "Минимальное значение: 90%"
    ),
    MIN_TREND_SCORE(
            "Задает минимальный наклон тренда для покупаемого предмета",
            "Допустимый наклон: от -100%"
    ),
    MAX_TREND_SCORE(
            "Задает максимальный наклон тренда для покупаемого предмета",
            "Допустимый наклон: от -100% и больше minTrendScore"
    );

    private final String fName;
    private final String desc;
    private final String fork;

    YdpField(
            String desc,
            String fork
    ) {
        this.fName = convertEnumName(this.name());
        this.desc = desc;
        this.fork = fork;
    }

    public static YdpField fromFName(String input) {
        return findClosest(values(), input);
    }

    public static String generateDescription() {
        return Arrays
                .stream(values())
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
}
