package cs.youtrade.autotrade.client.telegram.menu.start.ref;

import cs.youtrade.autotrade.client.telegram.prototype.IMenuEnum;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum UserRefMenu implements IMenuEnum {
    REF_CREATE("📝 Создать код", 0),
    REF_CONNECT("🔗 Подключить", 0),
    RETURN("↩️ Назад", 1);

    private final String buttonName;
    private final int rowNum;
}
