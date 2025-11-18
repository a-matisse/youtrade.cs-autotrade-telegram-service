package cs.youtrade.autotrade.client.util.autotrade.dto.user.sell.parent;

import cs.youtrade.autotrade.client.util.autotrade.dto.AbstractFcdDto;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@EqualsAndHashCode(callSuper = true)
@Data
@NoArgsConstructor
public abstract class AbstrFcdSellGetFullCommand<T extends AbstrFcdSellGetSingleCommand<DTO>, DTO> extends AbstractFcdDto {
    private List<T> dtos;
    private Double fVolume;
    private Double fEarn;
    private Double fProfit;

    private AbstrFcdSellGetFullCommand(
            String cause,
            List<T> dtos,
            Double fEarn,
            Double fProfit
    ) {
        super(cause);
        this.dtos = dtos;
        this.fEarn = fEarn;
        this.fProfit = fProfit;
    }

    public AbstrFcdSellGetFullCommand(
            List<T> dtos
    ) {
        this(null, dtos, 0d, 0d);
    }

    public AbstrFcdSellGetFullCommand(
            String cause
    ) {
        this(cause, null, null, null);
    }

    public void setFields(
            Function<DTO, Double> sumMapper,
            Function<DTO, Double> fEarnMapper
    ) {
        this.fVolume = dtos
                .stream()
                .flatMap(e -> e.getOnSellList().stream())
                .mapToDouble(sumMapper::apply)
                .sum();

        this.fEarn = dtos
                .stream()
                .flatMap(e -> e.getOnSellList().stream())
                .mapToDouble(fEarnMapper::apply)
                .sum();

        this.fProfit = fEarn / fVolume;
    }

    public Map<String, List<DTO>> processToMap() {
        return dtos
                .stream()
                .collect(Collectors.toMap(
                        T::getTokenName,
                        T::getOnSellList
                ));
    }
}
