package cs.youtrade.autotrade.client.util.autotrade.dto.user.params;

import cs.youtrade.autotrade.client.util.autotrade.dto.AbstractFcdDto;
import lombok.*;

@EqualsAndHashCode(callSuper = true)
@Data
@NoArgsConstructor
public class FcdParamsDeleteReqDto extends AbstractFcdDto {
    private String tdpGivenName;
    private Long tdpId;
    private String confirm;
    private String decline;
    private Boolean old;
}
