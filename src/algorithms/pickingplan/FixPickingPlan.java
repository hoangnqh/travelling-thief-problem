package algorithms.pickingplan;

import models.Instance;
import models.ItemInTour;
import models.OtherFunction;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class FixPickingPlan {

    public static void fix(Instance instance, int[] tour, boolean[] pickingPlan){
        List<ItemInTour> itemInTourList = OtherFunction.getListItemInTour(instance, tour, pickingPlan);
        itemInTourList.sort(new ItemInTourComparator(1.4));
        // stop execution if interrupted
        if (Thread.currentThread().isInterrupted()){
            Arrays.fill(pickingPlan, false);
            return;
        }
        long sumWeight = 0;
        for (ItemInTour inTour : itemInTourList) {
            sumWeight += inTour.weight;
        }
        for (ItemInTour itemInTour : itemInTourList) {
            if (sumWeight <= instance.capacity) break;
            pickingPlan[itemInTour.pos] = false;
            sumWeight -= itemInTour.weight;
        }
    }
}
