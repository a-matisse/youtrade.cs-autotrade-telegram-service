package cs.youtrade.autotrade.client.telegram.menu.main.params.autosell.table.waiting;

import cs.youtrade.autotrade.client.telegram.menu.UserMenu;
import cs.youtrade.autotrade.client.telegram.menu.main.params.autosell.table.waiting.generator.TableWaitingGenerator;
import cs.youtrade.autotrade.client.telegram.prototype.data.UserData;
import cs.youtrade.autotrade.client.telegram.prototype.menu.doc.AbstractTerminalDocMenuState;
import cs.youtrade.autotrade.client.telegram.prototype.sender.doc.UserDocMessageSender;
import cs.youtrade.autotrade.client.util.autotrade.dto.user.sell.wait.FcdSellWaitFullDto;
import cs.youtrade.autotrade.client.util.autotrade.endpoint.user.sell.SellDefaultEndpoint;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.InputFile;
import org.telegram.telegrambots.meta.generics.TelegramClient;

import java.io.IOException;

@Service
@Log4j2
public class TableWaitingState extends AbstractTerminalDocMenuState<FcdSellWaitFullDto> {
    private final SellDefaultEndpoint endpoint;
    private final TableWaitingGenerator generator;

    public TableWaitingState(
            UserDocMessageSender sender,
            SellDefaultEndpoint endpoint,
            TableWaitingGenerator generator
    ) {
        super(sender);
        this.endpoint = endpoint;
        this.generator = generator;
    }

    @Override
    public UserMenu retState() {
        return UserMenu.TABLE;
    }

    @Override
    public FcdSellWaitFullDto getContent(UserData user) {
        long chatId = user.getChatId();
        var restAns = endpoint.getSellWaiting(chatId);
        if (restAns.getStatus() >= 300)
            return null;

        var fcd = restAns.getResponse();
        if (!fcd.isResult())
            return null;

        return fcd;
    }

    @Override
    public InputFile getHeaderDoc(UserData user, FcdSellWaitFullDto content) {
        try {
            return new InputFile(generator.createFile(content), "sell_listed.xlsx");
        } catch (IOException e) {
            log.error("Couldn't create table: {}", e.getMessage(), e);
            return null;
        }
    }

    @Override
    public String getHeaderDocText(UserData user, FcdSellWaitFullDto content) {
        return String.format("""
                        📦 Список ожидаемых предметов
                        🔢 Объем: $%.2f
                        💰 Прогнозируемый заработок: $%.2f
                        📈 Прогнозируемая прибыль: %.2f%%
                        """,
                content.getFVolume(),
                content.getFEarn(),
                content.getFProfit() * 100
        );
    }

    @Override
    public UserMenu supportedState() {
        return UserMenu.TABLE_WAITING;
    }

    @Override
    public String getHeaderText(TelegramClient bot, UserData userData) {
        return "📦 Список ожидаемых предметов";
    }
}
