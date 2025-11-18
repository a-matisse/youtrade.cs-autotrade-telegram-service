package cs.youtrade.autotrade.client.util.autotrade.dto.user.sell.parent;

import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
public abstract class AbstrFcdSellGetSingleCommand<T> {
    private List<T> onSellList;
    private String tokenName;
    private Long tmTokenId;

    public AbstrFcdSellGetSingleCommand(
            List<T> onSellList,
            String tokenName,
            Long tmTokenId
    ) {
        this.onSellList = onSellList;
        this.tokenName = tokenName;
        this.tmTokenId = tmTokenId;
    }
}
