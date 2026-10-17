package cs.youtrade.autotrade.client.telegram.menu.start.user.accounts.money;

public final class MarketCsgoTransferFailureText {
    private MarketCsgoTransferFailureText() {
    }

    public static String format(String cause, Long failedAccountId) {
        if ((cause == null || cause.isBlank()) && failedAccountId == null) return "";

        String reason = cause == null || cause.isBlank() ? "" : String.format("""
                • Причина: <code>%s</code>
                """, escapeHtml(cause));
        String account = failedAccountId == null ? "" : String.format("""
                • Аккаунт: <code>%s</code>
                """, failedAccountId);
        return String.format("""

                <blockquote>%s%s</blockquote>
                """, reason, account.stripTrailing());
    }

    private static String escapeHtml(String value) {
        return value.replace("&", "&amp;").replace("<", "&lt;")
                .replace(">", "&gt;").replace("\"", "&quot;");
    }
}
