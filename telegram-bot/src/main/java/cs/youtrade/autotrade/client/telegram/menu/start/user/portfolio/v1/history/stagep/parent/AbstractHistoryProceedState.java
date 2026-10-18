package cs.youtrade.autotrade.client.telegram.menu.start.user.portfolio.v1.history.stagep.parent;

import cs.youtrade.autotrade.client.telegram.menu.UserMenu;
import cs.youtrade.autotrade.client.telegram.menu.start.user.portfolio.v1.history.stagep.generator.AbstractTableHistoryGenerator;
import cs.youtrade.autotrade.client.telegram.prototype.data.UserData;
import cs.youtrade.autotrade.client.telegram.prototype.menu.doc.YTPTerminalDocMenuState;
import cs.youtrade.autotrade.client.telegram.prototype.sender.doc.UserDocMessageSender;
import cs.youtrade.autotrade.client.util.autotrade.dto.user.sell.parent.AbstrFcdSellGetFullCommand;
import lombok.extern.log4j.Log4j2;
import org.telegram.telegrambots.meta.api.objects.InputFile;

import java.io.File;
import java.io.IOException;

@Log4j2
public abstract class AbstractHistoryProceedState<T extends AbstrFcdSellGetFullCommand<?, ?>> extends YTPTerminalDocMenuState<T> {
    protected final AbstractTableHistoryGenerator<T, ?> generator;

    public AbstractHistoryProceedState(
            UserDocMessageSender sender,
            AbstractTableHistoryGenerator<T, ?> generator
    ) {
        super(sender);
        this.generator = generator;
    }

    @Override
    public UserMenu retState() {
        return UserMenu.PORTFOLIO;
    }

    @Override
    public InputFile getHeaderDoc(UserData user, T content) {
        try {
            File output = generator.createFile(content);
            return new InputFile(output, "sell_history.xlsx");
        } catch (IOException e) {
            log.error("Ошибка загрузки файла истории", e);
            return null;
        }
    }
}
