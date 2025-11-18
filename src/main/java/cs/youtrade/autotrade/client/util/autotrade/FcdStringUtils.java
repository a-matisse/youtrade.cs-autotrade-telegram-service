package cs.youtrade.autotrade.client.util.autotrade;

import org.apache.commons.text.similarity.LevenshteinDistance;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Stream;

public class FcdStringUtils {
    private static final LevenshteinDistance lDist = LevenshteinDistance.getDefaultInstance();

    public static <T extends FcdDistance> T findClosest(List<T> inputs, String input) {
        return findClosest(inputs, input, Integer.MAX_VALUE);
    }

    public static <T extends FcdDistance> T findClosest(List<T> inputs, String input, int threshold) {
        return inputs
                .stream()
                .filter(t -> getDist(t.name(), input, threshold) >= 0)
                .min(Comparator.comparingInt(t -> getDist(t.name(), input, threshold)))
                .orElse(null);
    }

    public static <T extends FcdDistance> T findClosest(T[] inputs, String input) {
        return findClosest(inputs, input, Integer.MAX_VALUE);
    }

    public static <T extends FcdDistance> T findClosest(T[] inputs, String input, int threshold) {
        return Stream.of(inputs)
                .filter(t -> getDist(t.name(), input, threshold) >= 0)
                .min(Comparator.comparingInt(t -> getDist(t.name(), input, threshold)))
                .orElse(null);
    }

    public static String transliterate(String input) {
        String[][] table = {
                {"А", "A"}, {"Б", "B"}, {"В", "V"}, {"Г", "G"}, {"Д", "D"},
                {"Е", "E"}, {"Ё", "E"}, {"Ж", "ZH"}, {"З", "Z"}, {"И", "I"},
                {"Й", "I"}, {"К", "K"}, {"Л", "L"}, {"М", "M"}, {"Н", "N"},
                {"О", "O"}, {"П", "P"}, {"Р", "R"}, {"С", "S"}, {"Т", "T"},
                {"У", "U"}, {"Ф", "F"}, {"Х", "KH"}, {"Ц", "TS"}, {"Ч", "CH"},
                {"Ш", "SH"}, {"Щ", "SHCH"}, {"Ы", "Y"}, {"Э", "E"}, {"Ю", "YU"}, {"Я", "YA"}
        };
        String result = input.toUpperCase();
        for (String[] pair : table) {
            result = result.replace(pair[0], pair[1]);
        }
        return result;
    }

    public static int getDist(String input1, String input2, int threshold) {
        if (input1 == null || input2 == null)
            return -1;

        int dist = lDist.apply(input1.toUpperCase(), input2.toUpperCase());
        if (dist > threshold)
            return -1;

        return dist;
    }
}
