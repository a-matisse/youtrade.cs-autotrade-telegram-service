package cs.youtrade.autotrade.client.telegram.menu.main.params.autobuy.words.add;

import cs.youtrade.autotrade.client.telegram.menu.main.params.autobuy.words.WordsType;
import lombok.Data;

import java.util.List;

@Data
public class WordsAddData {
    private WordsType type;
    private List<String> keyWord;
}
