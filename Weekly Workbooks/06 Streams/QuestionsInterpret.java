import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.IntSummaryStatistics;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public class QuestionsInterpret {
    public static void main(String[] args) throws IOException {
        // Integer Stream
        IntStream
                .range(1, 10)
                .forEach(System.out::print);
        System.out.println();
        // Predict what the output will look like.
        // Advanced: read up about method reference here: https://docs.oracle.com/javase/tutorial/java/javaOO/methodreferences.html
        // What type of method reference is System.out::print

        // --------------------------------------------

        // Integer Stream with skip
        IntStream
                .range(1, 10)
                .skip(5)
                .forEach(x -> System.out.println(x));
        // Predict what the output will look like
        // Super deep. Consider the difference between the Lambda expression in the above forEach method and the method reference in the previous example

        // --------------------------------------------

        // Stream rows from text file, sort, filter, and print
        Stream<String> bands = Files.lines(Paths.get("bands.txt"));
        bands
                .sorted()
                .filter(x -> x.length() > 13)
                .forEach(System.out::println);
        bands.close();

        System.out.println();

        // --------------------------------------------

        // Stream rows from CSV file, store fields in HashMap
        Stream<String> rows3 = Files.lines(Paths.get("data.txt"));
        Map<String, Integer> map = new HashMap<>();
        map = rows3
                .map(x -> x.split(","))
                .filter(x -> x.length == 3)
                .filter(x -> Integer.parseInt(x[1]) > 15)
                .collect(Collectors.toMap(
                        x -> x[0],
                        x -> Integer.parseInt(x[1])));
        rows3.close();
        for (String key : map.keySet()) {
            System.out.println(key + "  " + map.get(key));
        }

        // --------------------------------------------

        // Reduction - sum
        double total = Stream.of(7.3, 1.5, 4.8)
                .reduce(0.0, (Double a, Double b) -> a + b);
        System.out.println("Total = " + total);

        // Reduction - summary statistics
        IntSummaryStatistics summary = IntStream.of(7, 2, 19, 88, 73, 4, 10)
                .summaryStatistics();
        System.out.println(summary);
    }
}
