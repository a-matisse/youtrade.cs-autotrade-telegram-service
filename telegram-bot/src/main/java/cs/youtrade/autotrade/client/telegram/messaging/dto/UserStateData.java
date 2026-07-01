package cs.youtrade.autotrade.client.telegram.messaging.dto;

import cs.youtrade.autotrade.client.telegram.menu.UserMenu;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class UserStateData {
    private UserMenu menuState;
}
