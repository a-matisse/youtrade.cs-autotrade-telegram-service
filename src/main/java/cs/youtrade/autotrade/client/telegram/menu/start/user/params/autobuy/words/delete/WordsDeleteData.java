package cs.youtrade.autotrade.client.telegram.menu.start.user.params.autobuy.words.delete;

import cs.youtrade.autotrade.client.telegram.menu.start.user.params.autobuy.words.WordsType;
import lombok.Data;

import java.util.List;

@Data
public class WordsDeleteData {
    private WordsType type;
    private List<Long> ids;
}
