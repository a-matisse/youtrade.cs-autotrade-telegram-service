package cs.youtrade.autotrade.client.util.notification;

public enum BargainFailureReason {
    CREATION_REJECTED,
    CREATED_ORDER_MISSING,
    MARKETPLACE_DECLINED,
    MARKETPLACE_EXPIRED,
    MARKETPLACE_CANCELLED,
    COUNTER_OFFER_DECLINED,
    BARGAIN_AWAIT_EXPIRED
}
