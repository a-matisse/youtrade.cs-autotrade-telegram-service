package cs.youtrade.autotrade.client.telegram.menu.start.user.accounts.add.stages.worker.stage3;

import cs.youtrade.telegram.buttons.IMenuEnum;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum WorkerAddMaFileMenu implements IMenuEnum {
    GET_MAFILE("❓ Где я могу найти maFile"),
    RETURN("↩️ Назад");

    private final String buttonName;
    private final String optionName;
    private final int rowNum;

    WorkerAddMaFileMenu(
            String buttonName
    ) {
        this.buttonName = buttonName;
        this.optionName = name();
        this.rowNum = ordinal();
    }
}
