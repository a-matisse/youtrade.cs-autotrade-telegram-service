package cs.youtrade.autotrade.client.util.autotrade;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class TdpCopyMainData {
    private final long thatChatId;
    private final long yourChatId;
    private final long thatTdpId;
    private final long yourTdpId;
    private final ParamsCopyOptions pco;
    private final boolean status;
}
