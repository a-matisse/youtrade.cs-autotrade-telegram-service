package cs.youtrade.autotrade.client.util.autotrade.dto.user.sell.parent;

import com.google.gson.annotations.SerializedName;
import cs.youtrade.autotrade.client.util.autotrade.dto.AbstractFcdDto;
import cs.youtrade.autotrade.client.util.autotrade.dto.user.params.FcdParamsGetDto;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@EqualsAndHashCode(callSuper = true)
@Data
@NoArgsConstructor
public abstract class AbstrFcdSellGetFullCommand<T extends AbstrFcdSellGetSingleCommand<DTO>, DTO> extends AbstractFcdDto {
    private List<T> dtos;
    private Double turnover;
    private FcdParamsGetDto params;
    @SerializedName("fVolume")
    private Double fVolume;
    @SerializedName("fEarn")
    private Double fEarn;
    @SerializedName("fProfit")
    private Double fProfit;
    @SerializedName("fTotalProfit")
    private Double fTotalProfit;

    public Map<String, List<DTO>> processToMap() {
        return dtos
                .stream()
                .collect(Collectors.toMap(
                        T::getTokenName,
                        T::getOnSellList
                ));
    }
}
