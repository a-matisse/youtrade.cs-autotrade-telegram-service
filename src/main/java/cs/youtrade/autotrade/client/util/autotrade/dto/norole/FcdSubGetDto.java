package cs.youtrade.autotrade.client.util.autotrade.dto.norole;

import cs.youtrade.autotrade.client.util.autotrade.dto.AbstractFcdDto;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Data
@NoArgsConstructor
public class FcdSubGetDto extends AbstractFcdDto {
    private Long userTdId;
    private List<Long> adminChats;
}
