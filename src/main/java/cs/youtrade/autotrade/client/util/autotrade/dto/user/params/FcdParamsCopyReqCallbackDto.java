package cs.youtrade.autotrade.client.util.autotrade.dto.user.params;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class FcdParamsCopyReqCallbackDto {
    private String confirm;
    private String decline;
}
