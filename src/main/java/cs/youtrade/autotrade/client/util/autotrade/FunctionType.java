package cs.youtrade.autotrade.client.util.autotrade;

import lombok.Getter;

@Getter
public enum FunctionType {
    LINEAR("Линейная"),
    EXPONENTIAL("Экспоненциальная"),
    LOGARITHMIC("Логарифмическая"),
    PREDICTIVE("Предиктивная"),
    NONE("Не задана");

    private final String russianName;

    private static final int MAX_DAYS = 8;
    private static final int COEFF_DIVISIONS = 1000;
    public static final double[][][] LINEAR_CACHE = new double[MAX_DAYS + 1][MAX_DAYS + 1][COEFF_DIVISIONS + 1];
    public static final double[][][] EXPONENTIAL_CACHE = new double[MAX_DAYS + 1][MAX_DAYS + 1][COEFF_DIVISIONS + 1];
    public static final double[][][] LOGARITHMIC_CACHE = new double[MAX_DAYS + 1][MAX_DAYS + 1][COEFF_DIVISIONS + 1];

    FunctionType(String russianName) {
        this.russianName = russianName;
    }

    static {
        for (int m = 0; m <= MAX_DAYS; m++) {
            for (int d = 0; d <= m; d++) {
                for (int c = 1; c <= COEFF_DIVISIONS; c++) {
                    double coefficient = c / (double) COEFF_DIVISIONS;
                    LINEAR_CACHE[d][m][c] = calculateYLinear(d, m, coefficient);
                    EXPONENTIAL_CACHE[d][m][c] = calculateYExponential(d, m, coefficient);
                    LOGARITHMIC_CACHE[d][m][c] = calculateYLogarithmic(d, m, coefficient);
                }
            }
        }
    }

    public static double getCachedValue(FunctionType type, double daysBeforeUnlock, double maxDays, double coefficient) {
        int d = Math.min(MAX_DAYS, (int) daysBeforeUnlock);
        int m = Math.min(MAX_DAYS, (int) maxDays);
        int c = Math.min(COEFF_DIVISIONS, (int) (coefficient * COEFF_DIVISIONS));

        return switch (type) {
            case LINEAR -> LINEAR_CACHE[d][m][c];
            case EXPONENTIAL -> EXPONENTIAL_CACHE[d][m][c];
            case LOGARITHMIC -> LOGARITHMIC_CACHE[d][m][c];
            default -> 1.0;
        };
    }

    private static double calculateBExponential(double maxDays, double coefficient) {
        return -Math.log(coefficient) / Math.sqrt(maxDays);
    }

    public static double calculateYExponential(double daysBeforeUnlock, double maxDays, double coefficient) {
        return Math.exp(-calculateBExponential(maxDays, coefficient) * Math.sqrt(daysBeforeUnlock));
    }

    private static double calculateBLinear(double maxDays, double coefficient) {
        return (1 - coefficient) / maxDays;
    }

    public static double calculateYLinear(double daysBeforeUnlock, double maxDays, double coefficient) {
        return 1 - calculateBLinear(maxDays, coefficient) * daysBeforeUnlock;
    }

    private static double calculateBLogarithmic(double maxDays, double coefficient) {
        return (1 / coefficient - 1) / (maxDays * maxDays * maxDays);
    }

    public static double calculateYLogarithmic(double daysBeforeUnlock, double maxDays, double coefficient) {
        return 1 / (1 + calculateBLogarithmic(maxDays, coefficient) * daysBeforeUnlock * daysBeforeUnlock * daysBeforeUnlock);
    }
}
