package models;

import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Instance {
    public String fileName = "unknown";
    public String problemName = "unknown";
    public String knapsackDataType = "unknown";
    public int numOfCities = -1;
    public int numOfItems = -1;
    public double minSpeed = -1;
    public double maxSpeed = -1;

    // capacity of the knapsack
    public long capacity = -1;

    // renting rate
    public double R = Double.POSITIVE_INFINITY;

    // coordinates of cities, (coordinates[i][0]
    // coordinates[i][0] is X of the city i, coordinates[i][0] is Y of the city i
    public double[][] coordinates;
    public int[][] dist = null;
    public int[] cityOfItem;
    public int[] weight;
    public int[] profit;
    public List<List<Integer>> itemsAtCity;

    // Read instance(input)
    public static Instance readInstance(File file) throws IOException {
        Instance instance = new Instance();
        instance.fileName = file.getName();
        FileReader fileReader = new FileReader(file);
        BufferedReader br = new BufferedReader(fileReader);
        String line = br.readLine();
        while(line != null){
            if (line.contains("PROBLEM NAME")) {
                line = line.substring(line.indexOf(":") + 1);
                instance.problemName = line.trim();
            } else if (line.contains("KNAPSACK DATA TYPE")) {
                line = line.substring(line.indexOf(":") + 1);
                instance.knapsackDataType = line.trim();
            } else if (line.contains("DIMENSION")) {
                instance.numOfCities = Integer.parseInt(line.split(":")[1].trim());
                instance.coordinates = new double[instance.numOfCities][2];
            } else if (line.contains("NUMBER OF ITEMS")) {
                instance.numOfItems = Integer.parseInt(line.split(":")[1].trim());
                instance.cityOfItem = new int[instance.numOfItems];
                instance.weight = new int[instance.numOfItems];
                instance.profit = new int[instance.numOfItems];
            } else if (line.contains("RENTING RATIO")) {
                instance.R = Double.parseDouble(line.split(":")[1].trim());
            } else if (line.contains("CAPACITY OF KNAPSACK")) {
                instance.capacity = Integer.parseInt(line.split(":")[1].trim());
            } else if (line.contains("MIN SPEED")) {
                instance.minSpeed = Double.parseDouble(line.split(":")[1].trim());
            } else if (line.contains("MAX SPEED")) {
                instance.maxSpeed = Double.parseDouble(line.split(":")[1].trim());
            } else if (line.contains("EDGE_WEIGHT_TYPE")) {
                String edgeWeightType = line.split(":")[1].trim();
                if (!edgeWeightType.equals("CEIL_2D")) {
                    throw new RuntimeException("Only edge weight type of CEIL_2D supported.");
                }
            } else if (line.contains("NODE_COORD_SECTION")) {
                for (int i = 0; i < instance.numOfCities; i++) {
                    line = br.readLine();
                    String[] a = line.split("\\s+");
                    instance.coordinates[i][0] = Double.parseDouble(a[1].trim());
                    instance.coordinates[i][1] = Double.parseDouble(a[2].trim());
                }
                // distance matrix
                if (instance.numOfCities <= 10000) {
                    instance.dist = new int[instance.numOfCities][instance.numOfCities];
                    for (int i = 0; i < instance.numOfCities; i++) {
                        for (int j = 0; j < instance.numOfCities; j++) {
                            instance.dist[i][j] = (int) Math.ceil(Math.sqrt(
                                    Math.pow(instance.coordinates[i][0] - instance.coordinates[j][0], 2)
                                            + Math.pow(instance.coordinates[i][1] -instance.coordinates[j][1], 2)));
                        }
                    }
                }
            } else if (line.contains("ITEMS SECTION")) {
                for (int i = 0; i < instance.numOfItems; i++) {
                    line = br.readLine();
                    String[] a = line.split("\\s+");
                    instance.profit[i] = Integer.parseInt(a[1].trim());
                    instance.weight[i] = Integer.parseInt(a[2].trim());
                    instance.cityOfItem[i] = Integer.parseInt(a[3].trim()) - 1;
                }
            }
            line = br.readLine();
        }
        br.close();

        // make the checks to avoid wrong parameters for the problem
        if (instance.numOfCities == -1 || instance.numOfItems == -1
                || instance.minSpeed == -1 || instance.maxSpeed == -1
                || instance.capacity == -1 || instance.R == Double.POSITIVE_INFINITY)
            throw new RuntimeException("Error while loading problem. Some variables are not initialized");

        // initialize the itemsAtCity data structure
        instance.itemsAtCity = new ArrayList<>(instance.numOfCities);
        for (int i = 0; i < instance.numOfCities; i++) {
            instance.itemsAtCity.add(new ArrayList<>());
        }
        for (int i = 0; i < instance.cityOfItem.length; i++) {
            instance.itemsAtCity.get(instance.cityOfItem[i]).add(i);
        }
        return instance;
    }
    public static Instance readInstance(String filePath) throws IOException {
        String ttpData = ConfigHelper.getProperty("ttpdata");
        File file = new File(ttpData+filePath);
        return readInstance(file);
    }


    // It has been cross-referenced with the objective function of https://github.com/yafrani/ttplab
    public Solution evaluate(int[] tour, boolean[] pickingPlan, boolean copy){
        if (tour.length != this.numOfCities || pickingPlan.length != this.numOfItems) {
            throw new RuntimeException("Wrong input for traveling thief evaluation!");
        } else if(tour[0] != 0) {
            throw new RuntimeException("Thief must start at city 0!");
        }
        int[] cntCity = new int[tour.length];
        for (int i = 0; i < tour.length; i++) {
            cntCity[tour[i]] += 1;
        }
        if (cntCity[0] > 2) throw new RuntimeException("The first city repeats more than 2 times");
        for (int i = 1; i < this.numOfCities; i++) {
            if (cntCity[i] > 1) throw new RuntimeException("The city "+i+" repeats more than 1 time");
        }

        Solution solution = new Solution();
        if (copy){
            solution.tour = Arrays.copyOf(tour, tour.length);
            solution.pickingPlan = Arrays.copyOf(pickingPlan, pickingPlan.length);
        }
        else{
            solution.tour = tour;
            solution.pickingPlan = pickingPlan;
        }

        long weight = 0;
        double time = 0;
        long profit = 0;
        int[] cityOrder = new int[this.numOfCities];
        double[] timeAcc = new double[this.numOfCities];
        long[] weightAcc = new long[this.numOfCities];

        // iterate over all possible cities
        for (int i = 0; i < this.numOfCities; i++) {
            // the city where the thief currently is
            int city = tour[i];
            // for each item index this city
            for (int j : this.itemsAtCity.get(city)) {
                // if the thief picks that item
                if (pickingPlan[j]) {
                    // update the current weight and profit
                    weight += this.weight[j];
                    profit += this.profit[j];
                }
            }
            // if the maximum capacity constraint is reached
            if (weight > this.capacity) {
                return solution;
            }
            // update the speed accordingly
            double speed = this.maxSpeed - ((double) weight / this.capacity) * (this.maxSpeed - this.minSpeed);
            // increase time by considering the speed - do not forget the way from the last city to the first!
            int next = tour[(i + 1) % this.numOfCities];
            cityOrder[city] = i;
            double distance = distance(city, next);
            time += distance / speed;
            timeAcc[i] = time;
            weightAcc[i] = weight;
        }
        solution.profit = profit;
        solution.time = time;
        solution.Z = profit - this.R * time;

        solution.remainingCapacity = capacity - weight;
        solution.cityOrder = cityOrder;
        solution.timeAcc = timeAcc;
        solution.weightAcc = weightAcc;
        return solution;
    }

    public Solution evaluate(int[] tour, boolean[] pickingPlan){
        return evaluate(tour, pickingPlan, false);
    }

    public int distance(int i, int j) {
//        System.out.println(i+", "+j+", "+numOfCities);
        if (dist == null){
            return (int) Math.ceil(Math.sqrt(Math.pow(coordinates[i][0] - coordinates[j][0], 2)
                            + Math.pow(coordinates[i][1] - coordinates[j][1], 2)));
        }
        else return dist[i][j];
    }

}