package cs.youtrade.autotrade.client.util.autotrade;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.*;
import java.util.stream.Collectors;

@Getter
@AllArgsConstructor
public enum ParamsCopyOptions {
    // Общие настройки
    FULL(
            "Полный",
            "Все настройки"
    ),

    // 1. Подпункт со словами
    WORDS(
            "Все слова",
            "Полный список слов",
            FULL
    ),
    EXCLUDED_WORDS(
            "Список исключаемых",
            "Только исключаемые слова",
            WORDS
    ),
    INCLUDED_WORDS(
            "Список включаемых",
            "Только включаемые слова",
            WORDS
    ),

    // 2. Подпункт с основными параметрами
    MAIN_ONLY(
            "Основные",
            "Только основные настройки",
            FULL
    ),
    AUTOBUY(
            "Параметры автопокупки",
            "Основные настройки автопокупки",
            MAIN_ONLY
    ),
    AUTOSELL(
            "Параметры автопродажи",
            "Основные настройки автопродажи",
            MAIN_ONLY
    ),

    // 3. Подпункт с оценками
    SCORING(
            "Список scoring-ID",
            "Только profit-id",
            FULL
    );

    private final String modeName;
    private final String desc;
    private final ParamsCopyOptions ancestor;

    ParamsCopyOptions(String modeName, String desc) {
        this(modeName, desc, null);
    }

    public static boolean isAncestor(ParamsCopyOptions cur, ParamsCopyOptions ancestor) {
        if (cur.getAncestor() == null)
            return false;
        if (cur.getAncestor() == ancestor || cur == ancestor)
            return true;

        return isAncestor(cur.getAncestor(), ancestor);
    }

    public static ParamsCopyOptions getOrdinal(short ord) {
        return ParamsCopyOptions.values()[ord];
    }

    public static ParamsCopyOptions getOrdinal(String vName) {
        return Arrays.stream(ParamsCopyOptions.values())
                .map(opt -> new InternalData(opt, vName.toUpperCase()))
                .filter(InternalData::filterDist)
                .min(Comparator.comparingInt(InternalData::getDist))
                .map(InternalData::getOpt)
                .orElse(null);
    }

    public static String generateDescription() {
        return Arrays
                .stream(values())
                .map(val -> String.format(
                        "<code>%s</code> — %s", val.name(), val.getDesc()))
                .collect(Collectors.joining("\n"));
    }

    @Getter
    private static class InternalData {
        private static final int MAX_DIST = 5;

        private final ParamsCopyOptions opt;
        private final int dist;

        public InternalData(ParamsCopyOptions opt, String vName) {
            this.opt = opt;
            this.dist = FcdStringUtils.getDist(opt.name(), vName, MAX_DIST);
        }

        private boolean filterDist() {
            return dist >= 0;
        }
    }
}
