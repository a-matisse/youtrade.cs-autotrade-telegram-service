package cs.youtrade.autotrade.client.util.autotrade.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class WordDto {
    private Long id;
    private String keyWord;

    public String asText() {
        return String.format("[<code>%s</code>] %s", id, keyWord);
    }
}
