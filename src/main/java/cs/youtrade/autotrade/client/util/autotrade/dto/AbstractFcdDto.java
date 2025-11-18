package cs.youtrade.autotrade.client.util.autotrade.dto;

import lombok.Getter;

import java.util.Objects;

@Getter
public abstract class AbstractFcdDto implements FcdDtoIfc {
    private final boolean result;
    private final String cause;

    public AbstractFcdDto() {
        this.result = true;
        this.cause = "";
    }

    public AbstractFcdDto(String cause) {
        if (Objects.isNull(cause)) {
            this.result = true;
            this.cause = "success";
        } else {
            this.result = false;
            this.cause = cause;
        }
    }
}
