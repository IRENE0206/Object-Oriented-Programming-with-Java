# Java Lambdas 'n Streams Lab 

For this lab download and unzip `LambdasAndStreams.zip`. There is a video supporting this lab [here](https://mediasite.bris.ac.uk/Mediasite/Play/bb29c71713af4f47b4d8203249f808ed1d)

## Part 1 - Understanding code examples implemented with streams

For each of the code snippets below speculate on **what output might be produced** and note the further reading comments and questions at the end of each snippet

Try running `QuestionsInterpret.java` to confirm your thoughts

### 1) Integer stream
```Java 
IntStream
        .range(1, 10)
        .forEach(System.out::print);
System.out.println();

// Advanced: read up about method reference here: https://docs.oracle.com/javase/tutorial/java/javaOO/methodreferences.html
// What type of method reference is System.out::print

```
### 2) Integer stream with skip
```Java
IntStream
        .range(1, 10)
        .skip(5)
        .forEach(x -> System.out.println(x));
       
// Super deep. Consider the difference between the Lambda expression in the above forEach method and the method reference in the previous example
```

### 3) Stream rows from text file, sort, filter, and print
```Java
Stream<String> bands = Files.lines(Paths.get("bands.txt"));
bands
        .sorted()
        .filter(x -> x.length() > 13)
        .forEach(System.out::println);
 bands.close();
```
### 4) Stream rows from CSV file, store fields in HashMap
```Java
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
```
### 5) Reduction - sum
```Java
double total = Stream.of(7.3, 1.5, 4.8)
        .reduce(0.0, (Double a, Double b) -> a + b);
System.out.println("Total = " + total);
```
### 6) Reduction - summary statistics
```Java
IntSummaryStatistics summary = IntStream.of(7, 2, 19, 88, 73, 4, 10)
        .summaryStatistics();
System.out.println(summary);
```
## Part 2 - Writing your own Java code using streams

- Open the file `Questions.java` and review and run the imperatively coded methods:
	- `integerStreamWithSumImperatively()`
	- `streamOfSortedStringsFindFirstImperatively()`
    - `streamRowsTextfileAndInsertIntoListImperatively()`
    - `streamRowsTextfileAndInsertIntoList()`
    - `streamRowsFromCSVParseRowsImperatively()`
- Implement alternative versions of these methods which make use of streams and produce identical output. **Note that placeholder methods have been provided** for each of these methods which contain comments hinting on what methods you might want to use.
       
## Part 3 - Speaking OOP

Discuss the relevance of single abstract method interfaces to lambda functions in Java

<details>
    <summary>Hint</summary>

[Read this (old link but still informative)](http://cr.openjdk.java.net/~briangoetz/lambda/lambda-state-3.html)

</details>
