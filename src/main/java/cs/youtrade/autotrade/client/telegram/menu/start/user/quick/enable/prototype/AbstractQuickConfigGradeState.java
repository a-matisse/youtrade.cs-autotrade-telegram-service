package cs.youtrade.autotrade.client.telegram.menu.start.user.quick.enable.prototype;

import cs.youtrade.autotrade.client.telegram.prototype.data.UserData;
import cs.youtrade.autotrade.client.telegram.prototype.menu.text.base.YTPTextMenuState;
import cs.youtrade.autotrade.client.telegram.prototype.sender.text.UserTextMessageSender;

public abstract class AbstractQuickConfigGradeState extends YTPTextMenuState<QuickConfigGradeMenu> {
    public AbstractQuickConfigGradeState(UserTextMessageSender sender) {
        super(sender);
    }

    @Override
    public QuickConfigGradeMenu getOption(String optionStr) {
        return QuickConfigGradeMenu.valueOf(optionStr);
    }

    @Override
    public QuickConfigGradeMenu[] getOptions(UserData userData) {
        return QuickConfigGradeMenu.values();
    }
}
