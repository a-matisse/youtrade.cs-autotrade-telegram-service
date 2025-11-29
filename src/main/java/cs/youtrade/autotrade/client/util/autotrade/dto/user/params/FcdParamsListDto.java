package cs.youtrade.autotrade.client.util.autotrade.dto.user.params;

import cs.youtrade.autotrade.client.util.autotrade.MarketType;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

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
                        🏷 ID=<code>%d</code>%s
                        %s → %s
                        """,
                tdpId,
                nameStr(),
                source,
                destination
        );
    }

    private String nameStr() {
        return givenName.equals("Не задано")
                ? ""
                : String.format("\n📛 Имя: <code>%s</code>", givenName);
    }
}
