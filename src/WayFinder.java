import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/** Dijkstra's algorithm with the original project's adjacency-matrix approach. */
public final class WayFinder {
    private final City[] cities;
    private final int[][] timeMatrix;
    private final Map<String, Integer> indices = new HashMap<>();

    public WayFinder(City[] cities, int[][] timeMatrix) {
        if (cities == null || timeMatrix == null || cities.length == 0
                || cities.length != timeMatrix.length) {
            throw new IllegalArgumentException("A non-empty square city matrix is required.");
        }
        this.cities = cities.clone();
        this.timeMatrix = new int[cities.length][];
        for (int i = 0; i < cities.length; i++) {
            if (cities[i] == null || cities[i].getIndex() != i
                    || indices.put(cities[i].getCityName(), i) != null) {
                throw new IllegalArgumentException("Cities need unique names and consecutive indices.");
            }
            if (timeMatrix[i] == null || timeMatrix[i].length != cities.length) {
                throw new IllegalArgumentException("Travel-time matrix must be square.");
            }
            this.timeMatrix[i] = timeMatrix[i].clone();
            for (int weight : timeMatrix[i]) {
                if (weight != -1 && weight <= 0) {
                    throw new IllegalArgumentException("Use positive minutes or -1 for absent roads.");
                }
            }
        }
        for (int i = 0; i < cities.length; i++) {
            for (int j = 0; j < i; j++) {
                if (timeMatrix[i][j] != timeMatrix[j][i]) {
                    throw new IllegalArgumentException("Roads must be bidirectional.");
                }
            }
        }
    }

    public RouteResult findFastestRoute(String startCity, String endCity) {
        Integer start = indices.get(startCity);
        Integer end = indices.get(endCity);
        if (start == null || end == null) {
            throw new IllegalArgumentException("Start and destination must be declared cities.");
        }
        long[] distance = new long[cities.length];
        int[] previous = new int[cities.length];
        boolean[] visited = new boolean[cities.length];
        Arrays.fill(distance, Long.MAX_VALUE);
        Arrays.fill(previous, -1);
        distance[start] = 0;

        for (int step = 0; step < cities.length; step++) {
            int current = -1;
            for (int i = 0; i < cities.length; i++) {
                if (!visited[i] && distance[i] != Long.MAX_VALUE
                        && (current == -1 || distance[i] < distance[current])) {
                    current = i;
                }
            }
            // No reachable, unvisited vertex remains in this connected component.
            if (current == -1 || current == end) { break; }
            visited[current] = true;
            for (int neighbor = 0; neighbor < cities.length; neighbor++) {
                int minutes = timeMatrix[current][neighbor];
                if (minutes > 0 && !visited[neighbor]) {
                    long candidate = distance[current] + minutes;
                    if (candidate < distance[neighbor]) {
                        distance[neighbor] = candidate;
                        previous[neighbor] = current;
                    }
                }
            }
        }
        if (distance[end] == Long.MAX_VALUE) { return new RouteResult(List.of(), -1); }
        List<String> path = new ArrayList<>();
        for (int city = end; city != -1; city = previous[city]) {
            path.add(cities[city].getCityName());
        }
        Collections.reverse(path);
        return new RouteResult(path, distance[end]);
    }
}
