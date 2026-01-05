package cs.youtrade.autotrade.client.telegram.menu.start.user.params.quick.enable.prototype;

import cs.youtrade.autotrade.client.telegram.prototype.IMenuEnum;
import cs.youtrade.autotrade.client.util.autotrade.QuickConfigGrade;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
@AllArgsConstructor
public enum QuickConfigGradeMenu implements IMenuEnum {
    MINIMAL("🔓 Простой", 0, QuickConfigGrade.MINIMAL),
    MODERATE("⚖️ Умеренный", 0, QuickConfigGrade.MODERATE),
    STRICT("🔒 Строгий", 0, QuickConfigGrade.STRICT),
    ABSOLUTE("🚫 Тотальный", 1, QuickConfigGrade.ABSOLUTE),
    // Назад
    RETURN("↩️ Назад", 2);

    private final String buttonName;
    private final int rowNum;
    private QuickConfigGrade grade;
}
