package cs.youtrade.autotrade.client.telegram.menu.start.user.accounts.money;

final class MarketCsgoTransferFailureText {
    private MarketCsgoTransferFailureText() {
    }

    static String format(String cause, Long failedAccountId) {
        if ((cause == null || cause.isBlank()) && failedAccountId == null) return "";

        StringBuilder text = new StringBuilder("\n<blockquote>");
        if (cause != null && !cause.isBlank())
            text.append("• Причина: <code>").append(escapeHtml(cause)).append("</code>");
        if (failedAccountId != null) {
            if (text.length() > "\n<blockquote>".length()) text.append("\n");
            text.append("• Аккаунт: <code>").append(failedAccountId).append("</code>");
        }
        return text.append("</blockquote>").toString();
    }

    private static String escapeHtml(String value) {
        return value.replace("&", "&amp;").replace("<", "&lt;")
                .replace(">", "&gt;").replace("\"", "&quot;");
    }
}
