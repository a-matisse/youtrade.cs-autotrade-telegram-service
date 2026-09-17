package cs.youtrade.autotrade.client.telegram.menu.start.ref.transfer;

import java.math.BigDecimal;
import java.util.UUID;

public record RefTransferData(UUID requestId, BigDecimal amount) {
}
