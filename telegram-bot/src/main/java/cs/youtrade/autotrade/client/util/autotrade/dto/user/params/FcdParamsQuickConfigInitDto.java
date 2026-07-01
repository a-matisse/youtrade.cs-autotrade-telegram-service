package cs.youtrade.autotrade.client.util.autotrade.dto.user.params;

import cs.youtrade.autotrade.client.util.autotrade.QuickConfigGrade;

import java.math.BigDecimal;

public record FcdParamsQuickConfigInitDto(
        BigDecimal preferredTradeCapital,
        QuickConfigGrade buyGrade,
        QuickConfigGrade sellGrade
) {
}
