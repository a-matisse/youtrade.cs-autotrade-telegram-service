package cs.youtrade.autotrade.client.util.autotrade;

import cs.youtrade.autotrade.client.util.emoji.DynamicEmoji;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum MaFileStatus {
    ONLINE(DynamicEmoji.ON),
    CONNECTING(DynamicEmoji.ORANGE),
    OFFLINE(DynamicEmoji.OFF);

    private final DynamicEmoji emoji;
}
