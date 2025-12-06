package cs.youtrade.autotrade.client.util.autotrade.dto.user.buy;

import cs.youtrade.autotrade.client.util.autotrade.YdpField;
import cs.youtrade.autotrade.client.util.autotrade.dto.AbstractFcdDto;
import lombok.Getter;

@Getter
public class FcdProfitUpdateDto extends AbstractFcdDto {
    private Long ydpId;
    private YdpField field;
}
