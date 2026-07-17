package cs.youtrade.autotrade.client.telegram.menu.start.user.accounts.util;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum UserAccountsMode {
    GENERAL("🔍", "Общий"),
    BUYER("📥", "Покупка"),
    SELLER("📤", "Продажа"),
    WORKER("🚚", "Воркер");

    private final String emoji;
    private final String russianName;
}
