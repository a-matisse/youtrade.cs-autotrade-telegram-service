package cs.youtrade.autotrade.client.telegram.menu.start.user.portfolio;

import cs.youtrade.autotrade.client.util.excel.generator.AbstractXlsxGenerator;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Locale;

public abstract class AbstractPortfolioXlsxGenerator extends AbstractXlsxGenerator {
    private static final String PERCENT_FORMAT = "0.00%;[Red]-0.00%";

    @Override
    protected String valueFormat(String label) {
        if (label.toLowerCase(Locale.ROOT).contains("%")) return PERCENT_FORMAT;
        return super.valueFormat(label);
    }

    protected BigDecimal percentPoints(BigDecimal value) {
        return value == null ? null : value.divide(BigDecimal.valueOf(100), 4, RoundingMode.HALF_UP);
    }
}
