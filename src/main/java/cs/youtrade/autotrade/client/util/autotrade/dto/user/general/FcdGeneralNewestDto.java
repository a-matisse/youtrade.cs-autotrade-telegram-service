package cs.youtrade.autotrade.client.util.autotrade.dto.user.general;

import cs.youtrade.autotrade.client.util.autotrade.dto.AbstractFcdDto;
import cs.youtrade.autotrade.client.util.autotrade.dto.ItemStatsSummaryDto;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.util.Collection;

@EqualsAndHashCode(callSuper = true)
@Data
@NoArgsConstructor
public class FcdGeneralNewestDto extends AbstractFcdDto {
    private Collection<Integer> periods;
    private Collection<ItemStatsSummaryDto> items;
}
