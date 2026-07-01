package cs.youtrade.autotrade.client.util.minio.dto;

import org.telegram.telegrambots.meta.api.objects.InputFile;

import java.io.InputStream;

public record MinIOInputStream(String fileName, InputStream inputStream) implements AutoCloseable {
    public InputFile getFile() {
        return new InputFile(inputStream, fileName);
    }

    @Override
    public void close() throws Exception {
        inputStream.close();
    }
}
