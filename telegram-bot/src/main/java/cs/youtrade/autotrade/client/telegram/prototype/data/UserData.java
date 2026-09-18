package cs.youtrade.autotrade.client.telegram.prototype.data;

import cs.youtrade.autotrade.client.util.autotrade.dto.user.general.FcdGeneralAccInfoDto;
import cs.youtrade.telegram.buttons.data.AbstractUserData;
import lombok.Getter;

@Getter
public class UserData extends AbstractUserData {
    private boolean qualified;
    private boolean bargainAllowed;

    public UserData(
            Long chatId,
            FcdGeneralAccInfoDto accInfoDto
    ) {
        super(chatId);
        this.qualified = accInfoDto.getQualified();
        this.bargainAllowed = accInfoDto.getBargainAllowed();
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
        return this;
    }
}
