package cs.youtrade.autotrade.client.util.autotrade.dto.user.general;

import cs.youtrade.autotrade.client.util.autotrade.dto.AbstractFcdDto;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@EqualsAndHashCode(callSuper = true)
@Data
@NoArgsConstructor
public class FcdTokenGetSingleDto extends AbstractFcdDto {
    private Long id;
    private String name;
    private String steamToken;
    private Double balance;
    private Long sellId;

    public String asMessage() {
        return String.format("""
                        🏷 Buy-ID: %d
                        🏷 Имя: %s
                        🔑 Токен: %s
                        🔗 Привязанный Sell-ID: %s
                        💰 Баланс: %s
                        """,
                id,
                name,
                steamToken,
                sellIdMes(),
                balance
        );
    }

    private String sellIdMes() {
        return sellId != -1
                ? sellId.toString()
                : "Не привязан";
    }
}
