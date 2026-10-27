import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

/** Strict, line-aware reader for the SE115 Maps input format. */
public final class MapParser {
    private MapParser() { }
    public static final class ParsedMap {
        private final WayFinder wayFinder;
        private final String start;
        private final String end;
        private ParsedMap(WayFinder wayFinder, String start, String end) {
            this.wayFinder = wayFinder;
            this.start = start;
            this.end = end;
        }
        public RouteResult solve() { return wayFinder.findFastestRoute(start, end); }
    }

    public static ParsedMap parse(Reader source) throws IOException {
        BufferedReader reader = source instanceof BufferedReader
            ? (BufferedReader) source : new BufferedReader(source);
        int count = number(required(reader, 1), 1, "number of cities");
        if (count < 1) { throw error(1, "Number of cities must be positive."); }
        String[] names = required(reader, 2).split("\\s+");
        if (names.length != count) { throw error(2, "City count does not match declared names."); }
        City[] cities = new City[count];
        Map<String, Integer> indices = new HashMap<>();
        for (int i = 0; i < count; i++) {
            if (indices.put(names[i], i) != null) { throw error(2, "Duplicate city: " + names[i]); }
            cities[i] = new City(names[i], i);
        }
        int roads = number(required(reader, 3), 3, "number of roads");
        if (roads < 0) { throw error(3, "Number of roads cannot be negative."); }
        int[][] times = new int[count][count];
        for (int[] row : times) { Arrays.fill(row, -1); }
        int line = 3;
        for (int road = 0; road < roads; road++) {
            line++;
            String[] fields = required(reader, line).split("\\s+");
            if (fields.length != 3) { throw error(line, "Expected: City1 City2 Minutes."); }
            int from = cityIndex(indices, fields[0], line);
            int to = cityIndex(indices, fields[1], line);
            int minutes = number(fields[2], line, "travel time");
            if (minutes <= 0) { throw error(line, "Travel time must be positive."); }
            // Parallel roads are alternatives: retain the fastest in either direction.
            if (times[from][to] == -1 || minutes < times[from][to]) {
                times[from][to] = minutes;
                times[to][from] = minutes;
            }
        }
        line++;
        String[] query = required(reader, line).split("\\s+");
        if (query.length != 2) { throw error(line, "Expected start and destination cities."); }
        cityIndex(indices, query[0], line);
        cityIndex(indices, query[1], line);
        String extra;
        while ((extra = reader.readLine()) != null) {
            line++;
            if (!extra.isBlank()) { throw error(line, "Unexpected content after route query."); }
        }
        return new ParsedMap(new WayFinder(cities, times), query[0], query[1]);
    }

    private static String required(BufferedReader reader, int line) throws IOException {
        String value = reader.readLine();
        if (value == null || value.isBlank()) { throw error(line, "Missing required data."); }
        return value.trim();
    }
    private static int number(String value, int line, String field) {
        try { return Integer.parseInt(value); }
        catch (NumberFormatException e) { throw error(line, "Invalid " + field + ": " + value); }
    }
    private static int cityIndex(Map<String, Integer> indices, String name, int line) {
        Integer index = indices.get(name);
        if (index == null) { throw error(line, "Unknown city: " + name); }
        return index;
    }
    private static IllegalArgumentException error(int line, String message) {
        return new IllegalArgumentException("Line " + line + ": " + message);
    }
}
