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
    BUY_FAILED
}
