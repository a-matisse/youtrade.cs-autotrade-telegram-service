package cs.youtrade.autotrade.client.util.autotrade.util.accounts;

import cs.youtrade.autotrade.client.util.autotrade.MaFileStatus;
import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class WorkerData {
    MaFileStatus receiveStatus;
    MaFileStatus withdrawStatus;
}
