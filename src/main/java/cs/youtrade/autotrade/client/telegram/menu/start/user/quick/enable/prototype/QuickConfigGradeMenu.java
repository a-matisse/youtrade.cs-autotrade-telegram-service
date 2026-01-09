package cs.youtrade.autotrade.client.telegram.menu.start.user.quick.enable.prototype;

import cs.youtrade.autotrade.client.telegram.prototype.IMenuEnum;
import cs.youtrade.autotrade.client.util.autotrade.QuickConfigGrade;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
@AllArgsConstructor
public enum QuickConfigGradeMenu implements IMenuEnum {
    MINIMAL("🔓 Мягкий", 0, QuickConfigGrade.MINIMAL),
    MODERATE("⚖️ Умеренный", 0, QuickConfigGrade.MODERATE),
    STRICT("🔒 Строгий", 0, QuickConfigGrade.STRICT),
    ABSOLUTE("☠️ Тотальный", 1, QuickConfigGrade.ABSOLUTE),
    DISABLED("🚫 Выключить", 2, QuickConfigGrade.NONE),
    // Назад
    RETURN("↩️ Назад", 3);

    private final String buttonName;
    private final int rowNum;
    private QuickConfigGrade grade;
}
