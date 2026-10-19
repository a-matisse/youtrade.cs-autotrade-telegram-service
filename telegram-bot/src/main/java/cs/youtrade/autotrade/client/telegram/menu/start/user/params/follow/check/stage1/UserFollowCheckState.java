package cs.youtrade.autotrade.client.telegram.menu.start.user.params.follow.check.stage1;

import cs.youtrade.autotrade.client.telegram.localization.BotTexts;
import cs.youtrade.autotrade.client.telegram.localization.LocalizedTexts;
import java.util.Map;
import cs.youtrade.autotrade.client.telegram.menu.UserMenu;
import cs.youtrade.autotrade.client.telegram.menu.start.user.params.follow.check.UserFollowCheckData;
import cs.youtrade.autotrade.client.telegram.menu.start.user.params.follow.check.UserFollowCheckRegistry;
import cs.youtrade.autotrade.client.telegram.prototype.data.UserData;
import cs.youtrade.autotrade.client.telegram.prototype.menu.text.base.YTPTextMenuState;
import cs.youtrade.autotrade.client.telegram.prototype.sender.text.UserTextMessageSender;
import cs.youtrade.autotrade.client.util.autotrade.ParamsCopyOptions;
import cs.youtrade.autotrade.client.util.autotrade.dto.user.params.FcdParamsCopyReqDto;
import cs.youtrade.autotrade.client.util.emoji.DynamicEmoji;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.generics.TelegramClient;

@Service
public class UserFollowCheckState extends YTPTextMenuState<UserFollowCheckMenu> {
    private final UserFollowCheckRegistry registry;

    public UserFollowCheckState(
            UserTextMessageSender sender,
            UserFollowCheckRegistry registry,
            BotTexts botTexts
    ) {
        super(sender, botTexts);
        this.registry = registry;
    }

    @Override
    public UserMenu supportedState() {
        return UserMenu.FOLLOW_CHECK;
    }

    @Override
    public UserFollowCheckMenu getOption(String optionStr) {
        return UserFollowCheckMenu.valueOf(optionStr);
    }

    @Override
    public UserFollowCheckMenu[] getOptions(UserData userData) {
        return UserFollowCheckMenu.values();
    }

    @Override
    public UserMenu executeCallback(TelegramClient bot, Update update, UserData userData, UserFollowCheckMenu t) {
        return switch (t) {
            case ACCEPT -> UserMenu.FOLLOW_CHECK_ACCEPT;
            case DENY -> UserMenu.FOLLOW_CHECK_DENY;
            case RETURN -> UserMenu.FOLLOW;
        };
    }

    @Override
    public String getHeaderText(TelegramClient bot, UserData user) {
        var translations = botTexts.forUser(user, "parameters.follow.UserFollowCheck");
        var data = registry.get(user);
        if (data == null)
            return onEmptyMes(translations);

        return genMes(data, translations);
    }

    private String genMes(UserFollowCheckData dto, LocalizedTexts translations) {
        return switch (dto.getType()) {
            case FOLLOW -> followMes(dto.getDto(), dto.getPco(), translations);
            case COPY -> copyMes(dto.getDto(), dto.getPco(), translations);
        };
    }

    private String copyMes(
            FcdParamsCopyReqDto copyDto,
            ParamsCopyOptions pco,
            LocalizedTexts translations
    ) {
        return String.format("%s %s",
                DynamicEmoji.COPY.getEmoji(),
                translations.format("copy-request", Map.of(
                        "user", String.format("<b>%s</b>", copyDto.getThatUId()),
                        "operation", String.format("<b>%s</b>", translations.get("copying")),
                        "mode", translations.inBundle("parameters.follow.ParamsCopyOptions").enumText(pco, "mode-name"),
                        "parameters", String.format("<code>%s</code>", copyDto.getYourTdpName()))));
    }

    private String followMes(
            FcdParamsCopyReqDto copyDto,
            ParamsCopyOptions pco,
            LocalizedTexts translations
    ) {
        return String.format("%s %s",
                DynamicEmoji.FOLLOW.getEmoji(),
                translations.format("follow-request", Map.of(
                        "user", String.format("<b>%s</b>", copyDto.getThatUId()),
                        "operation", String.format("<b>%s</b>", translations.get("following")),
                        "mode", translations.inBundle("parameters.follow.ParamsCopyOptions").enumText(pco, "mode-name"),
                        "parameters", String.format("<code>%s</code>", copyDto.getYourTdpName()))));
    }

    private String onEmptyMes(LocalizedTexts translations) {
        return String.format("%s <i>%s</i>",
                DynamicEmoji.WAIT.getEmoji(),
                translations.get("no-requests-try-again-later"));
    }
}
