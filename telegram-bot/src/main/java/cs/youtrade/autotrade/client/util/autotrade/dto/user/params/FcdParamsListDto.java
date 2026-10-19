package cs.youtrade.autotrade.client.util.autotrade.dto.user.params;

import cs.youtrade.autotrade.client.telegram.localization.LocalizedTexts;
import cs.youtrade.autotrade.client.util.autotrade.MarketType;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

import static cs.youtrade.autotrade.client.util.autotrade.dto.user.params.FcdParamsGetDto.decideLink;
import static cs.youtrade.autotrade.client.util.autotrade.dto.user.params.FcdParamsGetDto.getDirection;

@Data
@NoArgsConstructor
public class FcdParamsListDto {
    private Long tdpId;
    private String givenName;
    private MarketType source;
    private MarketType destination;
    private BigDecimal balance;
    private BigDecimal referralBalance;

    public String asMessage(LocalizedTexts translations) {
        return String.format("""
                        🏷 ID=<code>%d</code> | %s: %s | %s
                        """,
                        tdpId, translations.get("name"),
                        nameStr(translations),
                        getDirection(source, destination));
    }

    private String nameStr(LocalizedTexts translations) {
        return givenName.equals("Не задано")
                ? translations.get("name-not-set")
                : String.format("<code>%s</code>", givenName);
    }
}
