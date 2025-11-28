package cs.youtrade.autotrade.client.telegram.menu.start;

import cs.youtrade.autotrade.client.telegram.menu.UserMenu;
import cs.youtrade.autotrade.client.telegram.prototype.data.UserData;
import cs.youtrade.autotrade.client.telegram.prototype.menu.text.AbstractTerminalTextMenuState;
import cs.youtrade.autotrade.client.telegram.prototype.sender.text.UserTextMessageSender;
import org.springframework.stereotype.Service;

@Service
public class UserStartState extends AbstractTerminalTextMenuState {
    public UserStartState(
            UserTextMessageSender sender
    ) {
        super(sender);
    }

    @Override
    public UserMenu supportedState() {
        return UserMenu.START;
    }

    @Override
    public String getHeaderText(UserData userData) {
        return """
                👋 Добро пожаловать в YouTradeSg!
                
                🔐 YouTradeSg - Безопасное управление токенами
                
                Храните токены и делитесь доступом с друзьями!
                 • Ваши данные под защитой
                 • Гостевой доступ по запросу
                 • Прозрачный контроль просмотров
                
                Для работы с ботом требуется авторизация:
                 • Если вы здесь впервые или давно не заходили - введите пароль для доступа к вашему аккаунту.
                 • Если уже авторизованы - используйте /menu для получения актуального меню.
                
                📝 Пароль потребуется только один раз за сессию.
                """;
    }

    @Override
    public UserMenu retState() {
        return UserMenu.MAIN;
    }
}
