package cs.youtrade.autotrade.client.telegram.menu.start.user.params.deep.autobuy.words;

import cs.youtrade.autotrade.client.telegram.prototype.IMenuEnum;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum WordsType implements IMenuEnum {
    INCLUDED("Включенные слова", 0),
    EXCLUDED("Исключенные слова", 1);

    private final String buttonName;
    private final int rowNum;
}
