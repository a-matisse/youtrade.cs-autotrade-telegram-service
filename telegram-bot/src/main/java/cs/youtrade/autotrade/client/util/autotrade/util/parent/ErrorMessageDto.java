package cs.youtrade.autotrade.client.util.autotrade.util.parent;

import lombok.Data;

@Data
public abstract class ErrorMessageDto {
    private Integer errorCode;
    private String errorMessage;
}
