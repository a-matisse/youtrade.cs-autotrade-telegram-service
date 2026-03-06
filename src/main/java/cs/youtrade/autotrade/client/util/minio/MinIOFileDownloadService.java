package cs.youtrade.autotrade.client.util.minio;

import cs.youtrade.autotrade.client.util.minio.dto.MinIODto;
import io.minio.GetObjectArgs;
import io.minio.MinioClient;
import io.minio.RemoveObjectArgs;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.InputFile;

import java.io.InputStream;

@Service
@Log4j2
public class MinIOFileDownloadService {
    private final MinioClient client;

    public MinIOFileDownloadService(
            @Value("${youtrade.minio.url}") String minIoUrl,
            @Value("${youtrade.minio.username}") String minIoUsername,
            @Value("${youtrade.minio.password}") String minIoPassword
    ) {
        this.client = MinioClient
                .builder()
                .endpoint(minIoUrl)
                .credentials(minIoUsername, minIoPassword)
                .build();
    }

    public InputFile fetchAndDeleteFile(MinIODto dto) {
        var file = getFile(dto);
        if (file != null) deleteFile(dto);
        return file;
    }

    public InputFile getFile(MinIODto dto) {
        try (InputStream stream = client.getObject(
                GetObjectArgs
                        .builder()
                        .bucket(dto.getBucket())
                        .object(dto.getFileName())
                        .build()
        )) {
            return new InputFile(stream, dto.getFileName());
        } catch (Exception e) {
            log.error("Error getting document from MinIO", e);
            return null;
        }
    }

    private void deleteFile(MinIODto dto) {
        try {
            client.removeObject(
                    RemoveObjectArgs
                            .builder()
                            .bucket(dto.getBucket())
                            .object(dto.getFileName())
                            .build()
            );
        } catch (Exception e) {
            log.error("Error deleting file from MinIO", e);
        }
    }
}
