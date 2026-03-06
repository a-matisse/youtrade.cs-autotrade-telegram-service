package cs.youtrade.autotrade.client.util.notification;

public enum YouTradeNotificationType {
    ERROR,
    TEXT,
    IMAGE,
    DOCUMENT;

    public static YouTradeNotificationType fromYouTradeNotification(YouTradeNotification data) {
        if (data.getDocument() != null)
            return DOCUMENT;
        else if (data.getImage() != null)
            return IMAGE;
        else if (data.getText() != null)
            return TEXT;
        else
            return ERROR;
    }
}
