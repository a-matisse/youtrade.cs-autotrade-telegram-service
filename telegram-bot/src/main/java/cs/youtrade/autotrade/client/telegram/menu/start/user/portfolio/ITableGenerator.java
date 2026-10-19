package cs.youtrade.autotrade.client.telegram.menu.start.user.portfolio;

import cs.youtrade.autotrade.client.telegram.localization.LocalizedTexts;
import java.io.File;
import java.io.IOException;

public interface ITableGenerator<I, O> {
    File createFile(I input, LocalizedTexts translations) throws IOException;
    O handleFile(File file, LocalizedTexts translations) throws IOException;
}
