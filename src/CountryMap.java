import java.io.File;
import java.io.IOException;
import java.util.Scanner;
import java.util.NoSuchElementException;
public class CountryMap {
    public static void main(String[] args) {
        if (args.length != 1) {
            System.out.println("Usage: java CountryMap <input-file>");
            return;
        }

        String filename = args[0];
        File file = new File(filename);

        try (Scanner sc = new Scanner(file, "UTF-8")) {
            if (!sc.hasNextLine()) {
                System.out.println("Error line 1 Reason: Missing number of cities.");
                return;
            }
            int numCities;
            try {
                numCities = Integer.parseInt(sc.nextLine().trim());
            } catch (NumberFormatException e) {
                System.out.println("Error Line 1 Reason: Invalid number of cities.");
                return;
            }

            if (!sc.hasNextLine()) {
                System.out.println("Error Missing city names.");
                return;
            }
            String[] cityNames = sc.nextLine().trim().split("\\s+");
            if (numCities <= 0 || cityNames.length != numCities || cityNames[0].isEmpty()) {
                System.out.println("Error Line 2. Reason: Number of cities does not match.");
                return;
            }

            City[] cities = new City[numCities];
            for (int i = 0; i < numCities; i++) {
                for (int j = 0; j < i; j++) {
                    if (cityNames[i].equals(cityNames[j])) {
                        System.out.println("Error line 2: Duplicate city name.");
                        return;
                    }
                }
                cities[i] = new City(cityNames[i], i);
            }
            if (!sc.hasNextLine()) {
                System.out.println("Error Line 3 Missing number of routes.");
                return;
            }

            int numRoutes;
            try{
                numRoutes=Integer.parseInt(sc.nextLine().trim());
            }catch(NumberFormatException e){
                System.out.println("Error Line 3 Invalid number of routes.");
                return;
            }

            if (numRoutes < 0) {
                System.out.println("Error line 3: Number of routes cannot be negative.");
                return;
            }

            int[][] timeMatrix = new int[numCities][numCities];
            for (int i = 0; i < numCities; i++) {
                for (int j = 0; j < numCities; j++) {
                    timeMatrix[i][j] = -1;
                }
            }
            for (int i = 0; i < numRoutes; i++) {
                if (!sc.hasNextLine()) {
                    System.out.println("Error line " + (i+4) + ": Missing route.");
                    return;
                }
                String[] routeInfo = sc.nextLine().trim().split("\\s+");
                if (routeInfo.length!= 3) {
                    System.out.println("Error line " + (i+4) + ": Expected City1 City2 Time.");
                    return;
                }
                String city1 = routeInfo[0];
                String city2 = routeInfo[1];
                int time;
                try {
                    time = Integer.parseInt(routeInfo[2]);
                } catch (NumberFormatException e) {
                    System.out.println("Error line " + (i+4) + ": Invalid travel time.");
                    return;
                }
                if (time<=0){
                    System.out.println("Error line "+(i+4)+" Invalid time");
                    return;
                }

                int city1Index = getIndex(cities, city1);
                int city2Index = getIndex(cities, city2);

                if (city1Index == -1 || city2Index == -1) {
                    System.out.println("Error line "+(i+4)+" Reason: Invalid city name. Unspecified city is first mentioned in here");
                    return;
                }
                if (timeMatrix[city1Index][city2Index] == -1 ||
                        time < timeMatrix[city1Index][city2Index]) {
                    timeMatrix[city1Index][city2Index] = time;
                    timeMatrix[city2Index][city1Index] = time;
                }
            }

            if (!sc.hasNextLine()) { //for last line
                System.out.println("Error line "+(4+numRoutes)+" Reason: Missing start and end cities.");
                return;
            }
            String[] lastLine = sc.nextLine().trim().split("\\s+");
            if (lastLine.length != 2) {
                System.out.println("Error line "+(4+numRoutes)+ "Reason: Input file structure.");
                return;
            }
            String startCity = lastLine[0];
            String endCity = lastLine[1];

            if (getIndex(cities, startCity) == -1 || getIndex(cities, endCity) == -1) {
                System.out.println("Error line " + (4+numRoutes) + ": Invalid start or end city.");
                return;
            }
            while (sc.hasNextLine()) {
                if (!sc.nextLine().trim().isEmpty()) {
                    System.out.println("Error: Unexpected data after start and end cities.");
                    return;
                }
            }

            System.out.println("File read is successful!");

            WayFinder wayFinder = new WayFinder(cities, timeMatrix);
            wayFinder.findFastestRoute(startCity, endCity);


        } catch (NoSuchElementException e) {
            System.out.println("Error: Missing input data.");
        } catch (IOException e) {
            System.out.println("Error. Reason: Can not read the file.");
        }
    }
    public static int getIndex(City[] cities, String cityName){
        for (int i = 0; i < cities.length; i++) {
            if (cities[i].getCityName().equals(cityName)) {
                return cities[i].getIndex();
            }
        }
        return -1;
    }
}
