package cs.youtrade.autotrade.client.util.autotrade.dto.user.buy;

import cs.youtrade.autotrade.client.util.autotrade.YdpField;
import cs.youtrade.autotrade.client.util.autotrade.dto.AbstractFcdDto;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

@EqualsAndHashCode(callSuper = true)
@Data
@NoArgsConstructor
public class FcdScoringUpdateDto extends AbstractFcdDto {
    private Long ydpId;
    private YdpField field;
}
