package cs.youtrade.autotrade.client.telegram.menu.start.user.quick.enable;

import cs.youtrade.autotrade.client.util.autotrade.QuickConfigGrade;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class QuickConfigCreateData {
    private BigDecimal preferredTradeCapital;
    private QuickConfigGrade buyGrade;
    private QuickConfigGrade sellGrade;
}
