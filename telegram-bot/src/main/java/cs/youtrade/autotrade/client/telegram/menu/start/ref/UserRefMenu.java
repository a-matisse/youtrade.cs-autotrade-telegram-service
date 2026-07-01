package cs.youtrade.autotrade.client.telegram.menu.start.ref;

import cs.youtrade.telegram.buttons.IMenuEnum;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum UserRefMenu implements IMenuEnum {
    REF_CREATE("📝 Создать код", 0),
    REF_CONNECT("🔗 Подключить", 0),
    RETURN("↩️ Назад", 1);

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
