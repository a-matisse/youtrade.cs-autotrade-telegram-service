package cs.youtrade.autotrade.client.telegram.menu.start.user.params.table.upload.stagep;

import cs.youtrade.autotrade.client.telegram.menu.UserMenu;
import cs.youtrade.autotrade.client.telegram.menu.start.user.params.table.upload.TableUploadRegistry;
import cs.youtrade.autotrade.client.telegram.prototype.data.UserData;
import cs.youtrade.autotrade.client.telegram.prototype.menu.text.AbstractTerminalTextMenuState;
import cs.youtrade.autotrade.client.telegram.prototype.sender.text.UserTextMessageSender;
import cs.youtrade.autotrade.client.util.autotrade.dto.user.sell.upload.FcdSellUploadInfoDto;
import cs.youtrade.autotrade.client.util.autotrade.endpoint.user.sell.SellUploadEndpoint;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.generics.TelegramClient;

import java.util.stream.Collectors;

@Service
public class TableUploadProceedState extends AbstractTerminalTextMenuState {
    private final TableUploadRegistry registry;
    private final SellUploadEndpoint endpoint;

    public TableUploadProceedState(
            UserTextMessageSender sender,
            TableUploadRegistry registry,
            SellUploadEndpoint endpoint
    ) {
        super(sender);
        this.registry = registry;
        this.endpoint = endpoint;
    }

    @Override
    public UserMenu supportedState() {
        return UserMenu.PORTFOLIO_UPLOAD_STAGE_P;
    }

    @Override
    public String getHeaderText(TelegramClient bot, UserData user) {
        long chatId = user.getChatId();
        var data = registry.remove(user);
        var restAns = endpoint.postUploadedItems(chatId, data.getDtos());
        if (restAns.getStatus() >= 300)
            return null;

        var fcd = restAns.getResponse();
        if (!fcd.isResult())
            return fcd.getCause();

        String ans = fcd
                .getData()
                .stream()
                .map(this::createPostMessageAns)
                .collect(Collectors.joining("\n"));

        return ans.isEmpty()
                ? "#0: Не найдены предметы к выставлению"
                : ans;
    }

    @Override
    public UserMenu retState() {
        return UserMenu.PORTFOLIO;
    }

    private String createPostMessageAns(FcdSellUploadInfoDto info) {
        return String.format("Токен [%s]: %d предметов выставлено", info.getTokenName(), info.getCount());
    }
}
