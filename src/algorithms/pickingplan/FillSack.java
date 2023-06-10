package algorithms.pickingplan;

import models.Instance;
import models.ItemInTour;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class FillSack {
    public static boolean[] get(Instance instance, int[] tour, List<ItemInTour> itemInTourList){
        boolean[] pickingPlan = new boolean[instance.numOfItems];
        Arrays.fill(pickingPlan, false);

        long sumWeight = 0;
        for (int i = 0; i < itemInTourList.size(); i++){
            // stop execution if interrupted
            if (Thread.currentThread().isInterrupted()) return pickingPlan;
            if (sumWeight + itemInTourList.get(i).weight <= instance.capacity){
                double ZBefore = instance.evaluate(tour, pickingPlan, false).Z;
                pickingPlan[itemInTourList.get(i).pos] = true;
                double ZAfter = instance.evaluate(tour, pickingPlan, false).Z;
                if (ZAfter <= ZBefore){
                    pickingPlan[itemInTourList.get(i).pos] = false;;
                    continue;
                }
                sumWeight += itemInTourList.get(i).weight;
            }
        }
        return pickingPlan;
    }
}
