package cs.youtrade.autotrade.client.util.autotrade.dto;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class DeleteAnsDto {
    private final String givenName;
    private final double amount;
    private final int count;
}
