package cs.youtrade.autotrade.client.util.autotrade.dto.user.params;

import cs.youtrade.autotrade.client.util.autotrade.QuickConfigGrade;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class FcdParamsQCData {
    private boolean exists;
    private QuickConfigGrade buyGrade;
    private QuickConfigGrade sellGrade;
}
