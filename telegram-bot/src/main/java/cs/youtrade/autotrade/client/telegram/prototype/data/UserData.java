package cs.youtrade.autotrade.client.telegram.prototype.data;

import cs.youtrade.autotrade.client.util.autotrade.dto.user.general.FcdGeneralAccInfoDto;
import cs.youtrade.telegram.buttons.data.AbstractUserData;
import lombok.Getter;

import java.time.LocalDateTime;
import java.time.ZoneOffset;

@Getter
public class UserData extends AbstractUserData {
    private boolean qualified;
    private boolean bargainAllowed;
    private String bargainAllowedUntil;
    private String blockedUntil;

    public UserData(
            Long chatId,
            FcdGeneralAccInfoDto accInfoDto
    ) {
        super(chatId);
        this.qualified = accInfoDto.getQualified();
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
        this.qualified = accInfoDto.getQualified();
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

    public void setBlockedUntil(String blockedUntil) {
        this.blockedUntil = blockedUntil;
    }

    public void setBargainAccess(boolean allowed, String until) {
        this.bargainAllowed = allowed;
        this.bargainAllowedUntil = until;
    }
}
