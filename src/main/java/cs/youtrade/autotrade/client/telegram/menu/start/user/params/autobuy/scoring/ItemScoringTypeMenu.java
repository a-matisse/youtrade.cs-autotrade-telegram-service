package cs.youtrade.autotrade.client.telegram.menu.start.user.params.autobuy.scoring;

import cs.youtrade.autotrade.client.telegram.prototype.IMenuEnum;
import cs.youtrade.autotrade.client.util.autotrade.ItemScoringType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
@AllArgsConstructor
public enum ItemScoringTypeMenu implements IMenuEnum {
    SINGLE(
            ItemScoringType.SINGLE,
            "👤 Одиночный",
            0
    ),
    GROUP(
            ItemScoringType.GROUP,
            "👥 Групповой",
            0
    ),
    MEAN(
            ItemScoringType.MEAN,
            "📏 Усредненная",
            0
    ),
    RETURN(
            "↩️ Назад",
            1
    );

    private ItemScoringType itemScoringType;
    private final String buttonName;
    private final int rowNum;
}
