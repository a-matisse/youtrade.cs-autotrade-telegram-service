package cs.youtrade.autotrade.client.telegram.menu.start.user.quick.enable.prototype;

import cs.youtrade.autotrade.client.util.autotrade.QuickConfigGrade;
import cs.youtrade.telegram.buttons.IMenuEnum;
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
    private final String optionName;
    private final int rowNum;
    private QuickConfigGrade grade;

    QuickConfigGradeMenu(
            String buttonName,
            int rowNum
    ) {
        this.buttonName = buttonName;
        this.optionName = name();
        this.rowNum = rowNum;
    }

    QuickConfigGradeMenu(
            String buttonName,
            int rowNum,
            QuickConfigGrade grade
    ) {
        this(buttonName, rowNum);
        this.grade = grade;
    }
}
