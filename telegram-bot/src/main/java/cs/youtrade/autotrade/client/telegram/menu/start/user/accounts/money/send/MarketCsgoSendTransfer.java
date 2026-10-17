package cs.youtrade.autotrade.client.telegram.menu.start.user.accounts.money.send;

import cs.youtrade.autotrade.client.telegram.prototype.data.UserData;
import cs.youtrade.autotrade.client.util.autotrade.dto.user.accounts.FcdMarketCsgoMoneySendDto;
import cs.youtrade.autotrade.client.util.autotrade.endpoint.user.accounts.MarketCsgoBalanceEndpoint;
import cs.youtrade.autotrade.client.util.autotrade.endpoint.user.accounts.dto.FcdMarketCsgoMoneySendInput;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;

@Service
public class MarketCsgoSendTransfer {
    private final MarketCsgoBalanceEndpoint endpoint;
    private final BigDecimal testAmount;

    public MarketCsgoSendTransfer(MarketCsgoBalanceEndpoint endpoint,
                                  @Value("${yt.market-csgo.test-transfer-divisor}") long testAmountDivisor) {
        this.endpoint = endpoint;
        if (testAmountDivisor <= 0) throw new IllegalArgumentException("Invalid test transfer divisor");
        this.testAmount = BigDecimal.ONE.divide(BigDecimal.valueOf(testAmountDivisor), 3, RoundingMode.UNNECESSARY);
    }

    public BigDecimal testAmount() { return testAmount; }

    public boolean sendTest(UserData user, MarketCsgoSendData data) {
        FcdMarketCsgoMoneySendDto result = send(user, data, testAmount);
        data.setResponse(result);
        if (result == null || result.isOutcomeUnknown()) {
            data.setResult(MarketCsgoSendData.Result.TEST_UNKNOWN);
            data.eraseSecrets();
            return false;
        }
        if (!result.isResult()) {
            data.setResult(MarketCsgoSendData.Result.TEST_FAILED);
            data.eraseSecrets();
            return false;
        }
        return true;
    }

    public void sendRemaining(UserData user, MarketCsgoSendData data) {
        BigDecimal remaining = data.getAmount().subtract(testAmount);
        if (remaining.signum() == 0) {
            completed(data);
            return;
        }
        FcdMarketCsgoMoneySendDto result = send(user, data, remaining);
        data.setResponse(result);
        if (result == null || result.isOutcomeUnknown()) {
            data.setResult(MarketCsgoSendData.Result.REMAINDER_UNKNOWN);
        } else if (!result.isResult()) {
            data.setResult(MarketCsgoSendData.Result.REMAINDER_FAILED);
        } else completed(data);
        data.eraseSecrets();
    }

    private void completed(MarketCsgoSendData data) {
        data.setResult(MarketCsgoSendData.Result.COMPLETED);
        data.eraseSecrets();
    }

    private FcdMarketCsgoMoneySendDto send(UserData user, MarketCsgoSendData data, BigDecimal amount) {
        FcdMarketCsgoMoneySendInput input = new FcdMarketCsgoMoneySendInput();
        input.setSourceTokenId(data.getTokenId());
        input.setDestinationApiKey(data.getDestinationKey());
        input.setPaymentPassword(data.getPassword());
        input.setCurrency("USD");
        input.setAmount(amount);
        try {
            var answer = endpoint.moneySend(user.getChatId(), input);
            var response = answer.getResponse();
            return answer.getStatus() >= 300 && (response == null || response.isResult())
                    ? null : response;
        } catch (RuntimeException error) {
            return null;
        } finally {
            input.setDestinationApiKey(null);
            input.setPaymentPassword(null);
        }
    }

}
