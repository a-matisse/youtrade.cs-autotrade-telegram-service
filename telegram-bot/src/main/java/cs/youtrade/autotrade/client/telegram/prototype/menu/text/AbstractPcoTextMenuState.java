package cs.youtrade.autotrade.client.telegram.prototype.menu.text;

import cs.youtrade.autotrade.client.telegram.localization.BotTexts;
import cs.youtrade.autotrade.client.telegram.localization.LocalizedTexts;
import cs.youtrade.autotrade.client.telegram.prototype.menu.text.base.YTPTextMenuState;
import cs.youtrade.autotrade.client.telegram.prototype.sender.text.UserTextMessageSender;
import cs.youtrade.autotrade.client.util.autotrade.ParamsCopyOptions;
import cs.youtrade.autotrade.client.util.autotrade.dto.user.params.FcdParamsGetDto;
import cs.youtrade.autotrade.client.util.emoji.DynamicEmoji;
import cs.youtrade.telegram.buttons.IMenuEnum;

import java.util.List;
import java.util.stream.Collectors;

public abstract class AbstractPcoTextMenuState<MENU_TYPE extends IMenuEnum> extends YTPTextMenuState<MENU_TYPE> {
    public AbstractPcoTextMenuState(UserTextMessageSender sender, BotTexts botTexts) {
        super(sender, botTexts);
    }

    public String getFollowWorks(FcdParamsGetDto fcd, LocalizedTexts translations) {
        String ans = fcd
                .getFollows()
                .stream()
                .filter(follow -> getMenuPcos()
                        .stream()
                        .anyMatch(pco ->
                                ParamsCopyOptions.isAncestor(pco, follow.getPco())))
                .map(follow -> String.format("%s %s → <b>%s</b> (<code>%d</code>)",
                                DynamicEmoji.ON.getEmoji(),
                                translations.get("following-is-active"),
                                translations.inBundle("parameters.follow.ParamsCopyOptions").enumText(follow.getPco(), "mode-name"),
                                follow.getId()))
                .collect(Collectors.joining("\n"));

        return ans.isEmpty()
                ? String.format("%s <b>%s</b>",
                        DynamicEmoji.OFF.getEmoji(),
                        translations.get("following-is-inactive"))
                : ans;
    }

    public abstract List<ParamsCopyOptions> getMenuPcos();
}
