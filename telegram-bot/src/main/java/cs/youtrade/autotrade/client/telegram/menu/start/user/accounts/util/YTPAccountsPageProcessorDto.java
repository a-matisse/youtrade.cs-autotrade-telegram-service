package cs.youtrade.autotrade.client.telegram.menu.start.user.accounts.util;

import cs.youtrade.autotrade.client.telegram.localization.LocalizedTexts;
import cs.youtrade.autotrade.client.util.autotrade.dto.user.accounts.FcdAccountsV2Dto;
import cs.youtrade.autotrade.client.util.autotrade.dto.user.params.FcdParamsGetDto;
import cs.youtrade.autotrade.client.util.autotrade.util.accounts.FcdAccountV2Dto;
import cs.youtrade.autotrade.client.util.emoji.DynamicEmoji;

import java.util.Comparator;
import java.util.stream.Collectors;

public record YTPAccountsPageProcessorDto(
        FcdParamsGetDto params,
        FcdAccountsV2Dto fcd,
        UserAccountsMetaData pageData
) {
    public String getAccountsListStr(LocalizedTexts translations) {
        var accounts = fcd.getAccounts();
        if (accounts.isEmpty())
            return String.format("%s %s",
                    DynamicEmoji.ERROR.getEmoji(),
                    translations.get("the-account-list-is-empty"));

        return accounts
                .stream()
                .sorted(Comparator.comparingLong(FcdAccountV2Dto::getId))
                .map(account -> account.asMessage(pageData, translations.inBundle("summary.AccountsSummary")))
                .collect(Collectors.joining("\n"))
                .trim();
    }
}
