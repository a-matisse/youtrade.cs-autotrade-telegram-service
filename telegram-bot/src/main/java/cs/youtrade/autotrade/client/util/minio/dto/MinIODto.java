package cs.youtrade.autotrade.client.util.minio.dto;

import lombok.Data;

@Data
public class MinIODto {
    private String bucket;
    private String fileName;
}
