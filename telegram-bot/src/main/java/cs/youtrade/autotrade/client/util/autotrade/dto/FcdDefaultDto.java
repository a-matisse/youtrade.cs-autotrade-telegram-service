package cs.youtrade.autotrade.client.util.autotrade.dto;

import lombok.Getter;

@Getter
public class FcdDefaultDto<T> extends AbstractFcdDto {
    private final T data;

    private FcdDefaultDto(
            String cause,
            T data
    ) {
        super(cause);
        this.data = data;
    }

    public FcdDefaultDto(
            T data
    ) {
        this(null, data);
    }

    public FcdDefaultDto(
            String cause
    ) {
        this(cause, null);
    }
}
