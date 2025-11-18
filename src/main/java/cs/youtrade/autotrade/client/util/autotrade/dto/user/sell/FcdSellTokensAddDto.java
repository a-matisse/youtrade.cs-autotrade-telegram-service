package cs.youtrade.autotrade.client.util.autotrade.dto.user.sell;

import cs.youtrade.autotrade.client.util.autotrade.dto.AbstractFcdDto;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@EqualsAndHashCode(callSuper = true)
@Data
@NoArgsConstructor
public class FcdSellTokensAddDto extends AbstractFcdDto {
    private String visible;
    private String hidden;

    private FcdSellTokensAddDto(
            String cause,
            String visible,
            String hidden
    ) {
        super(cause);
        this.visible = visible;
        this.hidden = hidden;
    }

    public FcdSellTokensAddDto(
            String visible,
            String hidden
    ) {
        this(null, visible, hidden);
    }

    public FcdSellTokensAddDto(
            String cause
    ) {
        this(cause, null, null);
    }
}
