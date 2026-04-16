package cs.youtrade.autotrade.client.telegram.menu.start.user.token.add.choose;

import cs.youtrade.autotrade.client.telegram.menu.UserMenu;
import cs.youtrade.autotrade.client.telegram.menu.start.user.token.add.TokenChooseOption;
import cs.youtrade.autotrade.client.telegram.menu.start.user.token.add.UserTokenAddData;
import cs.youtrade.autotrade.client.telegram.menu.start.user.token.add.UserTokenAddRegistry;
import cs.youtrade.autotrade.client.telegram.prototype.data.UserData;
import cs.youtrade.autotrade.client.telegram.prototype.menu.text.base.YTPTextMenuState;
import cs.youtrade.autotrade.client.telegram.prototype.sender.text.UserTextMessageSender;
import cs.youtrade.autotrade.client.util.autotrade.MarketType;
import cs.youtrade.autotrade.client.util.autotrade.endpoint.user.params.ParamsEndpoint;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.generics.TelegramClient;

import java.util.List;
import java.util.stream.Collectors;

import static cs.youtrade.autotrade.client.util.autotrade.dto.user.params.FcdParamsGetDto.decideLink;

@Service
public class TokenAddChooseState extends YTPTextMenuState<TokenChooseOption> {
    private final UserTokenAddRegistry registry;
    private final ParamsEndpoint paramsEndpoint;

    public TokenAddChooseState(
            UserTextMessageSender sender,
            UserTokenAddRegistry registry,
            ParamsEndpoint paramsEndpoint
    ) {
        super(sender);
        this.registry = registry;
        this.paramsEndpoint = paramsEndpoint;
    }

    @Override
    public UserMenu supportedState() {
        return UserMenu.TOKEN_ADD_STAGE_CHOOSE;
    }

    @Override
    public TokenChooseOption getOption(String optionStr) {
        return TokenChooseOption.valueOf(optionStr);
    }

    @Override
    public TokenChooseOption[] getOptions(UserData userData) {
        return TokenChooseOption.values();
    }

    @Override
    public UserMenu executeCallback(TelegramClient bot, Update update, UserData user, TokenChooseOption t) {
        if (t.equals(TokenChooseOption.RETURN))
            return UserMenu.TOKEN;

        var data = registry.getOrCreate(user, UserTokenAddData::new);
        data.setOpt(t);
        return UserMenu.TOKEN_ADD_STAGE_1;
    }

    @Override
    public String getHeaderText(TelegramClient bot, UserData userData) {
        var restAns = paramsEndpoint.getCurrent(userData.getChatId());
        if (restAns.getStatus() >= 300)
            return null;

        var fcd = restAns.getResponse();
        if (!fcd.isResult())
            return fcd.getCause();

        var data = registry.getOrCreate(userData, UserTokenAddData::new);
        data.setDirection(fcd.getData());

        return String.format("""
                        🧭 <b>Назначение аккаунта</b>
                        ━━━━━━━━━━━━
                        <blockquote>• Направление покупки: <b><a href="%s">%s</a></b>
                        • Направление продажи: <b><a href="%s">%s</a></b></blockquote>
                        """,
                decideLink(data.getSource()), data.getSource().getMarketName(),
                decideLink(data.getDestination()), data.getDestination().getMarketName()
        );
    }
}
