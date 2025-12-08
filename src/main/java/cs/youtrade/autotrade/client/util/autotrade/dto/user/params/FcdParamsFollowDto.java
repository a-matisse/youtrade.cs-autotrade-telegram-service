package cs.youtrade.autotrade.client.util.autotrade.dto.user.params;

import cs.youtrade.autotrade.client.util.autotrade.ParamsCopyOptions;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class FcdParamsFollowDto {
    private Long id;
    private Long theirId;
    private Long yourId;
    private ParamsCopyOptions pco;

    public String asMessage() {
        return String.format("🔗 follow-ID=<code>%d</code> | params-ID=%d | Опция: %s",
                id, yourId, pco.getModeName());
    }
}
