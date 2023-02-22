// Based on: https://www.youtube.com/watch?v=t1-YZ6bF-g0 (Joe James)
// Practical: https://mediasite.bris.ac.uk/Mediasite/Play/86d3e96a6eb94e61a6a5bcf6bfc4996d1d
// Theory: https://mediasite.bris.ac.uk/Mediasite/Play/cd2af72137b8406d916a103477abf0571d

import java.lang.String;
import java.util.Arrays;
import java.util.stream.*;
import java.util.*;
import java.nio.file.*;
import java.io.IOException;


public class StreamsFunMSc {
    static void printAverageSquaredValuesLambdary() {
        Arrays.stream(new int[]{0, 1, 1, 2, 3, 5, 8, 13, 21})
                .map(x -> x * x)
                .average()
                .ifPresent(System.out::println);
    }

    static void printAverageSquaredValuesImperatively() {
        int[] numbers = new int[]{0, 1, 1, 2, 3, 5, 8, 13, 21};
        int currentTotal = 0;

        for (int i = 0; i < numbers.length; ++i) {
            currentTotal += numbers[i] * numbers[i];
        }
        System.out.println((double) currentTotal / (double) numbers.length);
    }

    static void processStringArrayLambdary() {

        String[] names = {"Sebastian", "Mutalib", "Tom", "Simon", "Chris", "Sion", "Silas", "Robert", "Tim", "Ethan"};
        Arrays.stream(names)
                .filter(x -> x.startsWith("S"))
                .sorted()
                .forEach(System.out::println);
    }

    static void processStringArrayImperatively() {
        String[] names = {"Sebastian", "Mutalib", "Tom", "Simon", "Chris", "Sion", "Silas", "Robert", "Tim", "Ethan"};
        ArrayList<String> namesStartS = new ArrayList<>();

        for (int i = 0; i < names.length; ++i) {
            if (names[i].startsWith("S")) {
                namesStartS.add(names[i]);
            }
        }
        Collections.sort(namesStartS);
        for (int i = 0; i < namesStartS.size(); ++i) {
            System.out.println(namesStartS.get(i));
        }
    }

    public static void main(String[] args) throws IOException{
        processStringArrayImperatively();
        System.out.println("--------------------------------------------");
        processStringArrayLambdary();
        System.out.println("--------------------------------------------");
        printAverageSquaredValuesImperatively();
        System.out.println("--------------------------------------------");
        printAverageSquaredValuesLambdary();

    }
}
