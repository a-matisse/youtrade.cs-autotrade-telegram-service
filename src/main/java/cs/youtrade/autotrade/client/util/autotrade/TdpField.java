package cs.youtrade.autotrade.client.util.autotrade;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

@RequiredArgsConstructor
public enum TdpField {
    MIN_PRICE("minprice", DirType.BUY),
    MAX_PRICE("maxprice", DirType.BUY),
    MIN_POPULARITY("minpopularity", DirType.BUY),
    MAX_POPULARITY("maxpopularity", DirType.BUY),
    MAX_AUTOBUY_PERCENT("maxautobuypercent", DirType.BUY),
    MIN_DAYS_HOLD("mindayshold", DirType.BUY),
    MAX_DAYS_HOLD("maxdayshold", DirType.BUY),
    CORRECTION_COEFFICIENT("correctioncoefficient", DirType.BUY),
    MAX_DUPLICATES("maxduplicates", DirType.BUY),
    DUPLICATE_LAG("duplicatelag", DirType.BUY),
    MANIPULATION_COEFF("manipulationcoeff", DirType.BUY),
    MIN_TREND_SCORE("mintrendscore", DirType.BUY),
    MAX_TREND_SCORE("maxtrendscore", DirType.BUY),
    MIN_AUTOSELL_PROFIT("minautosellprofit", DirType.SELL),
    MAX_AUTOSELL_PROFIT("maxautosellprofit", DirType.SELL),
    EVAL_MODE_C1("evalmodec1", DirType.SELL);

    @Getter
    private final String fName;
    private final DirType dir;

    public ParamsCopyOptions pcoFromDir() {
        return switch (dir) {
            case BUY -> ParamsCopyOptions.AUTOBUY;
            case SELL -> ParamsCopyOptions.AUTOSELL;
        };
    }

    public static Optional<TdpField> fromFName(String fName) {
        String lowerCaseFName = fName.toLowerCase();
        return Arrays.stream(values())
                .filter(t -> t.fName.equals(lowerCaseFName))
                .findFirst();
    }

    public static String getInfo() {
        return String.format("""
                        Доступные поля автопокупки:
                        %s
                        
                        Доступные поля автопродажи:
                        %s
                        """,
                getBuyTypes(),
                getSellTypes()
        );
    }

    public static List<String> getBuyTypes() {
        return Stream.of(TdpField.values())
                .filter(f -> f.dir.equals(DirType.BUY))
                .map(TdpField::getFName)
                .toList();
    }

    public static List<String> getSellTypes() {
        return Stream.of(TdpField.values())
                .filter(f -> f.dir.equals(DirType.SELL))
                .map(TdpField::getFName)
                .toList();
    }

    private enum DirType {
        BUY, SELL
    }
}
