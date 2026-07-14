package cs.youtrade.autotrade.client.telegram.menu.start.user.accounts.add.stages.worker.stage3;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import cs.youtrade.autotrade.client.telegram.menu.UserMenu;
import cs.youtrade.autotrade.client.telegram.menu.start.user.accounts.add.stages.worker.WorkerAddData;
import cs.youtrade.autotrade.client.telegram.menu.start.user.accounts.add.stages.worker.WorkerAddRegistry;
import cs.youtrade.autotrade.client.telegram.prototype.data.UserData;
import cs.youtrade.autotrade.client.telegram.prototype.menu.text.base.YTPTextMenuState;
import cs.youtrade.autotrade.client.telegram.prototype.sender.text.UserTextMessageSender;
import cs.youtrade.autotrade.client.util.emoji.DynamicEmoji;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.generics.TelegramClient;

import java.io.File;
import java.nio.file.Files;
import java.util.Map;

import static cs.youtrade.autotrade.client.telegram.menu.start.user.accounts.add.stages.worker.parent.AbstractWorkerAddState.getDefaultSteamWarning;
import static cs.youtrade.telegram.buttons.TelegramFileDownloader.downloadFile;

@Service
@Log4j2
public class WorkerAddMaFileState extends YTPTextMenuState<WorkerAddMaFileMenu> {
    private final WorkerAddRegistry registry;

    public WorkerAddMaFileState(
            UserTextMessageSender sender,
            WorkerAddRegistry registry
    ) {
        super(sender);
        this.registry = registry;
    }

    @Override
    public UserMenu supportedState() {
        return UserMenu.ACCOUNTS_ADD_STAGE_3_WORKER;
    }

    @Override
    public WorkerAddMaFileMenu getOption(String optionStr) {
        return WorkerAddMaFileMenu.valueOf(optionStr);
    }

    @Override
    public WorkerAddMaFileMenu[] getOptions(UserData userData) {
        return WorkerAddMaFileMenu.values();
    }

    @Override
    public UserMenu executeCallback(TelegramClient bot, Update update, UserData userData, WorkerAddMaFileMenu t) {
        return UserMenu.ACCOUNTS;
    }

    @Override
    public String getHeaderText(TelegramClient bot, UserData userData) {
        return String.format("""
                        %s
                        
                        %s Теперь <b>отправьте</b> в этот чат <b><a href="%s">maFile</a> от Steam-аккаунта</b>, который хотите добавить""",
                getDefaultSteamWarning(),
                DynamicEmoji.STEAM.getEmoji(),
                "https://youtu.be/29jLB9GmKE4?si=foaU45ol-Pw8_jrR"
        );
    }

    @Override
    public Map<WorkerAddMaFileMenu, String> getUrls(UserData userData) {
        return Map.of(
                WorkerAddMaFileMenu.GET_MAFILE, "https://youtu.be/29jLB9GmKE4?si=foaU45ol-Pw8_jrR"
        );
    }

    @Override
    public UserMenu onNoCallback(TelegramClient bot, Update update, UserData userData) {
        var data = registry.getOrCreate(userData, WorkerAddData::new);
        try {
            // 1. Проверка расширения файла
            String fileName = update.getMessage().getDocument().getFileName();
            String fileNameLowerCase = fileName.toLowerCase();
            if (!fileNameLowerCase.endsWith(".mafile") && !fileNameLowerCase.endsWith(".json")) {
                sender.sendTextMes(bot, userData, "#2: Неверный формат файла. Отправьте <b>.maFile</b> или <b>.json</b>.");
                return UserMenu.ACCOUNTS;
            }
            // 2. Проверка размера файла (maFile обычно до 10 КБ)
            long fileSize = update.getMessage().getDocument().getFileSize();
            if (fileSize > 50_000) { // больше 50 КБ — подозрительно
                sender.sendTextMes(bot, userData, "#3: Файл слишком большой для maFile.");
                return UserMenu.ACCOUNTS;
            }
            File tmp = downloadFile(bot, update.getMessage().getDocument());
            String maFileContent = Files.readString(tmp.toPath());
            // 3. Валидация JSON
            ObjectMapper mapper = new ObjectMapper();
            JsonNode json;
            try {
                json = mapper.readTree(maFileContent);
            } catch (Exception e) {
                sender.sendTextMes(bot, userData, "#4: Файл не является валидным JSON.");
                return UserMenu.ACCOUNTS;
            }
            // 4. Проверка обязательных полей maFile
            if (!json.has("shared_secret")
                || !json.has("identity_secret")) {
                sender.sendTextMes(bot, userData, "#5: Файл не похож на maFile Steam-аккаунта.");
                return UserMenu.ACCOUNTS;
            }
            // 5. Проверка, что секреты — base64-строки
            String sharedSecret = json.get("shared_secret").asText();
            if (!sharedSecret.matches("^[A-Za-z0-9+/=]{20,40}$")) {
                sender.sendTextMes(bot, userData, "#6: Некорректный shared_secret.");
                return UserMenu.ACCOUNTS;
            }
            // 6. Сохранение maFile
            data.setMaFile(maFileContent);
            // 7. Переход к отправке на сервер данных
            return UserMenu.ACCOUNTS_ADD_STAGE_P_WORKER;
        } catch (Exception e) {
            log.error("Ошибка загрузки maFile", e);
            sender.sendTextMes(bot, userData, String.format("#1: Не удалось загрузить maFile от Steam-аккаунта <b>%s</b>.", data.getLogin()));
            return UserMenu.ACCOUNTS;
        } finally {
            sender.deleteMes(bot, userData, () -> update.getMessage().getMessageId(), null);
        }
    }
}
