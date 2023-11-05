// DON'T VIEW UNTIL YOU HAVE ATTEMPTED QUESTIONS (examples based on this person's code)!! https://github.com/joeyajames/Java/tree/master/Java%208%20Streams
// https://docs.oracle.com/javase/8/docs/api/java/util/stream/Stream.html

import java.io.File;
import java.io.FileNotFoundException;
import java.lang.String;
import java.util.Arrays;
import java.util.List;
import java.util.stream.*;
import java.util.*;
import java.nio.file.*;
import java.io.IOException;


public class Questions {

    // ---------------------------------------------------------------------------
    public static void integerStreamWithSumImperatively() {
        // Write a single expression which makes use of IntStream and range to print the sum of the number 1 to 5.
        int sum = 0;
        for (int i = 1; i <= 5; ++i) {
            sum = sum + i;
        }
        System.out.println(sum);
    }
    public static void integerStreamWithSum() {
        // Hints: range, IntStream, sum
        System.out.println(IntStream.range(1, 6).sum());
    }

    // ---------------------------------------------------------------------------


    // ---------------------------------------------------------------------------
    public static void streamOfSortedStringsFindFirstImperatively() {
        // 4.5 Imperatively sorted, find first
        List<String> listNames = new ArrayList<>(Arrays.asList("Ava", "Aneri", "Alberto"));
        Collections.sort(listNames);
        System.out.println(listNames.get(0));
    }
    public static void streamOfSortedStringsFindFirst() {
        // Hints: ifPresent, sorted, findFirst, Stream.of
        Stream
                .of("Ava", "Aneri", "Alberto")
                .sorted()
                .findFirst()
                .ifPresent(System.out::println);
    }
    // ---------------------------------------------------------------------------



    // ---------------------------------------------------------------------------

    public static void streamRowsTextfileAndInsertIntoListImperatively() throws FileNotFoundException {
        Scanner scanner = new Scanner(new File("bands.txt"));
        List<String> listBands = new ArrayList<String>();
        while (scanner.hasNext()) {
            var v = scanner.nextLine();
            if (v.contains("jit")) {
                listBands.add(v);
            }
        }
        for (String s : listBands) {
            System.out.println(s);
        }
    }

    public static void streamRowsTextfileAndInsertIntoList() throws IOException {
        // Hints: Look at QuestionsInterpret.java for tips on reading file with stream
        // Hints: forEach, collect, filter
        Stream<String> bands = Files.lines(Paths.get("bands.txt"));
        bands
                .filter(x -> x.contains("jit"))
                .toList()
                .forEach(System.out::println);
        bands.close();

    }
    // ---------------------------------------------------------------------------


    // ---------------------------------------------------------------------------
    public static void streamRowsTextfileAndCountImperatively() throws FileNotFoundException {
        Scanner scanner = new Scanner(new File("data.txt"));
        int count = 0;
        while (scanner.hasNext()) {
            var v = scanner.nextLine().split(",");
            if (v.length == 3) {
                count++;
            }
        }
        System.out.println(count + " rows.");
        scanner.close();
    }
    public static void streamRowsTextfileAndCount() throws IOException {
        // Hints: count, filter, map
        Stream<String> rows3 = Files.lines(Paths.get("data.txt"));
        long count =
                rows3
                .map(x -> x.split(","))
                .filter(x -> x.length == 3)
                .count();
        System.out.println(count + " rows.");
        rows3.close();
    }
    // ---------------------------------------------------------------------------


    // ---------------------------------------------------------------------------
    public static void streamRowsFromCSVParseRowsImperatively() throws FileNotFoundException {
        Scanner scanner = new Scanner(new File("data.txt"));
        while (scanner.hasNext()) {
            var v = scanner.nextLine().split(",");
            if (v.length == 3 && Integer.parseInt(v[1]) > 15) {
                System.out.println(v[0] + "  " + v[1] + "  " + v[2]);
            }
        }
        scanner.close();
    }
    public static void streamRowsFromCSVParseRows() throws IOException {
        // Hints: forEach, filter, filter, map
        Stream<String> rows3 = Files.lines(Paths.get("data.txt"));
        rows3
                .map(x -> x.split(","))
                .filter(x -> x.length == 3)
                .filter(x -> Integer.parseInt(x[1]) > 15)
                .forEach(v -> System.out.println(v[0] + "  " + v[1] + "  " + v[2]));
        rows3.close();
    }
    // ---------------------------------------------------------------------------



    public static void main(String[] args) throws IOException, FileNotFoundException {
        // ---------------------------------------------------------------------------
        integerStreamWithSumImperatively();
        integerStreamWithSum();
        // ---------------------------------------------------------------------------
        streamOfSortedStringsFindFirstImperatively();
        streamOfSortedStringsFindFirst();
        // ---------------------------------------------------------------------------
        streamRowsTextfileAndInsertIntoListImperatively();
        streamRowsTextfileAndInsertIntoList();
        // ---------------------------------------------------------------------------
        streamRowsTextfileAndCountImperatively();
        streamRowsTextfileAndCount();
        // ---------------------------------------------------------------------------
        streamRowsFromCSVParseRowsImperatively();
        streamRowsFromCSVParseRows();
        // ---------------------------------------------------------------------------
    }
}