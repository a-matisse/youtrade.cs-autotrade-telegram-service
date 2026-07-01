package cs.youtrade.autotrade.client.util.autotrade.dto.user.params;

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

    public String asMessage() {
        return String.format("""
                        🏷 ID=<code>%d</code> | Имя: %s | %s
                        """,
                tdpId,
                nameStr(),
                getDirection(source, destination)
        );
    }

    private String nameStr() {
        return givenName.equals("Не задано")
                ? givenName
                : String.format("<code>%s</code>", givenName);
    }
}
