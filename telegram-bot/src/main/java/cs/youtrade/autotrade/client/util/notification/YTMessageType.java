package cs.youtrade.autotrade.client.util.notification;

public enum YTMessageType {
    ERROR,
    TEXT,
    IMAGE,
    DOCUMENT;

    public static YTMessageType fromYouTradeNotification(YTMessageNotification data) {
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
