import java.util.List;

/** Immutable route calculation, independent of console or file output. */
public final class RouteResult {
    private final List<String> path;
    private final long totalMinutes;
    RouteResult(List<String> path, long totalMinutes) {
        this.path = List.copyOf(path);
        this.totalMinutes = totalMinutes;
    }
    public boolean isReachable() { return !path.isEmpty(); }
    public List<String> getPath() { return path; }
    /** Returns -1 when no route exists. */
    public long getTotalMinutes() { return totalMinutes; }
    public String format() {
        return isReachable()
            ? String.join("->", path) + " Total Time: " + totalMinutes + " minutes"
            : "No route found.";
    }
}
