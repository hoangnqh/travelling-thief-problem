package algorithms;

import algorithms.pickingplan.ComparatorScore1;
import algorithms.pickingplan.FillSack;
import algorithms.tour.NearestNeighbor;
import models.Instance;
import models.ItemInTour;
import models.OtherFunction;
import models.Solution;

import java.util.List;
import java.util.Random;

public class SimpleHeuristic implements Algorithm{
    @Override
    public Solution solve(Instance instance, int randomSeed) {
        Random random = new Random(randomSeed);
        // Get a tour using a greedy algorithm
        int[] tour = NearestNeighbor.get(instance);
//        List<Integer> tour = LinKernighan.get(instance);
        if (tour == null) return new Solution();

        // Get a picking plan with Simple Heuristic/ Density Heuristic SH
        List<ItemInTour> itemInTourList = OtherFunction.getListItemInTour(instance, tour);
        itemInTourList.sort(new ComparatorScore1(instance));

        // stop execution if interrupted
        if (Thread.currentThread().isInterrupted()) return new Solution();

        boolean[] pickingPlan = FillSack.get(instance, tour, itemInTourList);
        return instance.evaluate(tour, pickingPlan, true);
    }
}
