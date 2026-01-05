package cs.youtrade.autotrade.client.telegram.menu.start.user.params.deep.autobuy.words.add;

import cs.youtrade.autotrade.client.telegram.menu.start.user.params.deep.autobuy.words.WordsType;
import lombok.Data;

import java.util.List;

@Data
public class WordsAddData {
    private WordsType type;
    private List<String> keyWord;
}
