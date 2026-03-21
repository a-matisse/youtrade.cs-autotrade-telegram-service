package cs.youtrade.autotrade.client.telegram.prototype.menu.text;

import cs.youtrade.autotrade.client.telegram.prototype.menu.text.base.YTPTextMenuState;
import cs.youtrade.autotrade.client.telegram.prototype.sender.text.UserTextMessageSender;
import cs.youtrade.autotrade.client.util.autotrade.ParamsCopyOptions;
import cs.youtrade.autotrade.client.util.autotrade.dto.user.params.FcdParamsGetDto;
import cs.youtrade.telegram.buttons.IMenuEnum;

import java.util.List;
import java.util.stream.Collectors;

public abstract class AbstractPcoTextMenuState<MENU_TYPE extends IMenuEnum> extends YTPTextMenuState<MENU_TYPE> {
    public AbstractPcoTextMenuState(UserTextMessageSender sender) {
        super(sender);
    }

    public String getFollowWorks(FcdParamsGetDto fcd) {
        String ans = fcd
                .getFollows()
                .stream()
                .filter(follow -> getMenuPcos()
                        .stream()
                        .anyMatch(pco ->
                                ParamsCopyOptions.isAncestor(pco, follow.getPco())))
                .map(follow -> String.format(
                        "🟢 Следование работает → <b>%s</b> (<code>%d</code>)",
                        follow.getPco().getModeName(),
                        follow.getId()
                ))
                .collect(Collectors.joining("\n"));

        return ans.isEmpty()
                ? "🔴 <b>Следование не работает</b>"
                : ans;
    }

    public abstract List<ParamsCopyOptions> getMenuPcos();
}
