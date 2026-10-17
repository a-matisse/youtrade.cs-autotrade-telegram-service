package cs.youtrade.autotrade.client.telegram.prototype.data;

import cs.youtrade.autotrade.client.util.autotrade.dto.user.general.FcdGeneralAccInfoDto;
import cs.youtrade.telegram.buttons.data.AbstractUserData;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.time.ZoneOffset;

@Getter
public class UserData extends AbstractUserData {
    @Setter
    private UserLanguage language;
    private boolean qualified;
    @Setter
    private boolean accountExportPasswordSet;
    private boolean bargainAllowed;
    private String bargainAllowedUntil;
    @Setter
    private String blockedUntil;

    public UserData(
            Long chatId,
            FcdGeneralAccInfoDto accInfoDto
    ) {
        super(chatId);
        this.language = UserLanguage.fromStored(accInfoDto.getLanguageCode());
        this.qualified = accInfoDto.getQualified();
        this.accountExportPasswordSet = Boolean.TRUE.equals(accInfoDto.getAccountExportPasswordSet());
        this.bargainAllowed = accInfoDto.getBargainAllowed();
        this.bargainAllowedUntil = accInfoDto.getBargainAllowedUntil();
        this.blockedUntil = accInfoDto.getBlockedUntil();
    }

    public UserData(
            Long chatId
    ) {
        super(chatId);
        this.qualified = false;
        this.bargainAllowed = false;
    }

    public UserData updateQualified(FcdGeneralAccInfoDto accInfoDto) {
        UserLanguage savedLanguage = UserLanguage.fromStored(accInfoDto.getLanguageCode());
        if (savedLanguage != null) this.language = savedLanguage;
        this.qualified = accInfoDto.getQualified();
        this.accountExportPasswordSet = Boolean.TRUE.equals(accInfoDto.getAccountExportPasswordSet());
        this.bargainAllowed = accInfoDto.getBargainAllowed();
        this.bargainAllowedUntil = accInfoDto.getBargainAllowedUntil();
        this.blockedUntil = accInfoDto.getBlockedUntil();
        return this;
    }

    public boolean isBlocked() {
        if (blockedUntil == null || blockedUntil.isBlank()) return false;
        try {
            return LocalDateTime.parse(blockedUntil).toInstant(ZoneOffset.UTC).isAfter(java.time.Instant.now());
        } catch (java.time.format.DateTimeParseException e) {
            return false;
        }
    }

    public void setBargainAccess(boolean allowed, String until) {
        this.bargainAllowed = allowed;
        this.bargainAllowedUntil = until;
    }

    public UserLanguage effectiveLanguage() {
        return language == null ? UserLanguage.EN : language;
    }
}
