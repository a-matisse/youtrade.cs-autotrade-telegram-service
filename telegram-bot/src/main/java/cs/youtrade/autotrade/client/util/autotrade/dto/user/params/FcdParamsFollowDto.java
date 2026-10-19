package cs.youtrade.autotrade.client.util.autotrade.dto.user.params;

import cs.youtrade.autotrade.client.telegram.localization.LocalizedTexts;
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

    public String asMessage(LocalizedTexts translations) {
        return String.format("• follow-ID=<code>%d</code> | params-ID=<code>%d</code> | %s: <b>%s</b>",
                id, yourId, translations.get("option"),
                translations.inBundle("parameters.follow.ParamsCopyOptions").enumText(pco, "mode-name"));
    }
}
