package cs.youtrade.autotrade.client.util.notification;

public enum YTNotificationType {
    MESSAGE,
    BALANCE,
    PAYMENT,
    // Уведомления в Y.CS PRO о продаже
    SELL_ADDED,
    SELL_COMPLETED,
    SELL_FAILED,
    // Уведомления в Y.CS PRO о покупке
    BUY_COMPLETED,
    BUY_FAILED,
    BARGAIN_CREATED,
    BARGAIN_ACCEPTED,
    BARGAIN_FAILED,
    // Асинхронные уведомления из портфеля
    PORTFOLIO_UPLOADED,
    PORTFOLIO_RESTRICTED,
    PORTFOLIO_ALLOWED,
    // Изменения предметов
    ITEM_REMOVED,
    ITEM_CHANGED,
    // Уведомления из marketapp-service
    MAFILE_DELETED,
    // Различный мета-уведомления
    WAIT
}
