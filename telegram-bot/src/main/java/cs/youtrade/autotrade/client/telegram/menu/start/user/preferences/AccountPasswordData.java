package cs.youtrade.autotrade.client.telegram.menu.start.user.preferences;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AccountPasswordData {
    private Boolean changing = false;
    private String currentPassword;
    private boolean currentProvided;
    private boolean saved;
    private volatile Integer responseStatus;

    public void eraseSecrets() { setCurrentPassword(null); }
}
