package cs.youtrade.autotrade.client.telegram.menu.start.user.accounts.money;

import cs.youtrade.autotrade.client.telegram.localization.LocalizedTexts;

public final class MarketCsgoTransferFailureText {
    private MarketCsgoTransferFailureText() {
    }

    public static String format(String cause, Long failedAccountId, LocalizedTexts translations) {
        if ((cause == null || cause.isBlank()) && failedAccountId == null) return "";

        String reason = cause == null || cause.isBlank() ? "" : String.format("""
                • %s: <code>%s</code>
                """,
                translations.get("reason"),
                escapeHtml(cause));
        String account = failedAccountId == null ? "" : String.format("""
                • %s: <code>%s</code>
                """,
                translations.get("account"),
                failedAccountId);
        return String.format("""

                <blockquote>%s%s</blockquote>
                """, reason, account.stripTrailing());
    }

    private static String escapeHtml(String value) {
        return value.replace("&", "&amp;").replace("<", "&lt;")
                .replace(">", "&gt;").replace("\"", "&quot;");
    }
}
