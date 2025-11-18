package cs.youtrade.autotrade.client.util.autotrade.endpoint.norole;

import cs.youtrade.autotrade.client.util.autotrade.endpoint.parent.AbstractAtNoRoleAddEndpoint;
import org.springframework.stereotype.Component;

@Component
public class NoRoleAdminAddEndpoint extends AbstractAtNoRoleAddEndpoint {
    @Override
    public String getMainEndpoint() {
        return "/api/telegram/no-role/admin";
    }
}
