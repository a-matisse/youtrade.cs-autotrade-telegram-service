package cs.youtrade.autotrade.client.util.autotrade.dto.admin;

import cs.youtrade.autotrade.client.util.autotrade.dto.AbstractFcdDto;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@EqualsAndHashCode(callSuper = true)
@Data
@NoArgsConstructor
public class FcdAdminUserRequestDto extends AbstractFcdDto {
    private Long chatId;
    private Long tdId;
}
