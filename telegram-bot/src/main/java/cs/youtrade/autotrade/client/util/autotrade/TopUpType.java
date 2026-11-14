package cs.youtrade.autotrade.client.util.autotrade;

public enum TopUpType {
    MANUAL("Manual"),
    HELEKET("Heleket"),
    TRYBIT("TryBit");

    private final String displayName;

    TopUpType(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
