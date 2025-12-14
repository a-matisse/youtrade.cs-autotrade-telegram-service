package cs.youtrade.autotrade.client.telegram.menu.start.user.params.autosell.table.history.stagep.generator;

import cs.youtrade.autotrade.client.telegram.menu.start.user.params.autosell.table.ITableGenerator;
import cs.youtrade.autotrade.client.util.excel.XlsxExporter;
import cs.youtrade.autotrade.client.util.autotrade.dto.user.sell.history.FcdSellHistoryFullDto;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;

@Component
public class TableHistoryGenerator implements ITableGenerator<FcdSellHistoryFullDto, File> {
    @Override
    public File createFile(FcdSellHistoryFullDto input) throws IOException {
        return XlsxExporter.exportToXlsx(input.processToMap());
    }

    @Override
    public File handleFile(File file) throws IOException {
        return file;
    }
}
