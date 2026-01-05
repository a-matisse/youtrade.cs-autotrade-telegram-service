package cs.youtrade.autotrade.client.telegram.menu.start.user.params.table;

import java.io.File;
import java.io.IOException;

public interface ITableGenerator<I, O> {
    File createFile(I input) throws IOException;
    O handleFile(File file) throws IOException;
}
