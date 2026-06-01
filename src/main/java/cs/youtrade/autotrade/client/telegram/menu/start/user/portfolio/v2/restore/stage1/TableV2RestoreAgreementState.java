package cs.youtrade.autotrade.client.telegram.menu.start.user.portfolio.v2.restore.stage1;

import cs.youtrade.autotrade.client.telegram.menu.UserMenu;
import cs.youtrade.autotrade.client.telegram.prototype.data.UserData;
import cs.youtrade.autotrade.client.telegram.prototype.menu.text.base.YTPTextMenuState;
import cs.youtrade.autotrade.client.util.emoji.DynamicEmoji;
import cs.youtrade.telegram.buttons.sender.text.BaseTextMessageSender;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.generics.TelegramClient;

@Service
public class TableV2RestoreAgreementState extends YTPTextMenuState<TableV2RestoreAgreementMenu> {
    public TableV2RestoreAgreementState(BaseTextMessageSender<UserData> sender) {
        super(sender);
    }

    @Override
    public UserMenu supportedState() {
        return UserMenu.PORTFOLIO_V2_RESTORE_STAGE_1;
    }

    @Override
    public TableV2RestoreAgreementMenu getOption(String optionStr) {
        return TableV2RestoreAgreementMenu.valueOf(optionStr);
    }

    @Override
    public TableV2RestoreAgreementMenu[] getOptions(UserData userData) {
        return TableV2RestoreAgreementMenu.values();
    }

    @Override
    public UserMenu executeCallback(TelegramClient bot, Update update, UserData userData, TableV2RestoreAgreementMenu t) {
        return switch (t) {
            case AGREE -> UserMenu.PORTFOLIO_V2_RESTORE_STAGE_P;
            case RETURN -> UserMenu.PORTFOLIO;
        };
    }

    @Override
    public String getHeaderText(TelegramClient bot, UserData userData) {
        return String.format("""
                        %s <b>Вы точно хотите запустить алгоритм восстановления</b>
                        <blockquote>• Все предметы, которые не добавлены в список исключений, могут быть выставлены
                        • Бот будет генерировать стоимость покупки предмета на основании истории покупок аккаунтов
                        • Если совпадения не будут найдены - бот пропустит предмет и не выставит его
                        • Нажимая "Согласен" вы подтверждаете согласие с алгоритмом выставления цен на торговой площадке продажи</blockquote>
                        """,
                DynamicEmoji.QUESTION.getEmoji()
        );
    }
}
