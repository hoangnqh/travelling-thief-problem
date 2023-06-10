package algorithms.tour;

import models.Instance;
import java.util.Set;
import java.util.HashSet;

public class NearestNeighbor {
    public static int[] get(Instance instance) {

        /* the tour generated using greedy algorithm */
        int cnt = 0;
        int[] tour = new int[instance.numOfCities];

        Set<Integer> set = new HashSet<>();
        for (int i = 0; i < instance.numOfCities; i++){
            set.add(i);
        }

        tour[cnt++] = 0;
        set.remove(0);

        while (set.size() > 0){
            // stop execution if interrupted
            if (Thread.currentThread().isInterrupted()) return null;
            long minValue = Long.MAX_VALUE;
            int city = 0;
            for (int j: set){
                int dis = instance.distance(tour[cnt - 1], j);
                if (minValue >= dis) {
                    minValue = dis;
                    city = j;
                }
            }
            tour[cnt++] = city;
            set.remove(city);
        }
        return tour;
    }
}
