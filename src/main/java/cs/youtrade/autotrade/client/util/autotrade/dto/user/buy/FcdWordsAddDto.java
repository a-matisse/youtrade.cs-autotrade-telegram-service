package cs.youtrade.autotrade.client.util.autotrade.dto.user.buy;

import cs.youtrade.autotrade.client.util.autotrade.dto.AbstractFcdDto;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Data
@NoArgsConstructor
public class FcdWordsAddDto extends AbstractFcdDto {
    private List<String> added;
    private List<String> skipped;
}
