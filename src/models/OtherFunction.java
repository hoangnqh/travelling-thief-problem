package models;

import java.util.*;

import static java.util.Collections.shuffle;

public class OtherFunction {

    // Similar to generate in Python
    public static List<Integer> generateRange(int low, int high) {
        List<Integer> l = new ArrayList<>();
        for (int j = low; j < high; j++) {
            l.add(j);
        }
        return l;
    }

    public static int randInt(int l, int r, Random random){
        return random.nextInt(r - l + 1) + l;
    }

    // Similar to random_sample in Python
    public static List<Integer> randomSample(List<Integer> a, int k, Random random){
        List<Integer> sample = new ArrayList<>();
        Set<Integer> set = new HashSet<>();
        while (sample.size() < k){
            int index = random.nextInt(a.size());
            if (!set.contains(index)){
                set.add(index);
                sample.add(a.get(index));
            }
        }
        return sample;
    }

    public static long[] getDistanceFromFirstCity(Instance instance, int[] tour){
        // distances[i] is distance from city i to last city (city 0)
        long[] distances = new long[tour.length];
        for (int i = tour.length - 1; i >= 0; i--){
            int city = tour[i];
            int next = tour[(i + 1) % tour.length];
            distances[city] = distances[next] + instance.distance(city, next);
        }
        return distances;
    }

    public static List<ItemInTour> getListItemInTour(Instance instance, int[] tour, boolean[] pickingPlan){
        long[] distances = getDistanceFromFirstCity(instance, tour);
        List<ItemInTour> itemInTourList = new ArrayList<>();
        for (int i = 0; i < tour.length; i++){
            // stop execution if interrupted
            if (Thread.currentThread().isInterrupted()) return itemInTourList;

            int city = tour[i];
            for (int item: instance.itemsAtCity.get(city)){
                if(pickingPlan[item]){
                    ItemInTour itemInTour = new ItemInTour(item, instance.profit[item], instance.weight[item], distances[city]);
                    itemInTourList.add(itemInTour);
                }
            }
        }
        return itemInTourList;
    }

    public static List<ItemInTour> getListItemInTour(Instance instance, int[] tour){
        boolean[] pickingPlan = new boolean[instance.numOfItems];
        Arrays.fill(pickingPlan, true);
        return getListItemInTour(instance, tour, pickingPlan);
    }

}