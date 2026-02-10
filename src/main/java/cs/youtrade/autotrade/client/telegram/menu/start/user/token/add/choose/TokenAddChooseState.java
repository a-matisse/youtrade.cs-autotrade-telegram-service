package cs.youtrade.autotrade.client.telegram.menu.start.user.token.add.choose;

import cs.youtrade.autotrade.client.telegram.menu.UserMenu;
import cs.youtrade.autotrade.client.telegram.menu.start.user.token.add.TokenChooseOption;
import cs.youtrade.autotrade.client.telegram.menu.start.user.token.add.UserTokenAddData;
import cs.youtrade.autotrade.client.telegram.menu.start.user.token.add.UserTokenAddRegistry;
import cs.youtrade.autotrade.client.telegram.prototype.data.UserData;
import cs.youtrade.autotrade.client.telegram.prototype.menu.text.AbstractTextMenuState;
import cs.youtrade.autotrade.client.telegram.prototype.sender.text.UserTextMessageSender;
import cs.youtrade.autotrade.client.util.autotrade.MarketType;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.generics.TelegramClient;

import java.util.List;
import java.util.stream.Collectors;

import static cs.youtrade.autotrade.client.util.autotrade.MarketType.BUY_DIRS;
import static cs.youtrade.autotrade.client.util.autotrade.MarketType.SELL_DIRS;

@Service
public class TokenAddChooseState extends AbstractTextMenuState<TokenChooseOption> {
    private final UserTokenAddRegistry registry;

    public TokenAddChooseState(
            UserTextMessageSender sender,
            UserTokenAddRegistry registry
    ) {
        super(sender);
        this.registry = registry;
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
    public TokenChooseOption[] getOptions() {
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
        return String.format("""
                        🧭 <b>Назначение токена</b>
                        ━━━━━━━━━━━━
                        <blockquote>• <b>Направления покупки</b>: %s
                        • <b>Направления продажи</b>: %s</blockquote>
                        """,
                getDirs(BUY_DIRS),
                getDirs(SELL_DIRS)
        );
    }

    private String getDirs(List<MarketType> types) {
        return types.stream().map(MarketType::getMarketName).collect(Collectors.joining(", "));
    }
}
