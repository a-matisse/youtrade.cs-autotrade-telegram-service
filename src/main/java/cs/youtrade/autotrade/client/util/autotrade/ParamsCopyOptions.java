package cs.youtrade.autotrade.client.util.autotrade;

import lombok.Getter;

import java.util.*;

@Getter
public enum ParamsCopyOptions {
    // Общие настройки
    FULL("Полный"),

    // 1. Подпункт со словами
    WORDS("Все слова", FULL),
    EXCLUDED_WORDS("Список исключаемых", WORDS),
    INCLUDED_WORDS("Список включаемых", WORDS),

    // 2. Подпункт с основными параметрами
    MAIN_ONLY("Основные", FULL),
    AUTOBUY("Параметры автопокупки", MAIN_ONLY),
    AUTOSELL("Параметры автопродажи", MAIN_ONLY),

    // 3. Подпункт с оценками
    SCORING("Список scoring-ID", FULL);

    private final String modeName;
    private final ParamsCopyOptions ancestor;

    ParamsCopyOptions(String modeName) {
        this(modeName, null);
    }

    ParamsCopyOptions(String modeName, ParamsCopyOptions ancestor) {
        this.modeName = modeName;
        this.ancestor = ancestor;
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

    /**
     * Находит общего предка для двух вариантов копирования параметров
     * Если у одного нет предка - возвращает того, что без предка
     * Если у обоих нет предка - возвращает первый
     */
    public ParamsCopyOptions combine(ParamsCopyOptions second) {
        if (second == null || this == second)
            return this;

        // Получаем всех предков первого варианта
        Set<ParamsCopyOptions> firstAncestors = getAllAncestors(this);

        // Ищем общего предка
        ParamsCopyOptions current = second;
        while (current != null) {
            if (firstAncestors.contains(current)) {
                return current;
            }
            current = current.getAncestor();
        }

        // Если общего предка нет, возвращаем того у кого нет предка или первого
        if (this.getAncestor() == null) {
            return this;
        }
        if (second.getAncestor() == null) {
            return second;
        }

        return this; // по умолчанию возвращаем первого
    }

    /**
     * Проверяет, содержит ли данный вариант указанного наследника (DFS поиск)
     */
    public ParamsCopyOptions checkAndGet(ParamsCopyOptions toCompare) {
        if (toCompare == null)
            return null;

        // Проверка, если toCompare - текущий
        if (this == toCompare)
            return this;
        // Проверка, если toCompare - предок
        if (isAncestor(this, toCompare))
            return this;
        // Проверка, если toCompare - потомок
        if (isDescendant(this, toCompare))
            return toCompare;

        // Если ни потомок, ни предок, ни текущий
        return null;
    }

    public static boolean isAncestor(ParamsCopyOptions cur, ParamsCopyOptions ancestor) {
        if (cur.getAncestor() == null)
            return false;
        if (cur.getAncestor() == ancestor)
            return true;

        return isAncestor(cur.getAncestor(), ancestor);
    }

    public static boolean isDescendant(ParamsCopyOptions current, ParamsCopyOptions target) {
        if (current == target)
            return true;

        // Ищем среди прямых наследников текущего узла
        for (ParamsCopyOptions child : getDirectChildren(current))
            return isDescendant(child, target);

        return false;
    }

    private static List<ParamsCopyOptions> getDirectChildren(ParamsCopyOptions parent) {
        List<ParamsCopyOptions> children = new ArrayList<>();
        for (ParamsCopyOptions option : ParamsCopyOptions.values())
            if (option.getAncestor() == parent)
                children.add(option);

        return children;
    }

    // Вспомогательные методы
    private static Set<ParamsCopyOptions> getAllAncestors(ParamsCopyOptions option) {
        Set<ParamsCopyOptions> ancestors = new HashSet<>();
        ParamsCopyOptions current = option;

        while (current != null) {
            ancestors.add(current);
            current = current.getAncestor();
        }

        return ancestors;
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
