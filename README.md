# SE115 Maps

This is a Java project I made for my SE115 course. It reads a map from a text file and finds the fastest route between two cities using Dijkstra's algorithm.

Roads work in both directions, and their travel times are given in minutes. The result is written to `output.txt`.

## Classes

- `City`: stores the name and index of a city.
- `CountryMap`: reads the input file, checks the data, and creates the map.
- `WayFinder`: finds the fastest route and writes the path and total time to a file.

The map is stored in a two-dimensional array. I used a previous-city array to keep track of the route, then a recursive method to write it in the correct order.

## How to run

You need a Java JDK installed. Run these commands from the project folder:

```sh
javac -d build src/City.java src/CountryMap.java src/WayFinder.java
java -cp build CountryMap examples/map1.txt
```

After running the program, open `output.txt` to see the result.

## Input format

The file contains the number of cities, their names, the number of roads, the roads and their travel times, and finally the start and end cities.

For example:

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

The output for this map is:

```text
A->C->E Total Time: 50 minutes
```

City names are case-sensitive and cannot contain spaces. Travel times must be positive whole numbers. If there is no route between the two cities, the program writes `No route found.` to the output file.

You can also try `examples/disconnected-map.txt` or `examples/invalid-map.txt`.

## What I learned

I initially tried to find the route without using an algorithm, but the code became complicated. Learning Dijkstra's algorithm helped me understand how to solve the problem step by step.

I also practised reading and writing files, taking the input filename from command-line arguments, and checking invalid input. Reconstructing the route from the previous-city array was one of the parts I needed to work on.

This version keeps the original three-class structure. Small fixes were made later for invalid input, disconnected maps, and travel-time totals that exceed the `int` range.

Hayrünisa Toprak  
SE115 Course Project
