package cs.youtrade.autotrade.client.util.autotrade.dto.user.accounts;

import cs.youtrade.autotrade.client.util.autotrade.dto.AbstractFcdDto;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public class BalanceMarketDto {
    public record QuoteInput(Long accountId, BigDecimal amount, String withdrawalMethod) { }

    @Data
    public static class CreateInput {
        private UUID quoteId;
        private Long accountId;
        private BigDecimal amount;
        private String withdrawalMethod;
        private String walletAddress;
        private String paymentPassword;
        private boolean walletConfirmed;
    }

    @Data
    public static class Quote {
        private UUID quoteId;
        private Long accountId;
        private String withdrawalMethod;
        private BigDecimal sourceAmount;
        private BigDecimal totalFee;
        private BigDecimal totalFeePercent;
        private BigDecimal networkFee;
        private BigDecimal estimatedPayout;
        private LocalDateTime rateUpdatedAt;
        private LocalDateTime expiresAt;
    }

    @Data
    public static class Session {
        private Long sessionId;
        private String status;
        private String stage;
        private String reason;
        private boolean canCancel;
        private Integer queueAhead;
        private Long accountId;
        private BigDecimal sourceAmount;
        private BigDecimal estimatedPayout;
        private String withdrawalMethod;
        private String walletAddress;
        private BigDecimal actualPayout;
        private String redBalanceOrderId;
        private String payoutTxid;
        private LocalDateTime completedAt;
        private LocalDateTime rateUpdatedAt;
        private LocalDateTime queuedAt;
        private LocalDateTime queueExpiresAt;
        private LocalDateTime acceptedAt;
        private LocalDateTime updatedAt;
    }

    @Data
    @EqualsAndHashCode(callSuper = true)
    public static class QuoteAnswer extends AbstractFcdDto {
        private Quote data;
    }

    @Data
    @EqualsAndHashCode(callSuper = true)
    public static class SessionAnswer extends AbstractFcdDto {
        private Session data;
        private String code;
        private String message;
    }

    @Data
    @EqualsAndHashCode(callSuper = true)
    public static class ActiveAnswer extends AbstractFcdDto {
        private List<Session> data;
    }
}
