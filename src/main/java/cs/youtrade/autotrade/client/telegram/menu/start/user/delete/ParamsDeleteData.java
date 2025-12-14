package cs.youtrade.autotrade.client.telegram.menu.start.user.delete;

import lombok.Setter;

@Setter
public class ParamsDeleteData {
    private Boolean decision;
    private String confirm;
    private String decline;

    public String getCallback() {
        return decision
                ? confirm
                : decline;
    }
}
