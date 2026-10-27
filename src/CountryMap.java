import java.io.IOException;
import java.io.PrintStream;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

/** Command-line entry point: input file, optional output file. */
public final class CountryMap {
    private CountryMap() { }
    public static void main(String[] args) {
        System.exit(run(args, System.out, System.err));
    }
    static int run(String[] args, PrintStream out, PrintStream err) {
        if (args.length < 1 || args.length > 2) {
            err.println("Usage: java -cp build/classes CountryMap <input-file> [output-file]");
            return 1;
        }
        try {
            Path input = Path.of(args[0]).toAbsolutePath().normalize();
            Path output = Path.of(args.length == 2 ? args[1] : "output.txt").toAbsolutePath().normalize();
            if (input.equals(output) || (Files.exists(output) && Files.isSameFile(input, output))) {
                throw new IllegalArgumentException("Input and output must be different files.");
            }
            RouteResult result;
            try (Reader reader = Files.newBufferedReader(input, StandardCharsets.UTF_8)) {
                result = MapParser.parse(reader).solve();
            }
            Files.writeString(output, result.format() + System.lineSeparator(), StandardCharsets.UTF_8);
            out.println(result.format());
            out.println("Saved to " + output);
            return result.isReachable() ? 0 : 2;
        } catch (IOException | IllegalArgumentException e) {
            err.println("Error: " + e.getMessage());
            return 1;
        }
    }
}
