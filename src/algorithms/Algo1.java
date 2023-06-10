package algorithms;

import algorithms.pickingplan.BitFlip;
import algorithms.pickingplan.FixPickingPlan;
import algorithms.tour.LinKernighan;
import algorithms.tour.NearestNeighbor;
import models.Instance;
import models.Solution;

import java.util.Random;

public class Algo1 implements Algorithm{
    @Override
    public Solution solve(Instance instance, int randomSeed) {
        Random random = new Random(randomSeed);
        // Get a tour using a greedy algorithm
        int[] tour = NearestNeighbor.get(instance);
//        int[] tour = LinKernighan.get(instance);
        if (tour == null) return new Solution();

        // Get a pickingPlan using a greedy algorithm
        boolean[] pickingPlan = new boolean[instance.numOfItems];
        for (int i = 0; i < instance.numOfItems; i++){
            pickingPlan[i] = true;
        }
        FixPickingPlan.fix(instance, tour, pickingPlan);
        BitFlip.optimize(instance, tour, pickingPlan, random);

        return instance.evaluate(tour, pickingPlan, true);
    }
}
