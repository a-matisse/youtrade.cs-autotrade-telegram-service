package cs.youtrade.autotrade.client.telegram.prototype.data;

import cs.youtrade.autotrade.client.util.autotrade.dto.user.general.FcdGeneralAccInfoDto;
import cs.youtrade.telegram.buttons.data.AbstractUserData;
import lombok.Getter;
import lombok.Setter;

@Getter
public class UserData extends AbstractUserData {
    private boolean qualified;

    public UserData(
            Long chatId,
            FcdGeneralAccInfoDto accInfoDto
    ) {
        super(chatId);
        this.qualified = accInfoDto.getQualified();
    }

    public UserData(
            Long chatId
    ) {
        super(chatId);
        this.qualified = false;
    }

    public UserData updateQualified(FcdGeneralAccInfoDto accInfoDto) {
        this.qualified = accInfoDto.getQualified();
        return this;
    }
}
