package cs.youtrade.autotrade.client.telegram.menu.start.user.quick.enable.prototype;

import cs.youtrade.autotrade.client.telegram.prototype.menu.text.AbstractTextMenuState;
import cs.youtrade.autotrade.client.telegram.prototype.sender.text.UserTextMessageSender;

public abstract class AbstractQuickConfigGradeState extends AbstractTextMenuState<QuickConfigGradeMenu> {
    public AbstractQuickConfigGradeState(UserTextMessageSender sender) {
        super(sender);
    }

    @Override
    public QuickConfigGradeMenu getOption(String optionStr) {
        return QuickConfigGradeMenu.valueOf(optionStr);
    }

    @Override
    public QuickConfigGradeMenu[] getOptions() {
        return QuickConfigGradeMenu.values();
    }
}
