import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.io.StringReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Random;

/** Dependency-free regression suite; throws on failure even without -ea. */
public final class MapTests {
    private static int checks;
    public static void main(String[] args) throws Exception {
        RouteResult sample = parse("5\nA B C D E\n6\nA B 30\nA C 10\nB D 20\nC D 50\nC E 40\nD E 10\nA E\n");
        check(sample.getPath().equals(List.of("A", "C", "E")), "sample path");
        check(sample.getTotalMinutes() == 50, "sample time");
        check(sample.format().equals("A->C->E Total Time: 50 minutes"), "sample format");
        check(parse("2\nA B\n1\nA B 12\nB A").getPath().equals(List.of("B", "A")), "bidirectional");
        check(!parse("3\nA B C\n1\nA B 2\nA C").isReachable(), "unreachable");
        check(parse("3\nA B C\n1\nA B 2\nA B").getTotalMinutes() == 2, "isolated vertex");
        check(parse("1\nA\n0\nA A").getTotalMinutes() == 0, "same city");
        check(!parse("2\nA B\n0\nA B").isReachable(), "no roads");
        check(parse("2\nA B\n2\nA B 4\nB A 20\nA B").getTotalMinutes() == 4, "parallel roads");
        check(parse(" 2 \n A\t B \n1\n A   B\t5 \n A B \n\n").getTotalMinutes() == 5, "whitespace");
        check(parse("3\nA B C\n2\nA B 2147483647\nB C 2147483647\nA C").getTotalMinutes()
                == 4294967294L, "long accumulated time");
        invalid("", 1);
        invalid("0\n", 1);
        invalid("-1\n", 1);
        invalid("x\n", 1);
        invalid("2\nA\n", 2);
        invalid("2\nA A\n", 2);
        invalid("1\nA\n-1\n", 3);
        invalid("1\nA\nx\n", 3);
        invalid("2\nA B\n1\n", 4);
        invalid("2\nA B\n1\nA B\n", 4);
        invalid("2\nA B\n1\nA X 2\n", 4);
        invalid("2\nA B\n1\nA B -2\n", 4);
        invalid("2\nA B\n1\nA B 0\n", 4);
        invalid("2\nA B\n1\nA B nope\n", 4);
        invalid("2\nA B\n1\nA B 2147483648\n", 4);
        invalid("2\nA B\n0\nA X\n", 4);
        invalid("2\nA B\n0\nA\n", 4);
        invalid("2\nA B\n0\nA B\nextra\n", 5);
        invalid("2\nA B\n\n0\nA B", 3);
        randomGraphs();
        cli();
        System.out.println("PASS: " + checks + " checks (including 100 seeded graphs against Floyd-Warshall).");
    }

    private static RouteResult parse(String input) throws Exception {
        return MapParser.parse(new StringReader(input)).solve();
    }
    private static void invalid(String input, int line) throws Exception {
        try {
            parse(input);
            throw new AssertionError("Expected invalid input on line " + line);
        } catch (IllegalArgumentException e) {
            check(e.getMessage().startsWith("Line " + line + ":"), e.getMessage());
        }
    }
    private static void randomGraphs() {
        Random random = new Random(115);
        final long infinity = Long.MAX_VALUE / 4;
        for (int trial = 0; trial < 100; trial++) {
            int n = 2 + random.nextInt(7);
            City[] cities = new City[n];
            int[][] weights = new int[n][n];
            long[][] oracle = new long[n][n];
            for (int i = 0; i < n; i++) {
                cities[i] = new City("C" + i, i);
                for (int j = 0; j < n; j++) {
                    weights[i][j] = -1;
                    oracle[i][j] = i == j ? 0 : infinity;
                }
            }
            for (int i = 0; i < n; i++) {
                for (int j = 0; j < i; j++) {
                    if (random.nextBoolean()) {
                        int minutes = 1 + random.nextInt(1000);
                        weights[i][j] = weights[j][i] = minutes;
                        oracle[i][j] = oracle[j][i] = minutes;
                    }
                }
            }
            for (int k = 0; k < n; k++)
                for (int i = 0; i < n; i++)
                    for (int j = 0; j < n; j++)
                        oracle[i][j] = Math.min(oracle[i][j], oracle[i][k] + oracle[k][j]);
            WayFinder finder = new WayFinder(cities, weights);
            for (int i = 0; i < n; i++) {
                for (int j = 0; j < n; j++) {
                    RouteResult actual = finder.findFastestRoute("C" + i, "C" + j);
                    check(actual.isReachable() == (oracle[i][j] < infinity), "oracle reachability");
                    if (!actual.isReachable()) { continue; }
                    check(actual.getTotalMinutes() == oracle[i][j], "oracle time");
                    List<String> path = actual.getPath();
                    check(path.get(0).equals("C" + i) && path.get(path.size() - 1).equals("C" + j),
                            "path endpoints");
                    long sum = 0;
                    for (int p = 1; p < path.size(); p++) {
                        int from = Integer.parseInt(path.get(p - 1).substring(1));
                        int to = Integer.parseInt(path.get(p).substring(1));
                        check(weights[from][to] > 0, "path uses existing roads");
                        sum += weights[from][to];
                    }
                    check(sum == actual.getTotalMinutes(), "path weight");
                }
            }
        }
    }
    private static void cli() throws Exception {
        Path dir = Files.createTempDirectory("map-tests-");
        Path input = dir.resolve("map.txt");
        Path output = dir.resolve("result.txt");
        ByteArrayOutputStream errors = new ByteArrayOutputStream();
        try (PrintStream sink = new PrintStream(new ByteArrayOutputStream());
             PrintStream err = new PrintStream(errors)) {
            check(CountryMap.run(new String[]{}, sink, err) == 1, "usage exit");
            check(CountryMap.run(new String[]{dir.resolve("missing.txt").toString()}, sink, err) == 1,
                    "missing file exit");
            Files.writeString(input, "2\nA B\n1\nA B 8\nA B", StandardCharsets.UTF_8);
            check(CountryMap.run(new String[]{input.toString(), output.toString()}, sink, err) == 0,
                    "success exit");
            check(Files.readString(output).trim().equals("A->B Total Time: 8 minutes"), "output file");
            String before = Files.readString(input);
            check(CountryMap.run(new String[]{input.toString(), input.toString()}, sink, err) == 1,
                    "overwrite protection");
            check(Files.readString(input).equals(before), "input retained");
            Files.writeString(input, "2\nA B\n0\nA B");
            check(CountryMap.run(new String[]{input.toString(), output.toString()}, sink, err) == 2,
                    "unreachable exit");
            check(Files.readString(output).trim().equals("No route found."), "unreachable output");
            Files.writeString(input, "bad input");
            Files.writeString(output, "previous result");
            check(CountryMap.run(new String[]{input.toString(), output.toString()}, sink, err) == 1,
                    "invalid exit");
            check(Files.readString(output).equals("previous result"), "invalid input leaves output intact");
            check(errors.toString(StandardCharsets.UTF_8).contains("Line 1:"), "CLI line diagnostics");
            Files.writeString(input, "1\nA\n0\nA A");
            check(CountryMap.run(new String[]{input.toString(), dir.toString()}, sink, err) == 1,
                    "unwritable output exit");
        } finally {
            Files.deleteIfExists(input);
            Files.deleteIfExists(output);
            Files.deleteIfExists(dir);
        }
    }
    private static void check(boolean condition, String message) {
        checks++;
        if (!condition) { throw new AssertionError(message); }
    }
}
