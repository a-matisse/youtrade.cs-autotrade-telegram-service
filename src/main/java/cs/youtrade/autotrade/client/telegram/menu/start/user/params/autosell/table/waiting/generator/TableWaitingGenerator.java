package cs.youtrade.autotrade.client.telegram.menu.start.user.params.autosell.table.waiting.generator;

import cs.youtrade.autotrade.client.telegram.menu.start.user.params.autosell.table.ITableGenerator;
import cs.youtrade.autotrade.client.util.excel.XlsxExporter;
import cs.youtrade.autotrade.client.util.autotrade.dto.user.sell.wait.FcdSellWaitFullDto;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;

@Component
public class TableWaitingGenerator implements ITableGenerator<FcdSellWaitFullDto, File> {
    @Override
    public File createFile(FcdSellWaitFullDto input) throws IOException {
        return XlsxExporter.exportToXlsx(input.processToMap());
    }

    @Override
    public File handleFile(File file) throws IOException {
        return file;
    }
}
