package cs.youtrade.autotrade.client.telegram.menu.start.user.accounts.util;

import cs.youtrade.autotrade.client.util.emoji.DynamicEmoji;
import cs.youtrade.telegram.buttons.IMenuEnum;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum AccountsChooseOption implements IMenuEnum {
    BUYER_ACCOUNT("📥", "Покупка", 0, DynamicEmoji.ITEM_RECEIVE.getEmoji()),
    SELLER_ACCOUNT("📤", "Продажа", 0, DynamicEmoji.ITEM_SEND.getEmoji()),
    WORKER_ACCOUNT("🚚", "Воркер", 0, DynamicEmoji.WORKER.getEmoji()),
    // Назад
    RETURN("↩️", "Назад", 3, "");

    private final String emojiName;
    private final String description;
    private final String buttonName;
    private final String optionName;
    private final String dynamicEmoji;
    private final int rowNum;

    AccountsChooseOption(
            String emojiName,
            String description,
            int rowNum,
            String dynamicEmoji
    ) {
        this.emojiName = emojiName;
        this.description = description;
        this.buttonName = emojiName + " " + description;
        this.optionName = name();
        this.rowNum = rowNum;
        this.dynamicEmoji = dynamicEmoji;
    }
}
