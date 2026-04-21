package cs.youtrade.autotrade.client.util.notification;

import cs.youtrade.autotrade.client.util.autotrade.MarketType;
import cs.youtrade.autotrade.client.util.autotrade.dto.user.params.FcdParamsGetDto;
import cs.youtrade.autotrade.client.util.emoji.DynamicEmoji;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public abstract class YTSkinNotification extends YTBaseNotification {
    private String givenName;
    private String accountName;
    private MarketType boughtOn;
    private MarketType soldOn;

    public String getTokenInfoStr() {
        return String.format("""
                        %s %s
                        <blockquote>• Параметры: <b>%s</b>
                        • Аккаунт: <b>%s</b></blockquote>
                        """,
                DynamicEmoji.MAP.getEmoji(), getDirection(),
                givenName,
                accountName
        );
    }

    public String getDirection() {
        return FcdParamsGetDto.getDirection(boughtOn, soldOn);
    }
}
