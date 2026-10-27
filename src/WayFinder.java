import java.io.FileWriter;
import java.io.IOException;
public class WayFinder {
    private City[] cities;
    private int[][] timeMatrix;

    public WayFinder(City[] cities, int[][] timeMatrix){
        this.cities = cities;
        this.timeMatrix = timeMatrix;
    }

    private int getIndex(String cityName){
        for (int i=0 ; i<cities.length ; i++){
            if (cities[i].getCityName().equals(cityName)) return i;
        }
        return -1;
    }

    private int getMinDistance(long[] distances, boolean[] visited){
        long min = Long.MAX_VALUE;
        int closestIndex = -1;

        for (int i=0 ; i<cities.length ; i++){
            if (!visited[i] && distances[i]<min){
                min = distances[i];
                closestIndex = i;
            }
        }
        return closestIndex;
    }

    private void writeToFile(FileWriter wr, String output) {
        try{
            wr.write(output);
        }
        catch(IOException e){
            System.out.println("Error");
        }
    }

    public void printPath(FileWriter wr, int[] previous, int start, int end){
        if (end == -1){
            return;
        }
        printPath(wr, previous, start, previous[end]);
        try{
            if (end!=start){
                wr.write("->");
            }
            wr.write(cities[end].getCityName());
        }
        catch(IOException e){
            System.out.println("Error printing path stage");
        }
    }


    public void findFastestRoute (String startCity, String endCity){
        int startIndex = getIndex(startCity);
        int endIndex = getIndex(endCity);

        try (FileWriter wr = new FileWriter("output.txt")){
            if (startIndex == -1 || endIndex == -1) {
                System.out.println("Error. Invalid city.");
                return;
            }
            boolean[] visited = new boolean[cities.length];
            long[] distance = new long[cities.length]; //shortest
            int[] previous = new int[cities.length];

            for (int i = 0; i < cities.length; i++) {
                visited[i] = false;
                distance[i] = Long.MAX_VALUE;
                previous[i] = -1;
            }
            distance[startIndex] = 0;

            for (int i = 0; i < cities.length; i++) {
                int current = getMinDistance(distance, visited);
                if (current == -1) {
                    break; // No more reachable cities.
                }
                visited[current] = true;

                for (int n = 0; n < cities.length; n++) {
                    if (timeMatrix[current][n]!=-1 && !visited[n]&&distance[current]!=Long.MAX_VALUE){
                        long newDistance=distance[current]+timeMatrix[current][n];
                        if (newDistance < distance[n]) {
                            distance[n] = newDistance;
                            previous[n] = current;
                        }
                    }
                }
            }

            if (distance[endIndex] == Long.MAX_VALUE) {
                writeToFile(wr, "No route found.");
            } else {
                printPath(wr, previous, startIndex, endIndex);
                wr.write(" Total Time: " + distance[endIndex] + " minutes");
            }
            System.out.println("Check output.");
        }catch(IOException e){
            System.out.println("Error, structure");
        }
    }
}