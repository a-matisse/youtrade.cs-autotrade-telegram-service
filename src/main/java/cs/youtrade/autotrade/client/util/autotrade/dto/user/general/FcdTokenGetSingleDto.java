package cs.youtrade.autotrade.client.util.autotrade.dto.user.general;

import cs.youtrade.autotrade.client.util.autotrade.dto.AbstractFcdDto;
import cs.youtrade.autotrade.client.util.emoji.DynamicEmoji;
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
                        %s ID=<code>%d</code> | %s | $%s | <b>S:</b> %s
                        """,
                DynamicEmoji.STEAM.getEmoji(),
                id,
                nameStr(),
                balance,
                sellIdMes()
        );
    }

    private String nameStr() {
        return name.equals("Не задано")
                ? String.format("<code>%s</code>", steamToken)
                : String.format("<code>%s</code> [<b>%s</b>]", steamToken, name);
    }

    private String sellIdMes() {
        return sellId != -1 ? DynamicEmoji.SUCCESS_2.getEmoji() : DynamicEmoji.ERROR.getEmoji();
    }
}
