package cs.youtrade.autotrade.client.telegram.menu.start.user.accounts.add.stages.worker;

import cs.youtrade.autotrade.client.telegram.prototype.data.UserData;
import cs.youtrade.telegram.buttons.state.AbstractStateRegistry;
import org.springframework.stereotype.Component;

@Component
public class WorkerAddRegistry extends AbstractStateRegistry<UserData, WorkerAddData> {
}
