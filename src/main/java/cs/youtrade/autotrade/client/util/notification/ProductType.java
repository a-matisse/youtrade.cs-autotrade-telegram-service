package cs.youtrade.autotrade.client.util.notification;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

public enum ProductType {
    // Самый первый и главный бот
    YOUTRADE_AUTOBUY(ActionType.FLEX),
    YOUTRADE_AUTOSELL(ActionType.FLEX),
    YOUTRADE_PRO(ActionType.FLEX, Set.of(YOUTRADE_AUTOBUY, YOUTRADE_AUTOSELL)),
    // Бот-парсер, ассистент главного бота (более дешевая версия)
    YOUTRADE_LITE(ActionType.FIX),
    // ToDo: Бот, который присылает уведомления о возможности снайпить позиции
    YOUTRADE_NOTIFY(ActionType.FLEX);

    private final ActionType type;
    private final Set<ProductType> children;

    ProductType(ActionType type) {
        this(type, Set.of());
    }

    ProductType(ActionType type, Set<ProductType> children) {
        this.type = type;
        this.children = children;
    }

    public boolean isPerAction() {
        return this.type == ActionType.FLEX;
    }

    public boolean isPerPeriod() {
        return this.type == ActionType.FIX;
    }

    /**
     * Проверяет, есть ли услуга в подписках пользователя
     */
    public boolean hasService(
            Collection<ProductType> userSubscriptions
    ) {
        return findServiceInSubscriptions(userSubscriptions, this) != null;
    }

    /**
     * Рекурсивно ищет услугу в подписках пользователя (поиск вглубину)
     *
     * @param userSubscriptions подписки пользователя
     * @param targetService     целевая услуга для поиска
     * @return родительская подписка, содержащая услугу, или null если не найдено
     */
    private static ProductType findServiceInSubscriptions(
            Collection<ProductType> userSubscriptions,
            ProductType targetService
    ) {
        for (ProductType subscription : userSubscriptions) {
            ProductType result = findServiceRecursive(subscription, targetService, new HashSet<>());
            if (result != null)
                return result;
        }
        return null;
    }

    /**
     * Рекурсивный поиск услуги в дереве зависимостей (DFS)
     */
    private static ProductType findServiceRecursive(
            ProductType current,
            ProductType target,
            Set<ProductType> visited
    ) {
        if (!visited.add(current))
            return null; // Уже посещали эту ноду

        // Если нашли целевую услугу
        if (current == target)
            return current;

        // Ищем в зависимостях
        for (ProductType dependency : current.children) {
            ProductType result = findServiceRecursive(dependency, target, visited);
            if (result != null)
                return result;
        }

        return null;
    }

    private enum ActionType {
        FIX,
        FLEX
    }
}
