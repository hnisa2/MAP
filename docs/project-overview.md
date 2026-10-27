# Technical project overview

## Problem and scope

Given a set of named cities, bidirectional roads, and fixed travel times, calculate a route with minimum total travel time between two selected cities. The application reads a UTF-8 text file and produces a text result.

The project is a Java implementation of a weighted graph problem. It models travel-time optimization on supplied data; it does not perform geographic routing or traffic prediction.

## Original course project

The supplied SE115 project report and local Java sources establish the original design:

- `City` stores each city's name and index.
- `CountryMap` reads the input and constructs an adjacency matrix.
- `WayFinder` uses Dijkstra's algorithm and predecessor tracking.
- Roads are bidirectional, with positive integer travel times.
- The documented example returns `A->C->E Total Time: 50 minutes`.

The report describes learning Dijkstra's algorithm, command-line arguments, file output, input validation, and exception handling. It mentions development beginning in December 2024; the repository itself was created in March 2025. Those are separate milestones.

## Portfolio revision

The repository's main branch had no current files when this revision began. The implementation was restored and refactored from the Java source files supplied alongside the report. Existing repository history is retained.

| Original limitation | Current behavior |
| --- | --- |
| An unreachable vertex could produce index -1 and crash | Stop when no reachable unvisited vertex remains |
| Accumulated time used 32-bit integers | Store totals in 64-bit `long` values |
| Some missing or nonnumeric road records escaped validation | Validate every required record and report its line |
| City names could be ambiguous or undeclared | Reject duplicates and unknown city references |
| Repeated road records depended on input order | Retain the minimum travel time |
| Algorithm wrote directly to a fixed output file | Return an immutable result; CLI owns output |
| Recursive path output depended on call-stack depth | Reconstruct the path iteratively |
| No repeatable test suite in the current repository | Deterministic regressions and independent oracle checks |

The original report is used as a reference rather than published verbatim. This overview keeps the technical content without including the report's student identifier or unrelated personal material.

## Algorithm

1. Initialize every distance to infinity, except the start city at zero.
2. Select the unvisited city with minimum known travel time.
3. Stop if there is no reachable unvisited city, or if the destination is selected.
4. Relax every existing road from the selected city.
5. Record the predecessor whenever a strictly shorter route is discovered.
6. Follow predecessors from the destination back to the start and reverse the path.

All road weights are positive, so selecting the nearest unvisited city finalizes that city's shortest distance. A same-city query returns the single city and zero minutes.

For equal-cost alternatives, strict relaxation retains the first predecessor discovered. The implementation returns one fastest route, not all equivalent routes.

## Complexity and tradeoffs

- Matrix storage and defensive copying: **O(V²)**.
- Linear nearest-city selection plus row scans: **O(V²)** time per query.
- Distance, predecessor, and visited arrays: **O(V)** auxiliary space.
- Input parsing: **O(V² + E)**, including matrix initialization.

The matrix makes the algorithm straightforward to inspect and compare with the original coursework. It is not a scalable representation for very large sparse networks. There is no benchmark claim in this project.

An edge is a positive Java `int`; a simple shortest path uses at most V - 1 edges. A `long` total prevents the overflow that occurs when valid individual road times sum above Integer.MAX_VALUE.

## Verification

The dependency-free test runner uses a fixed random seed and compares every city pair in 100 small random graphs against Floyd-Warshall. It also checks that each returned path starts and ends correctly, uses real roads, and sums to the reported time.

Regression cases cover disconnected graphs, an isolated vertex beside a valid route, reverse travel, same-city travel, no roads, repeated roads, whitespace, values beyond the 32-bit total range, malformed records, and CLI output handling.

CI is configured for Java 17 and 21 on Linux and Windows. Local validation used JDK 23 with `--release 17`; CI results are recorded by GitHub Actions.

## Possible next steps

- Replace the matrix with adjacency lists and a priority queue for sparse networks.
- Add multiple route queries without rereading the same map.
- Introduce a small interactive graph view while keeping calculation separate from presentation.
- Measure runtime and memory before making performance claims.
