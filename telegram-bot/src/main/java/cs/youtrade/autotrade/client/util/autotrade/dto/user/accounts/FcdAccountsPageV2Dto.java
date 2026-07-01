package cs.youtrade.autotrade.client.util.autotrade.dto.user.accounts;

import cs.youtrade.autotrade.client.util.autotrade.util.accounts.FcdAccountV2Dto;
import lombok.Getter;
import org.jetbrains.annotations.NotNull;
import org.springframework.data.util.Streamable;

import java.util.Iterator;
import java.util.List;

@Getter
public class FcdAccountsPageV2Dto implements Streamable<FcdAccountV2Dto> {
    private List<FcdAccountV2Dto> content;
    private int number;
    private int size;
    private long totalElements;
    private int totalPages;
    private boolean last;
    private boolean first;

    public boolean hasNext() {
        return !last;
    }

    public boolean hasPrevious() {
        return !first;
    }

    @NotNull
    @Override
    public Iterator<FcdAccountV2Dto> iterator() {
        return content.iterator();
    }
}
