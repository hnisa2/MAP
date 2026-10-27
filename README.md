# MAP — Fastest Route Finder

This is a Java project I originally developed for my SE115 course. It reads cities and roads from a text file and uses Dijkstra's algorithm to find the fastest route between two cities.

The program prints the route and its total travel time, then saves the result to a text file.

## How it works

The map is represented as a graph:
- Each city is a vertex.
- Each road connects two cities in both directions.
- Road weights represent travel time in minutes.

Dijkstra's algorithm calculates the shortest travel time. The program then follows the previous cities to reconstruct the route.

## Project structure

- `City.java` — stores a city's name and index.
- `MapParser.java` — reads and validates the input.
- `WayFinder.java` — implements Dijkstra's algorithm.
- `RouteResult.java` — stores the calculated route and travel time.
- `CountryMap.java` — handles command-line arguments and file output.
- `tests/MapTests.java` — tests the algorithm, input validation, and file handling.

## Running the project

You need JDK 17 or newer.

Clone the repository:

```sh
git clone https://github.com/hnisa2/MAP.git
cd MAP
```

Compile the source files:

```sh
javac --release 17 -d build/classes src/City.java src/RouteResult.java src/WayFinder.java src/MapParser.java src/CountryMap.java
```

Run the example:

```sh
java -cp build/classes CountryMap examples/sample-map.txt
```

The result is saved to `output.txt`. You can also choose an output file:

```sh
java -cp build/classes CountryMap examples/sample-map.txt build/route.txt
```

## Example input

```text
5
A B C D E
6
A B 30
A C 10
B D 20
C D 50
C E 40
D E 10
A E
```

The input contains:
1. The number of cities.
2. The city names, separated by spaces.
3. The number of roads.
4. Each road in the format `City1 City2 TravelTime`.
5. The start and destination cities.

City names are case-sensitive, and travel times must be positive integers. If the same road appears more than once, the program uses the smallest travel time.

### Output

```text
A->C->E Total Time: 50 minutes
```

In this example, travelling from A to C takes 10 minutes and from C to E takes 40 minutes.

If the destination cannot be reached, the program outputs `No route found.` Invalid input produces an error message with the relevant line number.

## Tests

Compile and run the test suite:

```sh
javac --release 17 -Xlint:all -Werror -d build/classes src/City.java src/RouteResult.java src/WayFinder.java src/MapParser.java src/CountryMap.java tests/MapTests.java
java -cp build/classes MapTests
```

The tests cover disconnected cities, invalid input, repeated roads, large travel times, and file output. They also compare results for 100 randomly generated graphs with a separate Floyd-Warshall implementation.

GitHub Actions runs the tests on Windows and Linux with Java 17 and 21.

## What I learned

During the original project, I learned how to represent a graph with an adjacency matrix and implement Dijkstra's algorithm. I also practised reading and writing files, using command-line arguments, and handling invalid input.

One of the harder parts was reconstructing the route after finding the shortest travel time. Keeping track of the previous city helped me understand how the algorithm builds a path.

## Later improvements

The original version used three classes: `City`, `CountryMap`, and `WayFinder`. This version separates input parsing and route results into their own classes.

It also includes fixes for disconnected maps and integer overflow, along with examples and automated tests.

The algorithm uses an adjacency matrix, so its time and storage complexity are both O(V²). This is suitable for small maps; an adjacency list and priority queue would be a useful next step for larger networks.

---

Hayrünisa Toprak  
SE115 Course Project
