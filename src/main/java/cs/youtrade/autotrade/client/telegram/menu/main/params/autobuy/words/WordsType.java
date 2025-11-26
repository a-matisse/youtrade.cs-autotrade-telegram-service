package cs.youtrade.autotrade.client.telegram.menu.main.params.autobuy.words;

import cs.youtrade.autotrade.client.telegram.prototype.IMenuEnum;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum WordsType implements IMenuEnum {
    INCLUDED("Включенные слова"),
    EXCLUDED("Исключенные слова");

    private final String buttonName;
}
