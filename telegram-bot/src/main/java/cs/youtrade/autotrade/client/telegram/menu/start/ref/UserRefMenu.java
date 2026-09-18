package cs.youtrade.autotrade.client.telegram.menu.start.ref;

import cs.youtrade.telegram.buttons.IMenuEnum;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum UserRefMenu implements IMenuEnum {
    REF_CREATE("🔗 Создать приглашение", 0),
    REF_INVITE("🔗 Пригласить друга", 0),
    REF_CONNECT("🎟 Ввести код", 1),
    REF_TRANSFER("💳 На баланс сервиса", 2),
    REF_PAYOUT("💸 Получить выплату", 2),
    RETURN("↩️ Назад", 3);

    private final String buttonName;
    private final String optionName;
    private final int rowNum;

    UserRefMenu(
            String buttonName,
            int rowNum
    ) {
        this.buttonName = buttonName;
        this.optionName = name();
        this.rowNum = rowNum;
    }
}
