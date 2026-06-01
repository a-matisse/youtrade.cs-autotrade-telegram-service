package cs.youtrade.autotrade.client.telegram.menu.start.user.portfolio.v1.history.stagep.parent;

import cs.youtrade.autotrade.client.telegram.menu.UserMenu;
import cs.youtrade.autotrade.client.telegram.menu.start.user.portfolio.v1.history.TableHistoryRegistry;
import cs.youtrade.autotrade.client.telegram.menu.start.user.portfolio.v1.history.stagep.generator.AbstractTableHistoryGenerator;
import cs.youtrade.autotrade.client.telegram.prototype.data.UserData;
import cs.youtrade.autotrade.client.telegram.prototype.menu.doc.YTPTerminalDocMenuState;
import cs.youtrade.autotrade.client.telegram.prototype.sender.doc.UserDocMessageSender;
import cs.youtrade.autotrade.client.util.autotrade.dto.user.sell.parent.AbstrFcdSellGetFullCommand;
import cs.youtrade.autotrade.client.util.autotrade.endpoint.user.sell.SellDefaultEndpoint;
import cs.youtrade.ytrest.RestAnswer;
import lombok.extern.log4j.Log4j2;
import org.telegram.telegrambots.meta.api.objects.InputFile;

import java.io.File;
import java.io.IOException;

@Log4j2
public abstract class AbstractHistoryProceedState<T extends AbstrFcdSellGetFullCommand<?, ?>> extends YTPTerminalDocMenuState<T> {
    protected final TableHistoryRegistry registry;
    protected final SellDefaultEndpoint endpoint;
    protected final AbstractTableHistoryGenerator<T, ?> generator;

    public AbstractHistoryProceedState(
            UserDocMessageSender sender,
            TableHistoryRegistry registry,
            SellDefaultEndpoint endpoint,
            AbstractTableHistoryGenerator<T, ?> generator
    ) {
        super(sender);
        this.registry = registry;
        this.endpoint = endpoint;
        this.generator = generator;
    }

    @Override
    public T getContent(UserData user) {
        var data = registry.remove(user);
        var restAns = getHistory(user.getChatId(), data.getPeriod());
        if (restAns.getStatus() >= 300)
            return null;

        var fcd = restAns.getResponse();
        if (!fcd.isResult())
            return null;

        return fcd;
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

    public abstract RestAnswer<T> getHistory(long chatId, int days);
}
